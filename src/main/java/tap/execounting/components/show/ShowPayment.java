package tap.execounting.components.show;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.entities.Payment;
import tap.execounting.services.SuperCalendar;

public class ShowPayment {

	@Parameter(required = true)
	@Property
	private Payment payment;

	@Inject
	private SuperCalendar calendar;

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
}
