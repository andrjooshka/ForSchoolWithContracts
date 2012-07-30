package tap.execounting.dal.mediators;

import java.util.Date;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.data.ContractState;
import tap.execounting.entities.Client;
import tap.execounting.entities.Contract;
import tap.execounting.entities.ContractType;
import tap.execounting.entities.Event;
import tap.execounting.entities.EventType;
import tap.execounting.entities.Payment;
import tap.execounting.entities.Teacher;

public class ContractMediator implements ContractMed{
	
	@Inject
	private CrudServiceDAO dao;

	public Contract getUnit() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setUnit(Contract unit) {
		// TODO Auto-generated method stub
		
	}

	public String getTeacherName() {
		// TODO Auto-generated method stub
		return null;
	}

	public EventType getEventType() {
		// TODO Auto-generated method stub
		return null;
	}

	public ContractType getContractType() {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getDate() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getLessonsNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getPrice() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getBalance() {
		// TODO Auto-generated method stub
		return 0;
	}

	public List<Event> getEvents() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Payment> getPayments() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Contract> getGroup() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setGroup(List<Contract> group) {
		// TODO Auto-generated method stub
		
	}

	public List<Contract> getAllContracts() {
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

	public ContractMed filter(Client c) {
		// TODO Auto-generated method stub
		return null;
	}

	public ContractMed filter(Teacher t) {
		// TODO Auto-generated method stub
		return null;
	}

	public ContractMed filter(ContractState state) {
		// TODO Auto-generated method stub
		return null;
	}

	public ContractMed filter(int remainingLessons) {
		// TODO Auto-generated method stub
		return null;
	}

	public ContractMed filter(Date date1, Date date2) {
		// TODO Auto-generated method stub
		return null;
	}

	public ContractMed filterByPlannedPaymentsDate(Date date1, Date date2) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer countGroupSize() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer count(ContractState state) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer countRemainingLessons() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer countCompleteLessonsMoney() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer countMoneyPaid() {
		// TODO Auto-generated method stub
		return null;
	}

}
