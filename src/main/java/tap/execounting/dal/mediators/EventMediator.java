package tap.execounting.dal.mediators;

import java.util.Date;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.data.EventState;
import tap.execounting.entities.Client;
import tap.execounting.entities.Contract;
import tap.execounting.entities.Event;
import tap.execounting.entities.EventType;
import tap.execounting.entities.Facility;
import tap.execounting.entities.Room;
import tap.execounting.entities.Teacher;

public class EventMediator implements EventMed {
	
	@Inject
	private CrudServiceDAO dao;

	public Event getUnit() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setUnit(Event unit) {
		// TODO Auto-generated method stub
		
	}

	public Date getDate() {
		// TODO Auto-generated method stub
		return null;
	}

	public Teacher getTeacher() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Client> getClient() {
		// TODO Auto-generated method stub
		return null;
	}

	public EventType getEventType() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Contract> getContracts() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getPrice() {
		// TODO Auto-generated method stub
		return 0;
	}

	public EventState getState() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getComment() {
		// TODO Auto-generated method stub
		return null;
	}

	public Facility getFacility() {
		// TODO Auto-generated method stub
		return null;
	}

	public Room getRoom() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Event> getGroup() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setGroup(List<Event> items) {
		// TODO Auto-generated method stub
		
	}

	public List<Event> getAllEvents() {
		// TODO Auto-generated method stub
		return null;
	}

	public void reset() {
		// TODO Auto-generated method stub
		
	}

	public String getFilterState() {
		// TODO Auto-generated method stub
		return null;
	}

	public EventMed filter(Teacher unit) {
		// TODO Auto-generated method stub
		return null;
	}

	public EventMed filter(Contract unit) {
		// TODO Auto-generated method stub
		return null;
	}

	public EventMed filter(EventState state) {
		// TODO Auto-generated method stub
		return null;
	}

	public EventMed filter(Facility unit) {
		// TODO Auto-generated method stub
		return null;
	}

	public EventMed filter(Room unit) {
		// TODO Auto-generated method stub
		return null;
	}

	public EventMed filter(EventType type) {
		// TODO Auto-generated method stub
		return null;
	}

	public EventMed filter(Date date1, Date date2) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer countGroupSize() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer count(EventState state) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer countEventsComplete() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer countEventsFailed() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer countEventsFailedByClient() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer countEventsFailedByTeacher() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer countMoney() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer countMoneyOfCompleteEvents() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer countMoneyOfFailedEvents() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer countMoneyOfEventsFailedByClient() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer countMoneyOfEventsFailedByTeacher() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer countGivenPercentOfMoney(int percent) {
		// TODO Auto-generated method stub
		return null;
	}

	public int countDaysInEventsGroup() {
		// TODO Auto-generated method stub
		return 0;
	}

}
