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

    /**
     * Teacher name
     * @return name of the teacher responsible for the contract
     */
    public String getTeacherName();

    /**
     * Client name
     * @return name of the client written in contract
     */
	public String getClientName();

    /**
     * Discipline, or subject type, or event type.
     * @return
     */
	public EventType getEventType();

    /**
     * State of the contract
     * @return
     */
	public ContractState getContractState();

    /**
     * String representation of contract state
     * @return
     */
    public String getContractStateString(boolean shrt);

	// date
	public Date getDate();

	// remaining events
	public int getRemainingLessons();

	// price
	public int getPrice();

	// Unit action methods

	// Write off all remaining events, to transfer all the money to the school
	// account.
	public void doWriteOff();
	
	public void doMoneyback() throws Exception;

	// Event planner method
	public void doPlanEvents(Date eventsStartDate);
	
	// Freezes the contract, also replanning the events after unfreeze date
	public ContractMed doFreeze(Date dateFreeze, Date dateUnfreeze);

    // Unfreezes the contract, by removing freeze dates
    public ContractMed doUnfreeze();

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
	
	// money paid for the certificate if contract has so
	public int countCertificateMoney();

	// Intersection operation
	public ContractMed retain(List<Contract> contracts);

	public ContractMed sortByDate(boolean asc);
}
