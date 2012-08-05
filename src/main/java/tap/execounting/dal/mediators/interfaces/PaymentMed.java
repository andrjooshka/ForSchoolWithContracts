package tap.execounting.dal.mediators.interfaces;

import java.util.Date;
import java.util.List;

import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.entities.Contract;
import tap.execounting.entities.Payment;

public interface PaymentMed {
//unit methods
	//unit
	public Payment getUnit();
	public void setUnit(Payment unit);

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
	
	//service methods which we should avoid to use
	public void setDao(CrudServiceDAO dao);
}
