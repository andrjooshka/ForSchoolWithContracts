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
import javax.validation.constraints.Min;

import org.apache.tapestry5.beaneditor.NonVisual;

import tap.execounting.data.ContractState;
import tap.execounting.data.EventState;
import tap.execounting.entities.interfaces.Dated;
import tap.execounting.services.DateService;
import tap.execounting.services.RusCalendar;
import tap.execounting.services.SuperCalendar;

/**
 * This class does not support interface entities.interfaces.Deletable, since
 * some contracts certainly should be removed, and it is not an accounting item,
 * but accounting unit.
 * 
 * Contract represents physical contract between client and the school. Its role
 * - to group the events, and set the rules, how to calculate money. Contract
 * could have one of six types, each type set the rules how to calculate money.
 * For more info about ContractType, look ContractType.java Also if contract is
 * free for client -- then all planned events will be free. Free events will be
 * written off, from the free contracts, but for other types of contracts they
 * won't.
 * 
 * @author truth0
 * 
 */
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

	private int discount;

	private Date dateFreeze;

	private Date dateUnfreeze;

	private boolean canceled;

	/**
	 * If contract is certificate
	 */
	private boolean gift;

	private int giftMoney;

	private String comment;

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
	@Min(value = 1)
	private int lessonsNumber;

	@Column(name = "teacher_id", nullable = true, unique = false)
	private int teacherId;

	@OneToOne(optional = false)
	@JoinColumn(name = "teacher_id", nullable = false, updatable = false, insertable = false)
	private Teacher teacher;

	@OneToMany(fetch = FetchType.EAGER)
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

	/**
	 * @return Id of event type of that contract
	 */
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isGift() {
		return gift;
	}

	public void setGift(boolean gift) {
		this.gift = gift;
	}

	public int getDiscount() {
		return discount;
	}

	public void setDiscount(int dicount) {
		this.discount = dicount;
	}

	public boolean isFrozen() {
		Date d = new Date();
		return dateUnfreeze != null && dateFreeze != null
				&& d.before(dateUnfreeze) && d.after(dateFreeze);
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

	public void setEventType(EventType et) {
		this.eventType = et;
	}

	public boolean isActive() {
		boolean active = !isComplete() && !isFrozen() && !isCanceled();
		return active;
	}

	public ContractState getState() {
		// TODO hotfix alert
		// if (ContractType.Trial == contractTypeId)
		// return ContractState.active;
		ContractState state = null;
		if (isCanceled())
			state = ContractState.canceled;
		else if (isFrozen())
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
			// Check if contract is new
			Date neww = DateService.fromNowPlusDays(-30);
			if (neww.before(date))
				return false;

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
		int lessonCost = getEventType().getPrice();
		int lessons = getLessonsNumber();
		int total = lessonCost * lessons;
		total -= discount;
		total += getGiftMoney();
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
		if (lessonsNumber == 0)
			throw new IllegalArgumentException("Contract id: " + id);
		int totalLessonsCost = lessonsNumber * eventType.getPrice() - discount;
		int singleLessonCost = totalLessonsCost / lessonsNumber;
		return singleLessonCost;
	}

	public int getCompleteLessonsCost() {
		if (!notFree())
			return 0;
		int sum = 0;
		for (Event e : getEvents())
			// Count event only if it is either complete, or failed by client,
			// and it is not marked as free.
			if ((!e.isFree() && !e.isWriteOff())
					&& (e.getState() == EventState.complete || e.getState() == EventState.failedByClient))
				sum += e.getEventType().getPrice();
		return sum;
	}

	/**
	 * Does not counts the writeoffs; Also if contract is free for client --
	 * then all planned events will be free. Free events will be written off,
	 * from the free contracts, but for other types of contracts they won't.
	 * 
	 * @param countFailedByClientAsComplete
	 * @return
	 */
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

		for (Event e : getEvents())
			if (e.isWriteOff() || (notFree() && e.isFree()))
				count--;
		return count;
	}

	private boolean notFree() {
		byte contractTypeId = (byte) getContractType().getId();
		return contractTypeId != ContractType.FreeFromSchool
				&& contractTypeId != ContractType.FreeFromTeacher;
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
	 * @return сумма денег уплаченная клиентом по этому договору.
	 */
	public int getMoneyPaid() {
		int total = 0;
		for (Payment p : getPayments()) {
			if (!p.isScheduled()) {
				total += p.getAmount();
			}
		}
		total += getGiftMoney();
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

	public int getBalance() {
		return getMoneyPaid() - getCompleteLessonsCost() - getWrittenOffMoney();
	}

	private int getWrittenOffMoney() {
		for (Event e : getEvents())
			if (e.isWriteOff())
				return e.getEventType().getPrice();
		return 0;
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

	@NonVisual
	public boolean hasSchedule() {
		if (this.schedule == null)
			return false;
		for (int i = 1; i < 8; i++)
			if (schedule.get(i))
				return true;
		return false;
	}

	@NonVisual
	public int getEventShiftsByTeacher() {
		int count = 0;
		for (Event e : getEvents())
			if (e.getComment() != null
					&& e.getComment().contains(
							EventState.movedByTeacher.toString()))
				count++;
		return count;
	}

	@NonVisual
	public int getEventShiftsByClient() {
		int count = 0;
		for (Event e : getEvents())
			if (e.getComment() != null
					&& e.getComment().contains(
							EventState.movedByClient.toString()))
				count++;
		return count;
	}

	@NonVisual
	public boolean hasEventShiftsByClient() {
		return getEventShiftsByClient() > 0;
	}

	public static List<Contract> cleanList() {
		return new ArrayList<Contract>();
	}

	public boolean notTrial() {
		return this.contractTypeId != ContractType.Trial;
	}

	public int getGiftMoney() {
		// TODO do something with database and eliminate default value
		if (isGift())
			return giftMoney > 0 ? giftMoney : 300;
		return giftMoney;
	}

	public void setGiftMoney(int giftMoney) {
		this.giftMoney = giftMoney;
	}

	public boolean hasComment() {
		return comment != null && !comment.isEmpty();
	}

	public void setDateFreezed(Date freeze) {
		dateFreeze = freeze;
	}

	public void setDateUnfreezed(Date unfreezed) {
		dateUnfreeze = unfreezed;
	}
}
