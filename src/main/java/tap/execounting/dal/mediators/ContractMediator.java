package tap.execounting.dal.mediators;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.dal.QueryParameters;
import tap.execounting.dal.mediators.interfaces.ContractMed;
import tap.execounting.dal.mediators.interfaces.DateFilter;
import tap.execounting.dal.mediators.interfaces.EventMed;
import tap.execounting.dal.mediators.interfaces.PaymentMed;
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
	private PaymentMed surePaymentMed;

	@Inject
	private CrudServiceDAO dao;
	private CrudServiceDAO sureDao;

	private Contract unit;

	public ContractMed setDao(CrudServiceDAO dao) {
		this.sureDao = dao;
		return this;
	}

	private CrudServiceDAO getDao() {
		return dao == null ? sureDao : dao;
	}

	private PaymentMed getPaymentMed() {
		if (paymentMed == null) {
			surePaymentMed = new PaymentMediator();
			surePaymentMed.setDao(getDao());
			return surePaymentMed;
		} else
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

	public void planEvents() {
		CrudServiceDAO dao = getDao();
		unit = dao.find(Contract.class, unit.getId());
		for (Event e : unit.getEvents())
			if (e.getState() == EventState.planned)
				dao.delete(Event.class, e.getId());

		int remain = getRemainingLessons();
		Calendar date = DateService.getMoscowCalendar();
		date.setTime(DateService.trimToDate(new Date()));

		while (remain > 0) {
			int dow = date.get(Calendar.DAY_OF_WEEK);
			if (unit.getSchedule().get(dow)) {
				Event e = new Event();
				e.getContracts().add(unit);
				e.setHostId(unit.getTeacherId());
				e.setFacilityId(unit.getTeacher().getScheduleDay(dow));
				e.setRoomId(dao.find(Facility.class, e.getFacilityId())
						.getRooms().get(0).getRoomId());
				e.setState(EventState.planned);
				e.setTypeId(unit.getEventType().getId());
				e.setDate(date.getTime());
				dao.create(e);
				remain--;
			}
			date.add(Calendar.DAY_OF_WEEK, 1);
		}
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

	public List<Contract> getAllContracts() {
		return getDao().findWithNamedQuery(Contract.ALL);
	}

	public void reset() {
		cache = null;
		appliedFilters = null;
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
		dateFilter.filterWithReturn(cache, date1, date2);
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

	public ContractMediator filterByContractType(int type) {
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

	public Integer countGroupSize() {
		try {
			return cache.size();
		} catch (NullPointerException npe) {
			return null;
		}
	}

	public Integer count(ContractState state) {
		return filter(state).countGroupSize();
	}

	public Integer countCompleteLessonsMoney() {
		return unit.getCompleteLessonsCost();
	}

	public Integer countMoneyPaid() {
		return unit.getMoneyPaid();
	}

}
