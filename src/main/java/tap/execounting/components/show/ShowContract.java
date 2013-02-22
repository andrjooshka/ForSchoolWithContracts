package tap.execounting.components.show;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.tapestry5.annotations.*;
import tap.execounting.security.AuthorizationDispatcher;
import tap.execounting.services.DateService;
import tap.execounting.services.SuperCalendar;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;

import tap.execounting.components.Freezer;
import tap.execounting.components.editors.AddContract;
import tap.execounting.components.editors.AddEvent;
import tap.execounting.components.editors.AddPayment;
import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.dal.mediators.interfaces.ContractMed;
import tap.execounting.dal.mediators.interfaces.EventMed;
import tap.execounting.dal.mediators.interfaces.PaymentMed;
import tap.execounting.entities.Client;
import tap.execounting.entities.Contract;
import tap.execounting.entities.Event;
import tap.execounting.entities.EventType;
import tap.execounting.entities.Payment;
import tap.execounting.entities.Teacher;

@Import(stylesheet = "context:css/contract.css")
public class ShowContract {

	@Parameter
	@Property
	private Contract contract;

	// Useful bits
	@Inject
	private CRUDServiceDAO dao;
	@Inject
	private SuperCalendar calendar;
	@Inject
	private Request request;
	@Inject
	@Property
	private AuthorizationDispatcher dispatcher;
	@Inject
	private ContractMed contractMed;
	@Inject
	private AjaxResponseRenderer renderer;

	// Screen fields
	@InjectComponent
	private Zone bodyZone;
	@Property
	private Event loopEvent;
	@Component
	private AddEvent eventEditor;
	@Component
	private AddPayment paymentEditor;
	@Component
	private Zone paymentZone;
	@Property
	private Payment loopPayment;
	@Component
	private AddContract editor;
    @Component
    private Zone freezerZone;
    @Property
    private Date dateFreeze;
    @Property
    private Date dateUnfreeze;

	// Behavior fields
	@Property
	private boolean updateMode;
	@Property
	private boolean addingEvent;
	@Property
	private Event newEvent;
	@Component
	private Zone eventZone;
	@Property
	private boolean addingPayment;
    @Property
    private  boolean freezerActive;

    @BeginRender
    void init(){
        this.contractId = this.contract.getId();
    }

	Object onEdit(int contractId) {
		// AUTHORIZATION MOUNT POINT EDIT CONTRACT
		refreshUnit(contractId);
		if (dispatcher.canEditContracts()) {
			editor.setup(contract);
			updateMode = true;
		}
		return bodyZone.getBody();
	}

	private void refreshUnit(int conId) {
		contract = dao.find(Contract.class, conId);
        contractMed.setUnit(contract);
	}

	void onDelete(Contract con) {
		// AUTHORIZATION MOUNT POINT DELETE CONTRACT
		if (dispatcher.canDeleteContracts()) {
			if (con.getEvents().size() > 0 || con.getPayments().size() > 0)
				// JAVASCRIPT MOUNT POINT ALERT
				throw new IllegalArgumentException(
						"Перед удалением договора - удалите все платежи по нему и все его события");
			dao.delete(Contract.class, con.getId());
		}
	}

	void onFreeze(int contractId) {
		// AUTHORIZATION MOUNT POINT EDIT CONTRACT FREEZE
		refreshUnit(contractId);
		if (dispatcher.canEditContracts()) {
            if(contract.isFrozen())
                contractMed.doUnfreeze();
            else {
                this.contractId = contractId;
                freezerActive = true;
                renderer.addRender(freezerZone.getClientId(), freezerZone);
            }
		}
	}

    void onPrepareFromFreezeForm(){
        dateFreeze = new Date();
        dateUnfreeze = DateService.datePlusMonths(dateFreeze,6);
    }

    void onSuccessFromFreezeForm(int contractId){
        refreshUnit(contractId);
        contractMed.doFreeze(dateFreeze,  dateUnfreeze);
        freezerActive = false;
    }

	Object onWriteOff(int contractId) {
		refreshUnit(contractId);
		contractMed.setUnitId(contractId);
		contractMed.doWriteOff();
		this.contract = contractMed.getUnit();
		return bodyZone;
	}
	
	Object onMoneyback(int contractId) throws Exception {
		refreshUnit(contractId);
		contractMed.setUnitId(contractId);
		contractMed.doMoneyback();
		this.contract = contractMed.getUnit();
		return bodyZone;
	}

	Object onAddEvent(int contractId) {
		// AUTHORIZATION MOUNT POINT CREATE EVENT
		refreshUnit(contractId);
		if (dispatcher.canCreateEvents()) {
			addingEvent = true;
			newEvent = new Event();
			newEvent.setTypeId(contract.getTypeId());
			newEvent.addContract(contract);
			Client c = dao.find(Client.class, contract.getClientId());
			newEvent.getClients().add(c);
			newEvent.setHostId(contract.getTeacherId());
			eventEditor.setup(newEvent, false);
		}
		return eventZone.getBody();
	}

	Object onAddPayment(int contractId) {
		// AUTHORIZATION MOUNT POINT CREATE PAYMENT
		refreshUnit(contractId);
		if (dispatcher.canCreatePayments()) {
			addingPayment = true;
			Payment p = new Payment();
			p.setContractId(contractId);
			paymentEditor.setup(p);
		}
		return paymentZone.getBody();
	}

	Object onInnerPayment(Payment p) {
		refreshUnit(p.getContractId());
		if (p.getId() != 0) {
			for (Payment p2 : contract.getPayments())
				if (p2.getId() == p.getId())
					return paymentZone.getBody();
			contract.getPayments().add(p);
		}
		return paymentZone.getBody();
	}

	@Inject
	private PaymentMed paymentMed;

	Object onEditPayment(int paymentId) {
		// AUTHORIZATION MOUNT POINT EDIT PAYMENT
		Payment p = paymentMed.getUnitById(paymentId);
		refreshUnit(p.getContractId());
		if (dispatcher.canEditPayments()) {
			addingPayment = true;
			paymentEditor.setup(p);
		}
		return request.isXHR() ? paymentZone.getBody() : null;
	}

	void onDeletePayment(int paymentId) {
		// AUTHORIZATION MOUNT POINT DELETE PAYMENT
		if (dispatcher.canDeletePayments()) {
			dao.delete(Payment.class, paymentId);
		}
	}

	void onDeleteEvent(int eventId) {
		// AUTHORIZATION MOUNT POINT DELETE EVENT
		if (dispatcher.canDeleteEvents()) {
			dao.delete(Event.class, eventId);
		}
	}

    @Property
    @Persist
	private Integer contractId;
	@Inject
	private EventMed eventMed;

	Object onEditEvent(int eventId, int conId) {
		// AUTHORIZATION MOUNT POINT EDIT EVENT
		Event e = eventMed.getUnitById(eventId);
		contractId = conId;
		refreshUnit(conId);
		if (dispatcher.canEditEvents()) {
			addingEvent = true;
			eventEditor.setup(e, true);
		}
		return request.isXHR() ? eventZone.getBody() : null;
	}

	// Here is a crutch / workaround. Against hibernate lazy initialization
	// exception
	public boolean isCompleteNotPaid() {
		contract = dao.find(Contract.class, contract.getId());
		return contract.isComplete() && !contract.isPaid();
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
		if (loopPayment.getComment() != null)
			sb.append(" [" + loopPayment.getComment() + "]");

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

	public int getEventShiftsByClient() {
		return contract.getEventShiftsByClient();
	}

	public boolean hasEventShiftsByClient() {
		return contract.hasEventShiftsByClient();
	}

	public String getContractDate() {
		calendar.setTime(contract.getDate());
		return calendar.stringByTuple("day", "month", "year");
	}

	public String getFreezeLabel() {
		String response = contract.isFrozen() ? "разморозить" : "заморозить";
		return response;
	}

	@Inject
	@Path("context:icons/lock.png")
	private Asset locked;
	@Inject
	@Path("context:icons/ulock.png")
	private Asset unlocked;

	public Asset getLockImg() {
		return contract.isFrozen() ? locked : unlocked;
	}

	Object onExperiment(Contract con) {
		this.contract = con;
		return bodyZone.getBody();
	}

	Object onCancel(Contract con) {
		this.contract = con;
		return bodyZone.getBody();
	}

	Object onInnerUpdate(Event e) {
		// Be carefull, as it returns only the first contract from events
		if (contractId != null)
			refreshUnit(contractId);
		else if (e.getContracts() != null && e.getContracts().size() > 0)
			this.contract = dao.find(Contract.class, e.getContracts().get(0)
					.getId());

		if (e.getId() != 0) {
			for (Event ei : contract.getEvents())
				if (ei.getId() == e.getId())
					return eventZone.getBody();
			this.contract.getEvents().add(e);
		}
		return eventZone.getBody();
	}
}
