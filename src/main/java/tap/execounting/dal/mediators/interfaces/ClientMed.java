package tap.execounting.dal.mediators.interfaces;

import java.util.Date;
import java.util.List;

import tap.execounting.data.ClientState;
import tap.execounting.entities.Client;
import tap.execounting.entities.Contract;
import tap.execounting.entities.Teacher;

public interface ClientMed {
//unit
	public Client getUnit();
	public ClientMed setUnit(Client c);

	//getters

	//contracts
	public boolean hasContracts();
	public List<Contract> getContracts();

		//active
	public boolean hasActiveContracts();
	public List<Contract> getActiveContracts();
	
		//frozen
	public boolean hasFrozenContracts();
	public List<Contract> getFrozenContracts();

		//canceled
	public boolean hasCanceledContracts();
	public List<Contract> getCanceledContracts();

		//trial
	public boolean hasTrialContracts();
	public List<Contract> getTrialContracts();

		//finished
	public boolean hasFinishedContracts();
	public List<Contract> getFinishedContracts();

	//balance
	public int getBalance();

	//state
	public ClientState getState();
	public void cancelClient();

	//date of first contract
	public Date getDateOfFirstContract();

	//teachers
	public List<Teacher> getActiveTeachers();
	
	//return
	public int getReturn();

//group
	public List<Client> getGroup();
	public List<Client> getGroup(boolean reset);
	public ClientMed setGroup(List<Client> group);
	public List<Client> getAllClient();
	public void reset();
	public String getFilterState();
	
	//filters
	
	//state
	public ClientMed filter(ClientState state);
	
	//Teacher
	public ClientMed filterByActiveTeacher(Teacher teacher);
	
	//Date of first contract (duration of relations)
	public ClientMed filterDateOfFirstContract(Date date1, Date date2);
	
	//Date of planned payments
	public ClientMed filterDateOfPlannedPayments(Date date1, Date date2);
	
	//Name filter
	public ClientMed filterName(String name);
	
	//debtors
	public List<Client> getDebtors();
	
	//contract expiring
	public List<Client> getClientsWithExpiringContracts();
	
	//counters:
	public Integer countGroupSize();
	
	//state
	public Integer count(ClientState state);
	public Integer count(ClientState state, Date date1, Date date2);
	
		//continuers
	public Integer countContinuers(Date date1, Date date2);
	
		//newbies
	public Integer countNewbies(Date date1, Date date2);
	
		//trials
	public Integer countTrial(Date date1, Date date2);
	
		//canceled
	public Integer countCanceled(Date date1, Date date2);
	
		//undefined
	public Integer countUndefined(Date date1, Date date2);
	
		//frozen
	public Integer countFrozen(Date date1, Date date2);
}















