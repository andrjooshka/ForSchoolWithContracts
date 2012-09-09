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
	public ContractMed setUnit(Contract unit);
	
	//getters:
	//Teacher name
	public String getTeacherName();
	
	//discipline
	public EventType getEventType();
	
	//contract type
	public ContractType getContractType();
	
	//state
	public ContractState getContractState();
	
	//date
	public Date getDate();
	
	//lessons number
	public int getLessonsNumber();
	
	//remaining events
	public int getRemainingLessons();
	
	//price
	public int getPrice();
	
	//balance
	public int getBalance();
	
	//events
	public List<Event> getEvents();
	
	//payments
	public List<Payment> getPayments();
	
	//event planner method
	public void planEvents();
	
//group methods
	//group
	public List<Contract> getGroup();
	public ContractMed setGroup(List<Contract> group);
	public List<Contract> getAllContracts();
	public void reset();
	public String getFilterState();	
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
	
	//contract type
	public ContractMed filterByContractType(int type);
	
////counters:
	
	//actual group size
	public Integer countGroupSize();
	
	//state
	public Integer count(ContractState state);
	
	//money for complete events
	public Integer countCompleteLessonsMoney();
	
	//money paid on contract
	public Integer countMoneyPaid();
}



















