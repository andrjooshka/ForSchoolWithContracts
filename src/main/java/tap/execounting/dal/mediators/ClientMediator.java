package tap.execounting.dal.mediators;

import java.util.Date;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.data.ClientState;
import tap.execounting.entities.Client;
import tap.execounting.entities.Contract;
import tap.execounting.entities.Teacher;

public class ClientMediator implements ClientMed {

	@Inject
	private CrudServiceDAO dao;
	
	public Client getUnit() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setUnit() {
		// TODO Auto-generated method stub
		
	}

	public boolean hasContracts() {
		// TODO Auto-generated method stub
		return false;
	}

	public List<Contract> getContarcts() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasActiveContracts() {
		// TODO Auto-generated method stub
		return false;
	}

	public List<Contract> getActiveContracts() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasFrozenContracts() {
		// TODO Auto-generated method stub
		return false;
	}

	public List<Contract> getFrozenContracts() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasCanceledContracts() {
		// TODO Auto-generated method stub
		return false;
	}

	public List<Contract> getCanceledContracts() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasTrialContracts() {
		// TODO Auto-generated method stub
		return false;
	}

	public List<Contract> getTrialContracts() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasFinishedContracts() {
		// TODO Auto-generated method stub
		return false;
	}

	public List<Contract> getFinishedContracts() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getBalance() {
		// TODO Auto-generated method stub
		return 0;
	}

	public ClientState getState() {
		// TODO Auto-generated method stub
		return null;
	}

	public void cancelClient() {
		// TODO Auto-generated method stub
		
	}

	public Date getDateOfFirstContract() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Teacher> getActiveTeachers() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Teacher> getReturn() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Client> getGroup() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setGroup(List<Client> group) {
		// TODO Auto-generated method stub
		
	}

	public List<Client> getAllClient() {
		// TODO Auto-generated method stub
		return null;
	}

	public void reset() {
		// TODO Auto-generated method stub
		
	}

	public String getStateFilter() {
		// TODO Auto-generated method stub
		return null;
	}

	public ClientMed filter(ClientState state) {
		// TODO Auto-generated method stub
		return null;
	}

	public ClientMed filter(Teacher teacher) {
		// TODO Auto-generated method stub
		return null;
	}

	public ClientMed filterDateOfFirstContract(Date date1, Date date2) {
		// TODO Auto-generated method stub
		return null;
	}

	public ClientMed filterDateOfPlannedPayments(Date date1, Date date2) {
		// TODO Auto-generated method stub
		return null;
	}

	public ClientMed filterName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Client> getDebtors() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Client> getClientsWithExpiringContracts() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer countGroupSize() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer count(ClientState state) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer count(ClientState state, Date date1, Date date2) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer countContinuers(Date date1, Date date2) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer countNewbies(Date date1, Date date2) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer countTrial(Date date1, Date date2) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer countCanceled(Date date1, Date date2) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer countUndefined(Date date1, Date date2) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer countFrozen(Date date1, Date date2) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
