package tap.execounting.dal.mediators.interfaces;

public interface ClientMed {
//unit
	public Client getUnit();
	public void setUnit();

	//getters

	//contracts
	public boolean hasContracts();
	public List<Contract> getContarcts();

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

//group
	public List<Client> getGroup();
	public void setGroup(List<Client> group);
	public List<Client> getAllClient();
	public void reset();
	public String getStateFilter();
	//filters
	
	//counters
}
