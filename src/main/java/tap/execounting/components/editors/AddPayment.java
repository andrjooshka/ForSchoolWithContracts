package tap.execounting.components.editors;


import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.entities.Payment;

public class AddPayment {


	@Property
	@Persist
	private boolean updateMode;

	@Inject
	private CrudServiceDAO dao;

	@Property
	@Persist
	private Payment payment;


	public void setup(Payment p) {
		payment = p;
		updateMode = true;
	}

	public void reset() {
		payment = new Payment();
		updateMode = false;
	}

	void onSubmit() {
		
		if (updateMode) {
			dao.update(payment);
		} else {
			dao.create(payment);
		}
	}
}
