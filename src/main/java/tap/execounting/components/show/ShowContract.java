package tap.execounting.components.show;

import java.util.HashMap;
import java.util.List;

import tap.execounting.services.SuperCalendar;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import tap.execounting.components.editors.AddContract;
import tap.execounting.components.editors.AddEvent;
import tap.execounting.components.editors.AddPayment;
import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.entities.Client;
import tap.execounting.entities.Contract;
import tap.execounting.entities.Event;
import tap.execounting.entities.EventType;
import tap.execounting.entities.Payment;
import tap.execounting.entities.Teacher;

@Import(stylesheet = "context:/layout/contract.css")
public class ShowContract {

	@Parameter
	@Property
	private Contract contract;

	@Inject
	private CrudServiceDAO dao;
	@Inject
	private SuperCalendar calendar;
	@Inject
	private Request request;

	@InjectComponent
	private Zone bodyZone;

	@Property
	private boolean updateMode;
	@Property
	private boolean addingEvent;
	@Property
	private Event newEvent;
	@Component
	private AddEvent eventEditor;
	@Component
	private Zone eventZone;
	@Property
	private Event loopEvent;

	@Component
	private AddPayment paymentEditor;
	@Component
	private Zone paymentZone;
	@Property
	private Payment loopPayment;

	@Component
	private AddContract editor;

	@Property
	private boolean addingPayment;

	Object onEdit(Contract e) {
		editor.setup(e);
		updateMode = true;
		return bodyZone.getBody();
	}

	void onDelete(Contract con) {
		if (con.getEvents().size() > 0 || con.getPayments().size() > 0)
			throw new IllegalArgumentException(
					"Перед удалением договора - удалите все платежи по нему и все его события");
		dao.delete(Contract.class, con.getId());
	}

	void onFreeze(Contract con) {
		System.out.print("\n\n onFreeze\n");
		con.setFreeze(!con.isFreeze());
		dao.update(con);
	}

	Object onAddEvent(Contract con) {
		addingEvent = true;
		newEvent = new Event();
		newEvent.setTypeId(con.getTypeId());
		newEvent.addContract(con);
		Client c = dao.find(Client.class, con.getClientId());
		newEvent.getClients().add(c);
		newEvent.setHostId(con.getTeacherId());
		eventEditor.setup(newEvent, false);

		return eventZone.getBody();
	}

	Object onAddPayment(Contract con) {
		addingPayment = true;
		Payment p = new Payment();
		p.setContractId(con.getId());
		paymentEditor.setup(p);

		return paymentZone.getBody();
	}

	Object onEditPayment(Payment p) {
		addingPayment = true;
		paymentEditor.setup(p);

		return request.isXHR() ? paymentZone.getBody() : null;
	}

	void onDeletePayment(Payment p) {
		dao.delete(Payment.class, p.getId());
	}

	void onDeleteEvent(Event e) {
		dao.delete(Event.class, e.getId());
	}

	Object onEditEvent(Event e) {
		addingEvent = true;
		eventEditor.setup(e, true);

		return request.isXHR() ? eventZone.getBody() : null;
	}

	public String getEtype() {
		return dao.find(EventType.class, contract.getTypeId()).getTitle();
	}

	public String getTeacher() {
		String s;
		if (contract.getTeacherId() == 0)
			s = "не указан";
		else
			s = dao.find(Teacher.class, contract.getTeacherId()).getName();

		return s;
	}

	public List<Payment> getPayments() {
		HashMap<String, Object> params = new HashMap<String, Object>(1);
		params.put("contractId", contract.getId());
		return dao.findWithNamedQuery(Payment.BY_CONTRACT_ID, params);
	}

	public List<Event> getEvents() {
		return contract.getEvents(true);
	}

	public String getPaymentInfo() {
		StringBuilder sb = new StringBuilder();
		calendar.setTime(loopPayment.getDate());
		sb.append("От: ");
		sb.append(calendar.stringByTuple("day", "month", "year"));
		sb.append(", Сумма: ");
		sb.append(loopPayment.getAmount());
		if (loopPayment.isScheduled())
			sb.append(" (план.)");

		return sb.toString();
	}

	public String getEventInfo() {
		StringBuilder sb = new StringBuilder();
		// sb.append("#" + loopEvent.getId() + " от: ");
		try {
			sb.append(calendar.setTime(loopEvent.getDate()).stringByTuple(
					"day", "month", "year"));
		} catch (NullPointerException npe) {
			sb.append("нет информации о дате");
		}
		sb.append(". " + loopEvent.getState().toString());
		return sb.toString();
	}

	public String getContractDate() {
		calendar.setTime(contract.getDate());
		return calendar.stringByTuple("day", "month", "year");
	}

	public String getFreezeLabel() {
		String response = contract.isFreeze() ? "разморозить" : "заморозить";
		return response;
	}

	public String getLockImg() {

		return contract.isFreeze() ? request.getContextPath()
				+ "/icons/lock.png" : request.getContextPath()
				+ "/icons/ulock.png";
	}
}
