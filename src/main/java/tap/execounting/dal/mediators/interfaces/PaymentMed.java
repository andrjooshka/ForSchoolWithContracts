package tap.execounting.dal.mediators.interfaces;

import java.util.Date;
import java.util.List;

import tap.execounting.entities.Contract;
import tap.execounting.entities.Payment;

public interface PaymentMed {
//unit methods
	//unit
	public Payment getUnit();
	public PaymentMed setUnit(Payment unit);
	public Payment getUnitById(int paymentId);

	//getters
	//planned
	public boolean getPlanned();

	//Comment
	public String getComment();
	
	//Amount
	public int getAmount();
	
	//Date
	public Date getDate();
	
//group methods
	//group
	public List<Payment> getGroup();
	public PaymentMed setGroup(List<Payment> payments);
	public PaymentMed setGroupFromContracts(List<Contract> contracts);
	public List<Payment> getAllPayments();
	public void reset();
	public String getFilterState();

	//filters
	//contract
	public PaymentMed filter(Contract unit);
	
	//Date
	public PaymentMed filter(Date date1, Date date2);

	//Planned (state)
	public PaymentMed filter(boolean state);

	//counters
	public Integer countGroupSize();
	
	//amount
	public Integer countAmount();
	public Integer countReturn(Date date1, Date date2);
	public int countRealPaymentsAmount();
	public int countScheduledPaymentsAmount();
	// Maps payments into contracts
	public List<Contract> getContracts();
}
