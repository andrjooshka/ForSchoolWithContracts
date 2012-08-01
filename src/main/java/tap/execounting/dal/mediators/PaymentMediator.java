package tap.execounting.dal.mediators;

import java.util.Date;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.entities.Contract;
import tap.execounting.entities.Payment;

public class PaymentMediator implements PaymentMed {
	
	@Inject
	private CrudServiceDAO dao;

	public Payment getUnit() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setUnit() {
		// TODO Auto-generated method stub
		
	}

	public boolean getPlanned() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getComment() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getAmount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Date getDate() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Payment> getGroup() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setGroup() {
		// TODO Auto-generated method stub
		
	}

	public List<Payment> getAllPayments() {
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

	public PaymentMed filter(Contract unit) {
		// TODO Auto-generated method stub
		return null;
	}

	public PaymentMed filter(Date date1, Date date2) {
		// TODO Auto-generated method stub
		return null;
	}

	public PaymentMed filter(boolean state) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer countGroupSize() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer countAmount() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer countReturn(Date date1, Date date2) {
		// TODO Auto-generated method stub
		return null;
	}

}
