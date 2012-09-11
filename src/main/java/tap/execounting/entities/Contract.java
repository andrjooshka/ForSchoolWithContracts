package tap.execounting.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import tap.execounting.data.ContractState;
import tap.execounting.data.EventState;
import tap.execounting.entities.interfaces.Dated;
import tap.execounting.services.DateService;
import tap.execounting.services.RusCalendar;
import tap.execounting.services.SuperCalendar;

@Entity
@Table(name = "contracts")
@NamedQueries({
		@NamedQuery(name = Contract.ALL, query = "from Contract"),
		@NamedQuery(name = Contract.BY_DATES, query = "from Contract where date between "
				+ ":earlierDate and :laterDate"),
		@NamedQuery(name = Contract.WITH_TEACHER, query = "from Contract where teacherId = :teacherId"),
		@NamedQuery(name = Contract.WITH_CLIENT, query = "from Contract where clientId = :clientId") })
public class Contract implements Comparable<Contract>, Dated {

	public static final String ALL = "Contract.all";
	public static final String BY_DATES = "Contract.byDates";
	public static final String WITH_TEACHER = "Contract.withTeacher";
	public static final String WITH_CLIENT = "Contract.withClient";

	@Id
	@Column(name = "contract_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "client_id", unique = false)
	private int clientId;

	@ManyToOne
	@JoinColumn(name = "client_id", updatable = false, insertable = false)
	private Client client;

	private Date date;

	private boolean gift;

	private int discount;

	private boolean freeze;

	private boolean canceled;

	@Column(name = "contract_type_id", unique = false)
	private int contractTypeId;

	@OneToOne(optional = false)
	@JoinColumn(name = "contract_type_id", insertable = false, updatable = false)
	private ContractType contractType;

	@Column(name = "type_id", unique = false)
	private int typeId;

	@OneToOne(optional = false)
	@JoinColumn(name = "type_id", nullable = false, updatable = false, insertable = false)
	private EventType eventType;

	@Column(name = "lessons_number")
	private int lessonsNumber;

	@Column(name = "teacher_id", nullable = true, unique = false)
	private int teacherId;

	@OneToOne(optional = false)
	@JoinColumn(name = "teacher_id", nullable = false, updatable = false, insertable = false)
	private Teacher teacher;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "contract_id")
	private List<Payment> payments = new ArrayList<Payment>();

	@ManyToMany(mappedBy = "contracts")
	// @JoinTable(name = "events_contracts", joinColumns = { @JoinColumn(name =
	// "contract_id") }, inverseJoinColumns = { @JoinColumn(name = "event_id")
	// })
	private List<Event> events = new ArrayList<Event>();

	@OneToOne(cascade = CascadeType.ALL)
	private WeekSchedule schedule;

	public Contract() {
		setDate(DateService.trimToDate(new Date()));
		setContractTypeId(1);
	}

	// Unit properties and methods

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public WeekSchedule getSchedule() {
		if (schedule == null)
			schedule = new WeekSchedule();
		return schedule;
	}

	public void setSchedule(WeekSchedule schedule) {
		this.schedule = schedule;
	}

	public int getClientId() {
		return clientId;
	}

	public void setClientId(int clientId) {
		this.clientId = clientId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	/**
	 * @return returns written number of events in contract 
	 */
	public int getLessonsNumber() {
		return lessonsNumber;
	}

	public void setLessonsNumber(int lessonsNumber) {
		this.lessonsNumber = lessonsNumber;
	}

	public int getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(int teacherId) {
		this.teacherId = teacherId;
	}

	public boolean isGift() {
		return gift;
	}

	public void setGift(boolean gift) {
		this.gift = gift;
	}

	public int getGiftPrice() {
		return 300;
	}

	public int getDiscount() {
		return discount;
	}

	public void setDiscount(int dicount) {
		this.discount = dicount;
	}

	public boolean isFreeze() {
		return freeze;
	}

	public void setFreeze(boolean freeze) {
		this.freeze = freeze;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	public int getContractTypeId() {
		return contractTypeId;
	}

	public void setContractTypeId(int contractTypeId) {
		this.contractTypeId = contractTypeId;
	}

	public List<Payment> getPayments() {
		return payments;
	}

	public void setPayments(List<Payment> payments) {
		this.payments = payments;
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

	public Teacher getTeacher() {
		Teacher t;
		try {
			t = teacher;
		} catch (NullPointerException npe) {
			System.out.print("\n\nNPE at getTeacher, method will return null");
			t = null;
		} catch (Exception e) {
			System.out.println("\n\nException at getTeacher " + e.getMessage()
					+ ". Method will return null value");
			t = null;
		}
		return t;
	}

	public EventType getEventType() {
		return eventType;
	}

	public boolean isActive() {
		boolean active = !isComplete() && !isFreeze() && !isCanceled();
		return active;
	}

	public ContractState getState() {
		ContractState state = null;
		if (isCanceled())
			state = ContractState.canceled;
		else if (isFreeze())
			state = ContractState.frozen;
		else if (isComplete())
			state = ContractState.complete;
		else if (undefinedStateTest()) {
			state = ContractState.undefined;
		} else
			state = ContractState.active;

		return state;
	}

	private boolean undefinedStateTest() {
		try {
			boolean result = true;
			Date former = DateService.fromNowPlusDays(-360);
			for (Event e : getEvents())
				if (e.getDate().after(former)) {
					result = false;
					break;
				}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("\n\nContract:" + id + "\n\n");
			return false;
		}
	}

	/**
	 * @return full contract price
	 */
	public int getMoney() {
		int lessonCost = getEventType().getMoney();
		int lessons = getLessonsNumber();
		int total = lessonCost * lessons;
		total -= discount;
		if (isGift())
			total += getGiftPrice();
		return total;
	}

	// Lessons Event etc. methods
	public List<Event> getEventsCopied() {
		List<Event> list = new ArrayList<Event>();
		for (Event e : events)
			list.add(e.clone());
		return list;
	}

	public List<Event> getEvents(boolean asc) {
		List<Event> list = getEvents();
		Collections.sort(list);
		if (!asc)
			Collections.reverse(list);
		return list;
	}

	public List<Event> getFinishedEvents() {
		List<Event> events = new ArrayList<Event>();
		for (Event e : getEvents())
			if (e.getState() == EventState.complete)
				events.add(e);
		return events;
	}

	public int getSingleLessonCost() {
		int totalLessonsCost = lessonsNumber * eventType.getMoney() - discount;
		int singleLessonCost = totalLessonsCost / lessonsNumber;
		return singleLessonCost;
	}

	public int getCompleteLessonsCost() {
		int completeLessons = getCompleteLessonsNumber(true);
		int completeLessonsCost = completeLessons * getSingleLessonCost();
		return completeLessonsCost;
	}

	public int getCompleteLessonsNumber(boolean countFailedByClientAsComplete) {
		int count = 0;

		if (countFailedByClientAsComplete) {
			for (Event e : getEvents())
				if (e.getState() == EventState.complete
						|| e.getState() == EventState.failedByClient)
					count++;
		} else
			for (Event e : getEvents())
				if (e.getState() == EventState.complete)
					count++;

		return count;
	}

	public int getLessonsRemain() {
		int lessonsNumber = getLessonsNumber();
		int lessonsComplete = getCompleteLessonsNumber(true);
		int remaining = lessonsNumber - lessonsComplete;
		return remaining;
	}

	public List<Event> getScheduledLessons() {
		List<Event> events = new ArrayList<Event>();
		for (Event e : getEvents())
			if (e.getState() == EventState.planned)
				events.add(e);
		return events;
	}

	public List<Event> getEvents(Date d) {
		List<Event> res = new ArrayList<Event>();
		d = DateService.trimToDate(d);
		for (Event e : getEvents())
			if (DateService.trimToDate(e.getDate()).equals(d))
				res.add(e);
		return res;
	}

	// Money and Payments
	/**
	 * 
	 * @return сумма денег уплаченная клиентом по этому договору.
	 */
	public int getMoneyPaid() {
		int total = 0;
		for (Payment p : getPayments()) {
			if (!p.isScheduled()) {
				total += p.getAmount();
			}
		}
		if (isGift())
			total += getGiftPrice();
		return total;
	}

	public Client getClient() {
		return client;
	}

	public List<Payment> getPlannedPayments() {
		List<Payment> payments = new ArrayList<Payment>();
		for (Payment p : getPayments())
			if (p.isScheduled())
				payments.add(p);
		return payments;
	}

	public boolean isComplete() {
		return getLessonsRemain() <= 0;
	}

	public boolean isPaid() {
		return getMoneyPaid() >= getMoney();
	}

	public boolean isTrialLesson() {
		return lessonsNumber < 3;
	}

	public int getBalance() {
		return getMoneyPaid() - getCompleteLessonsCost()
				- (isGift() ? getGiftPrice() : 0);
	}

	public ContractType getContractType() {
		return contractType;
	}

	public List<Payment> getPlannedPayments(int days) {
		SuperCalendar calendar = new RusCalendar();
		calendar.addDays(days);
		List<Payment> list = getPlannedPayments();

		Payment p;
		for (int i = list.size() - 1; i >= 0; i--) {
			p = list.get(i);
			if (p.getDate().after(calendar.getTime()))
				list.remove(i);
		}
		return list;
	}

	// Util
	public int compareTo(Contract contract) {
		return getDate().compareTo(contract.getDate());
	}
}
