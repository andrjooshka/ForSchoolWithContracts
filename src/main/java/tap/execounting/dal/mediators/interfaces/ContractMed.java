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
	// unit methods
	// unit
	public Contract getUnit();

	public ContractMed setUnit(Contract unit);

	public ContractMed setUnitId(int id);

	// getters:
	// Teacher name
	public String getTeacherName();

	// Client name
	public String getClientName();

	// discipline
	public EventType getEventType();

	// contract type
	public ContractType getContractType();

	// state
	public ContractState getContractState();

	// date
	public Date getDate();

	// lessons number
	public int getLessonsNumber();

	// remaining events
	public int getRemainingLessons();

	// price
	public int getPrice();

	// balance
	public int getBalance();

	// events
	public List<Event> getEvents();

	// payments
	public List<Payment> getPayments();

	public EventType loadEventType(int id);

	// Unit action methods

	// Write off all remaining events, to transfer all the money to the school
	// account.
	public void doWriteOff();
	
	public void doMoneyback() throws Exception;

	// Event planner method
	public void doPlanEvents(Date eventsStartDate);
	
	// Freezes the contract, also replanning the events after unfreeze date
	public void doFreeze(int contractId, Date dateFreeze, Date dateUnfreeze);

	// group methods
	// group
	public List<Contract> getGroup();

	public ContractMed setGroup(List<Contract> group);
	
	// Extracts all contracts from ever client and adds it to group
	public ContractMed setGroupFromClients(List<Client> clients);
	
	// Translates all contracts in group to clients
	public List<Client> getClients();

	public List<Contract> getAllContracts();

	public void reset();

	public String getFilterState();

	// //filters:

	// client
	public ContractMed filter(Client c);

	// teacher
	public ContractMed filter(Teacher t);

	// state
	public ContractMed filter(ContractState state);

	// remaining lessons
	public ContractMed filter(int remainingLessons);

	// date
	public ContractMed filter(Date date1, Date date2);

	// planned payments date
	public ContractMed filterByPlannedPaymentsDate(Date date1, Date date2);

	// contract type
	public ContractMed filterByContractType(int type);

	// this removes finished contracts from group. added as Tema asked, to
	// remove finished trials from TeacherPage
	public ContractMed removeComlete();

	// //counters:

	// actual group size
	public Integer countGroupSize();
	
	// Trial / Not trial dichotomy is in wide use.
	public int countNotTrial();

	// state
	public Integer count(ContractState state);

	// money for complete events
	public Integer countCompleteLessonsMoney();

	// money paid on contract
	public Integer countMoneyPaid();
	
	// money paid for the certificate if contract has so
	public int countCertificateMoney();

	// Intersection operation
	public ContractMed retain(List<Contract> contracts);

	public ContractMed sortByDate(boolean asc);
}
