package tap.execounting.components.show;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;

import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.entities.Payment;
import tap.execounting.services.SuperCalendar;

public class ShowPayment {

	@Property
	private boolean updateMode;
	@Component
	private Zone bodyZone;
	@Inject
	private Request request;
	@Inject
	private CrudServiceDAO dao;

	@Parameter(required = true)
	@Property
	private Payment payment;

	@Persist
	private Payment paymentback;
	@Inject
	private SuperCalendar calendar;
	@Parameter(required = true)
	private String id;
	@Inject
	private AjaxResponseRenderer renderer;

	public String getPaymentInfo() {
		StringBuilder builder = new StringBuilder();
		builder.append(calendar.setTime(payment.getDate()).stringByTuple("day",
				"month")
				+ ": ");
		builder.append(payment.getAmount());
		builder.append(" р. за '"
				+ payment.getContract().getEventType().getTitle() + "'.");
		if (payment.getComment() != null && !payment.getComment().equals(""))
			builder.append("Комм.: " + payment.getComment());
		return builder.toString();
	}

	Block onEdit(Payment payment) {
		updateMode = true;
		return request.isXHR() ? bodyZone.getBody() : null;
	}

	Block onDelete(Payment payment) {
		dao.delete(Payment.class, payment.getId());
		return request.isXHR() ? bodyZone.getBody() : null;
	}

	public void refreshZone() {
		renderer.addRender(bodyZone.getClientId(), bodyZone);
		// return bodyZone.getBody();
	}

	public boolean isUpdateMode() {
		return updateMode;
	}

	public String getId() {
		if (id != null)
			if (payment != null)
				return payment.getId() + "";
			else
				return paymentback.getId() + "";
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
