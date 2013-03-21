package tap.execounting.dal.mediators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.ChainMap;
import tap.execounting.dal.mediators.interfaces.DateFilter;
import tap.execounting.dal.mediators.interfaces.EventMed;
import tap.execounting.data.EventState;
import tap.execounting.entities.Client;
import tap.execounting.entities.Contract;
import tap.execounting.entities.Event;
import tap.execounting.entities.EventType;
import tap.execounting.entities.EventTypeAddition;
import tap.execounting.entities.Facility;
import tap.execounting.entities.Room;
import tap.execounting.entities.Teacher;
import tap.execounting.entities.WeekSchedule;
import tap.execounting.services.DateService;

public class EventMediator extends ProtoMediator<Event> implements EventMed {

	@Inject
	private DateFilter dateFilter;

    public EventMediator(){
        clazz = Event.class;
    }

    public EventMed setUnit(Event unit) {
		this.unit = unit;
		return this;
	}

	public Date getDate() {
		try {
			return unit.getDate();
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}
	}

	public Teacher getTeacher() {
		try {
			return dao.find(Teacher.class, unit.getHostId());
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}
	}

	public List<Client> getClients() {
		try {
			List<Client> clients = new ArrayList<Client>();
			for (Contract c : getContracts())
				clients.add(c.getClient());
			return clients;
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}
	}

	public EventType getEventType() {
		try {
			return unit.getEventType();
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}
	}

	public List<Contract> getContracts() {
		try {
			return unit.getContracts();
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}
	}

	public int getPrice() {
		return unit.getEventType().getPrice();
	}

	public EventState getState() {
		return unit.getState();
	}

	public String getComment() {
		try {
			return unit.getComment();
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}
	}

	public Facility getFacility() {
		try {
			return dao.find(Facility.class, unit.getFacilityId());
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}
	}

	public Room getRoom() {
		try {
			return dao.find(Room.class, unit.getRoomId());
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}
	}

	public EventType loadEventType(int id) {
		return dao.find(EventType.class, id);
	}

	public EventTypeAddition loadProbation(int id) {
		return dao.findUniqueWithNamedQuery(
				EventTypeAddition.PROBATION_BY_EVENT_TYPE_ID, ChainMap
						.with("eventTypeId", id).yo());
	}

	public void save(Event event) {
		if (event.getId() != 0)
			dao.update(event);
		else
			dao.create(event);
	}
	public void move(EventState newState, Date newDate, int transferType)
			throws IllegalAccessException {
		// Scheduled transfer type = 0
		if (transferType == 0) {
			// TODO group scheduled transfers
			// 1. Check that event has only one contract on it. Group events are
			// not for scheduled transfer yet.
			unit = dao.find(Event.class, unit.getId());
			if (unit.getContracts().size() > 1)
				throw new IllegalAccessException(
						"Перенос по расписанию, пока недоступен для групповых занятий");
			// 2. Check that contract have schedule.
			Contract con = unit.getContracts().get(0);
			if (!con.hasSchedule())
				throw new IllegalAccessException(
						"По данному договору расписания не найдено.");
			// 3. Find the latest event in contract even if it is current event.
			List<Event> events = con.getEvents(true);
			Event last = events.get(events.size() - 1);
			// 4. Get day of the week for the latest event, and increment that;
			newDate = DateService.datePlusDays(last.getDate(),1);
			int dow = DateService.dayOfWeekRus(newDate);
			
			WeekSchedule s = con.getSchedule();
			// 5. Get next day of the week in schedule for that contract.
			while (!s.get(dow)) {
				newDate = DateService.datePlusDays(newDate, 1);
				dow = DateService.dayOfWeekRus(newDate);
			}
			// 6. Set newDate.
			// 7. Move the event.
		}
		unit.move(newState, newDate);
		save(unit);
	}

	private Map<String, Object> appliedFilters;

	private Map<String, Object> getAppliedFilters() {
		if (appliedFilters == null)
			appliedFilters = new HashMap<String, Object>(5);
		return appliedFilters;
	}

	public List<Event> getGroup() {
		if (cache == null)
			cache = dao.findWithNamedQuery(Event.ALL);
		return cache;
	}

	public List<Event> getGroup(boolean reset) {
		List<Event> innerCache = getGroup();
		if (reset)
			reset();
		return innerCache;
	}

	public EventMed setGroup(List<Event> items) {
		cache = items;
		return this;
	}

	public List<Event> getAllEvents() {
		return dao.findWithNamedQuery(Event.ALL);
	}

	public void reset() {
		cache = null;
		appliedFilters = null;
	}

	public List<Event> getByDateClientIdTeacherIdAndEventTypeTitle(Date d,
			int clientId, int teacherId, String typeTitle) {
		Map<String, Object> params;
		List<Event> preliminary;
		params = ChainMap.with("date", d).n("teacherId", teacherId)
				.yo(); // cut .and("title", '%'+typeTitle+'%')
		preliminary = dao.findWithNamedQuery(Event.BY_DATE_TEACHERID_TITLE,
				params);

		// Filter by type title
		for (int j = preliminary.size() - 1; j >= -0; j--)
			if (!preliminary.get(j).getEventType().getTypeTitle()
					.equalsIgnoreCase(typeTitle))
				preliminary.remove(j);
		for (int j = preliminary.size() - 1; j >= 0; j--)
			if (!preliminary.get(j).containsClient(clientId))
				preliminary.remove(j);

		return preliminary;
	}

	public String getFilterState() {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, Object> entry : getAppliedFilters().entrySet())
			sb.append(entry.getKey() + ": " + entry.getValue().toString()
					+ "\n");
		return sb.toString();
	}

	public EventMed retainByClient(Client c) {
		List<Event> cache = getGroup();
		for (int i = countGroupSize() - 1; i >= 0; i--) {
			List<Contract> toCheck = cache.get(i).getContracts();
			boolean thereIsNoSuchClient = true;
			for (Contract con : toCheck) {
				if (con.getClientId() == c.getId()) {
					thereIsNoSuchClient = false;
					break;
				}
			}
			if (thereIsNoSuchClient)
				cache.remove(i);
		}

		getAppliedFilters().put("Teacher", unit);
		return this;
	}

	public EventMed retainByTeacher(Teacher unit) {
		if (cacheIsClean()) {
			cache = dao.findWithNamedQuery(
					Event.BY_TEACHER_ID,
					ChainMap.with("teacherId", unit.getId())
							.yo());

		} else {
			List<Event> cache = getGroup();
			for (int i = countGroupSize() - 1; i >= 0; i--)
				if (cache.get(i).getHostId() != unit.getId())
					cache.remove(i);
		}
		getAppliedFilters().put("Teacher", unit);
		return this;
	}

	public EventMed retainByContract(Contract unit) {
		if (cacheIsClean()) {
			getAppliedFilters().put("Contract", unit);
			setGroup(unit.getEventsCopied());
			return this;
		} else {
			getAppliedFilters().put("Contract", unit);
			List<Event> cache = getGroup();
			for (int i = countGroupSize() - 1; i >= 0; i--)
				evnt: {
					for (Contract c : cache.get(i).getContracts()) {
						if (c.getId() == unit.getId())
							break evnt;
						cache.remove(i);
					}
				}
		}
		return this;
	}

	public EventMed retainPaidEvents() {
		if (cacheIsClean()) {
			cache = dao.findWithNamedQuery(
					Event.BY_STATE,
					ChainMap.with("stateCode",
                            EventState.complete.getCode()).yo());
			List<Event> addition = dao.findWithNamedQuery(
					Event.BY_STATE,
					ChainMap.with("stateCode",
                            EventState.failedByClient.getCode()).yo());
			cache.addAll(addition);

		} else {
			List<Event> cache = getGroup();
			for (int i = countGroupSize() - 1; i >= 0; i--)
				if (cache.get(i).getState() != EventState.failedByClient
						&& cache.get(i).getState() != EventState.complete)
					cache.remove(i);
		}
		getAppliedFilters().put("EventState", "paid");
		return this;
	}

	public EventMed retainByState(EventState state) {
		// Added new conditions to count events moved by teacher and client
		if (state == EventState.movedByClient
				|| state == EventState.movedByTeacher) {
			List<Event> cache = getGroup();
			Event e;
			if (state == EventState.movedByClient) {
				for (int i = countGroupSize() - 1; i >= 0; i--) {
					e = cache.get(i);
					if (e.getState() == state
							|| (e.getComment() != null && e
									.getComment()
									.contains(
											EventState.movedByClient.toString())))
						continue;
					else
						cache.remove(i);
				}
			}
			if (state == EventState.movedByTeacher) {
				for (int i = countGroupSize() - 1; i >= 0; i--) {
					e = cache.get(i);
					if (e.getState() == state
							|| (e.getComment() != null && e.getComment()
									.contains(
											EventState.movedByTeacher
													.toString())))
						continue;
					else
						cache.remove(i);
				}
			}

		} else if (cacheIsClean()) {
			cache = dao.findWithNamedQuery(
					Event.BY_STATE,
					ChainMap.with("stateCode", state.getCode())
							.yo());
		} else {

			List<Event> cache = getGroup();
			for (int i = countGroupSize() - 1; i >= 0; i--)
				if (cache.get(i).getState() != state)
					cache.remove(i);
		}

		getAppliedFilters().put("EventState", state);
		return this;
	}

	public EventMed retainByFacility(Facility unit) {
		return retainByFacilityId(unit.getFacilityId());
	}

	public EventMed retainByRoom(Room unit) {
		if (cacheIsClean()) {
			cache = dao.findWithNamedQuery(
					Event.BY_ROOM_ID,
					ChainMap.with("roomId", unit.getRoomId())
							.yo());
		} else {

			List<Event> cache = getGroup();
			for (int i = countGroupSize() - 1; i >= 0; i--)
				if (cache.get(i).getRoomId() != unit.getRoomId())
					cache.remove(i);
		}
		getAppliedFilters().put("Room", unit);
		return this;
	}

	public EventMed retainByRoom(EventType type) {
		if (cacheIsClean()) {
			cache = dao.findWithNamedQuery(Event.BY_TYPE_ID,
					ChainMap.with("typeId", type.getId()).yo());
		} else {

			List<Event> cache = getGroup();
			for (int i = countGroupSize() - 1; i >= 0; i--)
				if (cache.get(i).getTypeId() != type.getId())
					cache.remove(i);
		}
		getAppliedFilters().put("EventType", type);
		return this;
	}

	public EventMed retainByDatesEntry(Date date1, Date date2) {
		if (cacheIsClean()) {
			if (date1 != null && date2 != null)
				cache = dao.findWithNamedQuery(
						Event.BETWEEN_DATE1_AND_DATE2,
						ChainMap.with("date1", date1)
								.n("date2", date2).yo());
			else if (date1 != null)
				cache = dao.findWithNamedQuery(Event.AFTER_DATE,
						ChainMap.with("date", date1).yo());
			else if (date2 != null)
				cache = dao.findWithNamedQuery(Event.BEFORE_DATE,
                        ChainMap.with("date", date2).yo());
		} else dateFilter.retainByDatesEntry(cache, date1, date2);
		getAppliedFilters().put("Date1", date1);
		getAppliedFilters().put("Date2", date2);

		return this;
	}

	public Integer countGroupSize() {
		try {
			return cache.size();
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}
	}

	public Integer count(EventState state) {
		return retainByState(state).countGroupSize();
	}

	public Integer countEventsComplete() {
		return count(EventState.complete);
	}

	public Integer countEventsFired() {
		return count(EventState.failedByClient);
	}

	public Integer countEventsMovedByClient() {
		return count(EventState.movedByClient);
	}

	public Integer countEventsMovedByTeacher() {
		return count(EventState.movedByTeacher);
	}

	public Integer countMoney() {
		if (cache == null)
			return null;
		int summ = 0;
		for (Event e : getGroup())
			summ += e.getMoney();
		return summ;
	}

	public Integer countMoneyOfCompleteEvents() {
		return retainByState(EventState.complete).countMoney();
	}

	public Integer countTeacherMoney() {
		int summ = 0;
		// TODO Tuleneva
		for (Event e : getGroup())
			summ += e.getTeacherMoney();
		return summ;
	}

	public Integer countSchoolMoney() {
		int summ = 0;
		for (Event e : getGroup())
			summ += e.getSchoolMoney();
		return summ;
	}

	public Integer countMoneyOfFailedEvents() {
		return countMoneyOfEventsFailedByClient()
				+ countMoneyOfEventsFailedByTeacher();
	}

	public Integer countMoneyOfEventsFailedByClient() {
		return retainByState(EventState.failedByClient).countMoney();
	}

	public Integer countMoneyOfEventsFailedByTeacher() {
		return retainByState(EventState.movedByTeacher).countMoney();
	}

	public Integer countGivenPercentOfMoney(int percent) {
		return countMoney() * 100 / percent;
	}

	public int countDaysInEventsGroup() {
		HashSet<Date> days = new HashSet<Date>();
		for (Event e : getGroup())
			days.add(DateService.trimToDate(e.getDate()));
		return days.size();
	}

	public EventMed sortByDate(boolean ascending) {
		Collections.sort(getGroup());
		if (!ascending)
			Collections.reverse(getGroup());

		return this;
	}

    public Event lastByDate(){
        getGroup();
        sortByDate(false);
        return cache.get(0);
    }

    public EventMed retainByEventTitleContaining(String typeId) {
        if(typeId == null) return this;
        getGroup();
        for (int i = cache.size() - 1; i >= 0; i--)
            if (!cache.get(i).getEventType().getTitle().contains(typeId))
                cache.remove(i);
        return this;
    }

    public EventMed retainByTeacherId(Integer teacherId) {
        if(teacherId == null) return this;
        getGroup();
        for (int i = cache.size() - 1; i >= 0; i--)
            if (cache.get(i).getHostId() != (teacherId))
                cache.remove(i);
        return this;
    }

    public EventMed retainByRoomId(Integer roomId) {
        if(roomId == null) return this;
        getGroup();
        for(int i = cache.size()-1;i>=0;i--)
            if(cache.get(i).getRoomId()!=roomId)
                cache.remove(i);
        return this;
    }

    public EventMed retainByStateCode(Integer state) {
        if(state == null) return this;
        retainByState(EventState.fromCode(state));
        return this;
    }

    public EventMed retainByFacilityId(Integer facilityId) {
        if(facilityId == null) return this;
        if (cacheIsClean()) {
            cache = dao.findWithNamedQuery(Event.BY_FACILITY_ID,
                    ChainMap.with("facilityId", facilityId)
                            .yo());
        } else {
            for (int i = countGroupSize() - 1; i >= 0; i--)
                if (cache.get(i).getFacilityId() != unit.getFacilityId())
                    cache.remove(i);
        }
        getAppliedFilters().put("Facility", facilityId);
        return this;
    }

    private boolean cacheIsClean() {
        return cache == null || appliedFilters == null
                || appliedFilters.size() == 0;
    }

}
