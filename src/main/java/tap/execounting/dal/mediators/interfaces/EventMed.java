package tap.execounting.dal.mediators.interfaces;

import java.util.Date;
import java.util.List;

import tap.execounting.data.EventState;
import tap.execounting.entities.Client;
import tap.execounting.entities.Contract;
import tap.execounting.entities.Event;
import tap.execounting.entities.EventType;
import tap.execounting.entities.Facility;
import tap.execounting.entities.Room;
import tap.execounting.entities.Teacher;

public interface EventMed {
//unit methods:
	//unit
	public Event getUnit();
	public EventMed setUnit(Event unit);

	//getters:
	//Date
	public Date getDate();
	
	//Teacher
	public Teacher getTeacher();

	//Clients
	public List<Client> getClients();

	//Discipline (EventType)
	public EventType getEventType();
	
	//Contracts
	public List<Contract> getContracts();
	
	//Price
	public int getPrice();

	//State
	public EventState getState();

	//Comment
	public String getComment();
	
	//Facility
	public Facility getFacility();

	//Room
	public Room getRoom();
	
	//EventTypeLoader
	public EventType loadEventType(int id);

//group methods:
	//group
	public List<Event> getGroup();
	public List<Event> getGroup(boolean resetAfter);
	public EventMed setGroup(List<Event> items);
	public List<Event> getAllEvents();
	public void reset();
	public String getFilterState();
	
	//filters
	//Teacher
	public EventMed filter(Teacher unit);
	
	//Contract
	public EventMed filter(Contract unit);

	//State
	public EventMed filter(EventState state);

	//Facility
	public EventMed filter(Facility unit);

	//Room
	public EventMed filter(Room unit);
	
	//Discipline
	public EventMed filter(EventType type);

	//Date
	public EventMed filter(Date date1, Date date2); 

	//counters
	//group length
	public Integer countGroupSize();

	//state
	public Integer count(EventState state);
	public Integer countEventsComplete();
	public Integer countEventsFailed();
	public Integer countEventsFailedByClient();
	public Integer countEventsFailedByTeacher();

	//money
	public Integer countMoney();
	public Integer countTeacherMoney();
	public Integer countSchoolMoney();
	public Integer countMoneyOfCompleteEvents();
	public Integer countMoneyOfFailedEvents();
	public Integer countMoneyOfEventsFailedByClient();
	public Integer countMoneyOfEventsFailedByTeacher();

	//percent
	public Integer countGivenPercentOfMoney(int percent);

	//days
	public int countDaysInEventsGroup();
}
