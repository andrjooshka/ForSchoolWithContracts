package tap.execounting.components.editors;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.internal.services.MessagesSource;
import org.apache.tapestry5.internal.util.CaptureResultCallback;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.entities.Payment;

public class AddPayment {

	@Property
	@Persist
	private boolean updateMode;

	@Inject
	private CRUDServiceDAO dao;
	@Inject
	private ComponentResources resources;
    @Inject
    private Messages messages;

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

    void onValidate() throws ValidationException {
        if(payment.getContractId() == 0)
            throw new ValidationException(messages.get("unsuccessful"));
    }

	Object onSuccess() {
		if (updateMode)
			dao.update(payment);
		else
			dao.create(payment);

		CaptureResultCallback<Object> capture = new CaptureResultCallback<Object>();
		resources.triggerEvent("InnerPayment", new Object[] { payment },
				capture);
		return capture.getResult();
	}
}
