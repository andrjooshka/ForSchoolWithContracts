package tap.execounting.dal.mediators.interfaces;

import java.util.Date;
import java.util.List;

import tap.execounting.data.ContractState;
import tap.execounting.entities.Client;
import tap.execounting.entities.Contract;
import tap.execounting.entities.ContractType;
import tap.execounting.entities.Event;
import tap.execounting.entities.EventType;
import tap.execounting.entities.Payment;
import tap.execounting.entities.Teacher;

public interface ContractMed {
//unit methods
	//unit
	public Contract getUnit();
	public void setUnit(Contract unit);
	
	//getters:
	//Teacher name
	public String getTeacherName();
	
	//discipline
	public EventType getEventType();
	
	//contract type
	public ContractType getContractType();
	
	//date
	public Date getDate();
	
	//lessons number
	public int getLessonsNumber();
	
	//price
	public int getPrice();
	
	//balance
	public int getBalance();
	
	//events
	public List<Event> getEvents();
	
	//payments
	public List<Payment> getPayments();
	
//group methods
	//group
	public List<Contract> getGroup();
	public void setGroup(List<Contract> group);
	
////filters:
	
	//client
	public ContractMed filter(Client c);
	
	//teacher
	public ContractMed filter(Teacher t);
	
	//state
	public ContractMed filter(ContractState state);
	
	//remaining lessons
	public ContractMed filter(int remainingLessons);
	
	//date
	public ContractMed filter(Date date1, Date date2);
	
	//planned payments date
	public ContractMed filterByPlannedPaymentsDate(Date date1, Date date2);	
	
////counters:
	
	//actual group size
	public Integer count();
	
	//state
	public Integer count(ContractState state);
	
	//remaining events
	public Integer countRemainingLessons();
	
	//money for complete events
	public Integer countCompleteLessonsMoney();
	
	//money paid on contract
	public Integer countMoneyPaid();
}



















