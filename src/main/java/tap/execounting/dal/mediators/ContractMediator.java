package tap.execounting.dal.mediators;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.dal.QueryParameters;
import tap.execounting.dal.mediators.interfaces.ClientMed;
import tap.execounting.dal.mediators.interfaces.ContractMed;
import tap.execounting.dal.mediators.interfaces.DateFilter;
import tap.execounting.dal.mediators.interfaces.EventMed;
import tap.execounting.dal.mediators.interfaces.PaymentMed;
import tap.execounting.data.Const;
import tap.execounting.data.ContractState;
import tap.execounting.data.EventState;
import tap.execounting.entities.Client;
import tap.execounting.entities.Contract;
import tap.execounting.entities.ContractType;
import tap.execounting.entities.Event;
import tap.execounting.entities.EventType;
import tap.execounting.entities.Facility;
import tap.execounting.entities.Payment;
import tap.execounting.entities.Teacher;
import tap.execounting.services.DateService;

public class ContractMediator implements ContractMed {

	@Inject
	private DateFilter dateFilter;

	@Inject
	private EventMed eventMed;

	@Inject
	private PaymentMed paymentMed;

	@Inject
	private ClientMed clientMed;

	@Inject
	private CRUDServiceDAO dao;

	private Contract unit;

	private CRUDServiceDAO getDao() {
		return dao;
	}

	private PaymentMed getPaymentMed() {
		return paymentMed;
	}

	public Contract getUnit() {
		try {
			return unit;
		} catch (NullPointerException npe) {
			return null;
		}
	}

	public ContractMed setUnit(Contract unit) {
		this.unit = unit;
		return this;
	}

	public ContractMed setUnitId(int id) {
		unit = dao.find(Contract.class, id);
		return this;
	}

	public String getTeacherName() {
		try {
			String name;
			// way1
			name = unit.getTeacher().getName();
			// way2
			// name = dao.find(Teacher.class, unit.getTeacherId()).getName();
			return name;
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getClientName() {
		return unit.getClient().getName();
	}

	public EventType getEventType() {
		try {
			EventType et;
			// way1
			et = unit.getEventType();
			// way2
			// et = dao.find(EventType.class, unit.getTypeId());
			return et;
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public ContractType getContractType() {
		try {
			ContractType ct;
			// way1
			ct = unit.getContractType();
			// way2
			// ct = dao.find(ContractType.class, unit.getContractTypeId());
			return ct;
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Date getDate() {
		try {
			return unit.getDate();
		} catch (NullPointerException e) {
			e.printStackTrace();
			return null;
		}
	}

	public int getLessonsNumber() {
		try {
			return unit.getLessonsNumber();
		} catch (NullPointerException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public int getPrice() {
		try {
			return unit.getMoney();
		} catch (NullPointerException e) {
			e.printStackTrace();
			return 0;
		}

	}

	public ContractState getContractState() {
		return unit.getState();
	}

	public int getBalance() {
		try {
			return unit.getBalance();
		} catch (NullPointerException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public List<Event> getEvents() {
		try {
			List<Event> events;
			events = unit.getEvents();
			return events;
		} catch (NullPointerException e) {
			e.printStackTrace();
			return null;
		}
	}

	public int getRemainingLessons() {
		return unit.getLessonsRemain();
	}

	public List<Payment> getPayments() {
		try {
			return unit.getPayments();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public EventType loadEventType(int id) {
		return dao.find(EventType.class, id);
	}

	/**
	 * This method does all the planning activity for single contract. It plans
	 * all available events, corresponding to the contract event schedule, which
	 * corresponds to the teacher schedule.
	 * Today (16 jan 2013) I have added contract type check.
	 * If contract is free from teacher or it is free from school.
	 */
	
	// TODO include freeze period
	public void doPlanEvents(Date dateOfFirstEvent) {
		CRUDServiceDAO dao = getDao();
		unit = dao.find(Contract.class, unit.getId());
		doRemovePlannedEvents();

		int remain = getRemainingLessons();
		Calendar date = DateService.getMoscowCalendar();

		// From 18.12.12 we have date of first event for planning
		if (dateOfFirstEvent != null)
			date.setTime(dateOfFirstEvent);
		else
			date.setTime(DateService.trimToDate(new Date()));

		int remain1 = remain;
		int count = 0;
		byte contractTypeId = (byte) unit.getContractType().getId();
		while (remain > 0) {
			if (count == 9 && remain1 == remain)
				break;
			int dow = DateService.dayOfWeekRus(date.getTime());
			if (unit.getSchedule().get(dow)) {
				Event e = new Event();
				e.getContracts().add(unit);
				e.setHostId(unit.getTeacherId());
				e.setFacilityId(unit.getTeacher().getScheduleDay(dow));
				e.setRoomId(dao.find(Facility.class, e.getFacilityId())
						.getRooms().get(0).getRoomId());
				e.setState(EventState.planned);
				e.setTypeId(unit.getTypeId());
				e.setDate(date.getTime());
				
				if(contractTypeId == ContractType.FreeFromSchool)
					e.setFree(Event.FREE_FROM_SCHOOL);
				else if(contractTypeId == ContractType.FreeFromTeacher)
					e.setFree(Event.FREE_FROM_TEACHER);
				dao.create(e);
				remain--;
			}
			count++;
			date.add(Calendar.DAY_OF_WEEK, 1);
		}
	}

	// When client is no longer doing any study, he is gone, and all his money
	// remain on the school account. So we should write them as profit, but we
	// don't share them with teacher.
	// Today (20.10.2012) the most simple and consistent way to this is:
	// 1) Delete all planned events.
	// 2) Add new event with 'Writeoff' header, whose price is equal to the
	// balance of the contract.
	// 3) Make a comment '$N events were written off, $WriteoffDate.
	public void doWriteOff() {

		// TODO WRITEOFF
		// Delete all planned events
		doRemovePlannedEvents();

		// 'Writeoff' events
		EventType writeOffEventType = loadWriteOffType();

		Event e = new Event();
		e.setTypeId(writeOffEventType.getId());
		e.setHostId(Const.WriteOffTeacherId);
		e.setState(EventState.complete);
		e.setComment("[" + writeOffEventType.getTitle() + "]");
		e.setFacilityId(Const.WriteOffFacilityId);
		e.setRoomId(Const.WriteOffRoomId);
		dao.create(e);
		e.getContracts().add(unit);
		dao.update(e);
		unit.setCanceled(true);
		unit.getEvents().add(e);

		// Comment
		String comment = "Списано " + writeOffEventType.getPrice() + " р.";

		if (unit.getComment() != null)
			comment = unit.getComment().concat(comment);
		unit.setComment(comment);
		dao.update(unit);
	}

	private EventType loadWriteOffType() {
		String title = Const.WriteOffPrefix + " : " + unit.getBalance();
		EventType writeOff = dao.findUniqueWithNamedQuery(EventType.WITH_TITLE,
				QueryParameters.with("title", title).parameters());
		if (writeOff == null) {
			writeOff = new EventType();
			writeOff.setPrice(unit.getBalance());
			writeOff.setShareTeacher(0);
			writeOff.setTitle(title);
			writeOff.setDeleted(true);
			dao.create(writeOff);
		}

		return writeOff;
	}
	
	// Goal of this is to return all the remaining money - (complete cost of the contract) * 0.15
	// To the client
	// This is done through the special event which sends money to the
	// nonexisting teacher, and sends remaining to the school.
	public void doMoneyback() throws Exception {
		String exceptionMessage = "Баланс на договоре клиента, меньше 15% от стоимости договора. Операция возврата невозможна, попробуйте списание.";
		// Amount which school will receive is 15%, so check, that it exists.
		int completeCost = unit.getMoney();
		int schoolShare = (completeCost * 15) / 100;
		int balance = unit.getBalance();
		if(balance < schoolShare)
			throw new Exception(exceptionMessage);
		// Remove all planned events
		doRemovePlannedEvents();
		int clientShare = balance - schoolShare;
		EventType type = loadMoneybackType(schoolShare, clientShare);
		Event e = new Event();
		e.setTypeId(type.getId());
		e.setHostId(Const.WriteOffTeacherId);
		e.setState(EventState.complete);
		e.setComment("[" + type.getTitle() + "]");
		e.setFacilityId(Const.WriteOffFacilityId);
		e.setRoomId(Const.WriteOffRoomId);
		dao.create(e);
		e.getContracts().add(unit);
		dao.update(e);
		unit.setCanceled(true);
		unit.getEvents().add(e);

		// Comment
		String comment = "Школе: " + type.getSchoolMoney() + " р." + "\n" +
		"Клиенту: " + type.getShareTeacher() + " р.";

		if (unit.hasComment())
			comment = unit.getComment().concat(comment);
		unit.setComment(comment);
		dao.update(unit);
	}
	
	private EventType loadMoneybackType(int schoolShare, int clientShare) {
		String title = Const.MoneybackPrefix + " : " + clientShare + "|" + schoolShare;
		EventType moneyback = dao.findUniqueWithNamedQuery(EventType.WITH_TITLE,
				QueryParameters.with("title", title).parameters());
		if (moneyback == null) {
			moneyback = new EventType();
			moneyback.setPrice(unit.getBalance());
			moneyback.setShareTeacher(clientShare);
			moneyback.setTitle(title);
			moneyback.setDeleted(true);
			dao.create(moneyback);
		}

		return moneyback;
	}
	
	public ContractMed doFreeze(Date freeze, Date unfreeze){
		doRemovePlannedEvents();
		unit.setDateFreeze(freeze);
		unit.setDateUnfreeze(unfreeze);
        dao.update(unit);
        return this;
	}

    public ContractMed doUnfreeze(){
        unit.setDateFreeze(null);
        unit.setDateUnfreeze(null);
        update();
        doPlanEvents(new Date());
        update();
        return this;
    }
    private void update(){
        dao.update(unit);
    }

	private void doRemovePlannedEvents() {
		// Delete all planned events
		List<Event> events = unit.getEvents();
		for (int i = events.size() - 1; i >= 0; i--)
			if (events.get(i).getState() == EventState.planned)
				removeEvent(events.get(i));
	}
	/**
	 * Removes element from contract with care.
	 * First removes contract from event.
	 * Updates event.
	 * Then removes event from contract.
	 * Then deletes events, if there is no other contracts.
	 * Updates contract.
	 * @param e -- event to remove
	 */
	private void removeEvent(Event e){
		List<Contract> eventContracts = e.getContracts();
		for (int j = eventContracts.size() - 1; j >= 0; j--)
			if (eventContracts.get(j).getId() == unit.getId())
				eventContracts.remove(j);
		dao.update(e);
		for(int i = unit.getEvents().size() - 1; i >= 0; i--)
			if(e.getId() == unit.getEvents().get(i).getId()) {
				unit.getEvents().remove(i);
				break;
			}
		
		if (e.getContracts().size() == 0)
			dao.delete(Event.class, e.getId());
		
		dao.update(unit);
	}

	// group methods

	private List<Contract> cache;
	private Map<String, Object> appliedFilters;

	private Map<String, Object> getAppliedFilters() {
		if (appliedFilters == null)
			appliedFilters = new HashMap<String, Object>(5);
		return appliedFilters;
	}

	public List<Contract> getGroup() {
		if (cache == null)
			load();
		return cache;
	}

	public List<Contract> getGroup(boolean reset) {
		List<Contract> innerCache = getGroup();
		if (reset)
			reset();
		return innerCache;
	}

	private void load() {
		cache = getDao().findWithNamedQuery(Contract.ALL);
		appliedFilters = new HashMap<String, Object>();
	}

	public ContractMed setGroup(List<Contract> group) {
		cache = group;
		return this;
	}

	public ContractMed setGroupFromClients(List<Client> clients) {
		this.cache = new ArrayList<Contract>();

		for (Client c : clients)
			this.cache.addAll(c.getContracts());

		return this;
	}

	public List<Client> getClients() {
		return clientMed.contractsToClients(getGroup());
	}

	public List<Contract> getAllContracts() {
		return getDao().findWithNamedQuery(Contract.ALL);
	}

	public void reset() {
		cache = null;
		appliedFilters = null;
	}

	// intersection operation
	public ContractMed retain(List<Contract> contracts) {
		List<Contract> cache = getGroup();
		boolean found;
		// use this to store cached element id
		int ci;
		for (int i = cache.size() - 1; i >= 0; i--) {
			// Get id of i-th element and start search in contracts
			found = false;
			ci = cache.get(i).getId();
			for (int j = 0; j < contracts.size(); j++) {
				if (ci == contracts.get(j).getId()) {
					// If found -- break
					found = true;
					break;
				}
			}
			// Remove element with id ci from cache, if it is not found in contracts
			if (!found)
				cache.remove(i);
		}
		return this;
	}

	public String getFilterState() {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, Object> entry : getAppliedFilters().entrySet())
			sb.append(entry.getKey() + ": " + entry.getValue().toString()
					+ "\n");
		return sb.toString();
	}

	public ContractMed filter(Client c) {
		getAppliedFilters().put("Client", c);
		if (cache == null)
			cache = getDao().findWithNamedQuery(Contract.WITH_CLIENT,
					QueryParameters.with("clientId", c.getId()).parameters());
		else {
			List<Contract> cache = getGroup();
			Contract con;

			for (int i = cache.size() - 1; i >= 0; i--) {
				con = cache.get(i);
				if (con.getClientId() == c.getId())
					continue;
				cache.remove(i);
			}
		}
		return this;
	}

	public ContractMed filter(Teacher t) {
		getAppliedFilters().put("Teacher", t);
		if (cache == null)
			cache = getDao().findWithNamedQuery(Contract.WITH_TEACHER,
					QueryParameters.with("teacherId", t.getId()).parameters());
		else {
			List<Contract> cache = getGroup();
			Contract con;
			for (int i = cache.size() - 1; i >= 0; i--) {
				con = cache.get(i);
				if (con.getTeacherId() == t.getId())
					continue;
				cache.remove(i);
			}
		}
		return this;
	}

	public ContractMed filter(ContractState state) {
		getAppliedFilters().put("ContractState", state);
		List<Contract> cache = getGroup();

		// save current unit;
		Contract tempUnit = getUnit();

		Contract con;
		for (int i = cache.size() - 1; i >= 0; i--) {
			con = cache.get(i);
			setUnit(con);
			if (getContractState() == state)
				continue;
			else
				cache.remove(i);
		}

		// restore unit
		setUnit(tempUnit);
		return this;
	}

	public ContractMed filter(int remainingLessons) {
		getAppliedFilters().put("RemainingLessons", remainingLessons);
		List<Contract> cache = getGroup();
		Contract con;

		for (int i = cache.size() - 1; i >= 0; i--) {
			con = cache.get(i);
			if (con.getLessonsRemain() <= remainingLessons)
				continue;
			cache.remove(i);
		}
		return this;
	}

	public ContractMed filter(Date date1, Date date2) {
		getAppliedFilters().put("Date1", date1);
		getAppliedFilters().put("Date2", date2);

		List<Contract> cache = getGroup();
		dateFilter.filter(cache, date1, date2);
		return this;
	}

	public ContractMed filterByPlannedPaymentsDate(Date date1, Date date2) {
		getAppliedFilters().put("PlannedPaymentsDate1", date1);
		getAppliedFilters().put("PlannedPaymentsDate2", date2);
		List<Contract> cache = getGroup();
		Contract con;

		for (int i = cache.size() - 1; i >= 0; i--) {
			con = cache.get(i);
			if (plannedPaymentsTest(con, date1, date2))
				continue;
			cache.remove(i);
		}
		return this;
	}

	private boolean plannedPaymentsTest(Contract con, Date date1, Date date2) {
		PaymentMed paymentMed = getPaymentMed();
		boolean result = paymentMed.filter(con).filter(true)
				.filter(date1, date2).getGroup().size() > 0;
		paymentMed.reset();
		return result;
	}

	public ContractMed filterByContractType(int type) {
		getAppliedFilters().put("ContractTypeId", type);
		List<Contract> cache = getGroup();
		Contract con;

		for (int i = cache.size() - 1; i >= 0; i--) {
			con = cache.get(i);
			if (con.getContractTypeId() == type)
				continue;
			cache.remove(i);
		}
		return this;
	}

	public ContractMed removeComlete() {
		getAppliedFilters().put("Complete", false);
		List<Contract> cache = getGroup();
		for (int i = cache.size() - 1; i >= 0; i--)
			if (cache.get(i).isComplete())
				cache.remove(i);
		return this;
	}

	public Integer countGroupSize() {
		try {
			return cache.size();
		} catch (NullPointerException npe) {
			return null;
		}
	}

	public int countNotTrial() {
		int count = 0;
		for (int i = 0; i < cache.size(); i++)
			if (cache.get(i).notTrial())
				count++;
		return count;
	}
	
	public int countCertificateMoney() {
		int summ =0 ;
		for(Contract c : getGroup())
			summ+=c.getGiftMoney();
		return summ;
	}

	// Now it this does not filter anything
	public Integer count(ContractState state) {
		int count = 0;
		for (Contract c : getGroup())
			if (c.getState() == state)
				count++;
		return count;
	}

	public Integer countCompleteLessonsMoney() {
		return unit.getCompleteLessonsCost();
	}

	public Integer countMoneyPaid() {
		return unit.getMoneyPaid();
	}
	
	// Sorting
	public ContractMed sortByDate(boolean asc){
		Collections.sort(getGroup());
		if(asc)
			Collections.reverse(getGroup());
		return this;
	}

}
