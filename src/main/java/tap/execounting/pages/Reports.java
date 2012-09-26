package tap.execounting.pages;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.hibernate.Criteria;
import org.hibernate.Session;

import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.entities.Client;
import tap.execounting.entities.Contract;
import tap.execounting.entities.Payment;
import tap.execounting.security.AuthorizationDispatcher;
import tap.execounting.services.SuperCalendar;

public class Reports {
	// Injected
	@Inject
	private Session session;
	@Inject
	private Messages messages;
	@Inject
	private AjaxResponseRenderer renderer;
	@Inject
	private AuthorizationDispatcher dispatcher;
	
	// Page components
	@Component
	private Zone paymentZone;
	@Inject
	private BeanModelSource beanModelSource;
	@Property
	private BeanModel<Client> modelOfEnding;
	@Property
	private BeanModel<Client> modelOfPayments;
	@Property
	private BeanModel<Client> modelOfDebtors;
	@Inject
	private ComponentResources componentResources;
	@Property
	private Payment loopPayment;
	@Persist
	private int loopPaymentId;
	@Inject
	private Block authBlock;
	@Inject
	private Block confirmBlock;
	// Page stuff
	@Property
	private Client client;
	@InjectPage
	private ClientPage clientPage;

	// TODO check if SQL will work better
	// Question is: should we remember about freezed contracts?
	public List<Client> getEndingLessons() {

		List<Contract> list = getAllContracts();
		// complete filter
		for (int i = list.size() - 1; i >= 0; i--)
			if (list.get(i).isComplete())
				list.remove(i);
		// remaining lessons
		for (int i = list.size() - 1; i >= 0; i--)
			if (list.get(i).getLessonsRemain() > 2)
				list.remove(i);

		Set<Client> clients = new HashSet<Client>(list.size());
		for (Contract c : list)
			clients.add(c.getClient());
		return new ArrayList<Client>(clients);
	}

	public String getEndingInfo() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < client.getContracts().size(); i++) {
			Contract c = client.getContracts().get(i);
			if (!c.isComplete() && c.getLessonsRemain() < 3) {
				builder.append(c.getEventType().getTitle() + ": ");
				builder.append(c.getLessonsRemain());
				if (c.getScheduledLessons().size() > 0)
					builder.append(" и " + c.getScheduledLessons().size()
							+ " уже запланировано");
				if (i < client.getContracts().size() - 1)
					builder.append(", ");
			}
		}
		if (builder.substring(builder.length() - 2).equals(", "))
			builder.replace(builder.length() - 2, builder.length(), "");
		return builder.toString();
	}

	public List<Client> getSchedPayments() {
		List<Contract> list = getAllContracts();

		for (int i = list.size() - 1; i >= 0; i--) {
			Contract c = list.get(i);
			if (c.getPlannedPayments(14).size() == 0)
				list.remove(i);
		}

		Set<Client> set = new HashSet<Client>(list.size());

		for (Contract c : list)
			set.add(c.getClient());

		return new ArrayList<Client>(set);
	}

	public List<Client> getDebtors() {
		@SuppressWarnings("unchecked")
		List<Client> list = session.createCriteria(Client.class).list();
		for (int i = list.size() - 1; i >= 0; i--)
			if (list.get(i).getBalance() >= 0)
				list.remove(i);
		return list;
	}

	public String getDebtInfo() {
		return client.getBalance() * (-1) + "";
	}

	void setupRender() {
		// end of subscription
		if (modelOfEnding == null) {
			modelOfEnding = beanModelSource.createDisplayModel(Client.class,
					componentResources.getMessages());
			modelOfEnding.add("endingInfo", null);
			modelOfEnding.exclude("return", "balance", "studentInfo",
					"firstContractDate", "state", "firstPlannedPaymentDate");
		}

		// soon payments
		if (modelOfPayments == null) {
			modelOfPayments = beanModelSource.createDisplayModel(Client.class,
					componentResources.getMessages());
			modelOfPayments.add("paymentsInfo", null);
			modelOfPayments.exclude("return", "balance", "studentInfo",
					"firstContractDate", "state");
		}

		// debtors
		if (modelOfDebtors == null) {
			modelOfDebtors = beanModelSource.createDisplayModel(Client.class,
					componentResources.getMessages());
			modelOfDebtors.add("debtInfo", null);
			modelOfDebtors.exclude("return", "balance", "studentInfo",
					"firstContractDate", "state", "firstPlannedPaymentDate");
		}
	}

	ClientPage onDetails(Client c) {
		clientPage.setup(c);
		return clientPage;
	}

	private List<Contract> getAllContracts() {
		Criteria criteria = session.createCriteria(Contract.class);
		@SuppressWarnings("unchecked")
		List<Contract> list = criteria.list();
		return list;
	}

	@Property
	@Persist
	private boolean switchPages;

	@Property
	private boolean editing;
	@Inject
	private SuperCalendar calendar;

	public int getLoopPaymentId() {
		int lpi;
		try {
			lpi = loopPayment.getId();
		} catch (NullPointerException e) {
			lpi = loopPaymentId;
		}
		return lpi;
	}

	public String getPaymentInfo() {
		StringBuilder builder = new StringBuilder();
		builder.append(calendar.setTime(loopPayment.getDate()).stringByTuple(
				"day", "month")
				+ ": ");
		builder.append(loopPayment.getAmount());
		builder.append(" р. за '"
				+ loopPayment.getContract().getEventType().getTitle() + "'.");
		if (loopPayment.getComment() != null
				&& !loopPayment.getComment().equals(""))
			builder.append("Комм.: " + loopPayment.getComment());
		return builder.toString();
	}

	public Block getDeleteBlock(){
		if(dispatcher.canDeletePayments())
			return confirmBlock;
		return authBlock;
	}

	void onEdit(Payment p) {
		if (dispatcher.canEditPayments())
			editing = true;
		loopPayment = p;
		renderer.addRender("paymentBody" + p.getId(), paymentZone);
	}

	void onDelete(int paymentId) {
		loopPaymentId = paymentId;
		loopPayment = null;
		if (dispatcher.canDeletePayments())
			dao.delete(Payment.class, paymentId);
		else
			loopPayment = dao.find(Payment.class, loopPaymentId);
		renderer.addRender("paymentBody" + paymentId, paymentZone);
	}

	@Inject
	private CRUDServiceDAO dao;

	void onSuccessFromPaymentEditor() {
		dao.update(loopPayment);
		editing = false;
		renderer.addRender("paymentBody" + loopPayment.getId(), paymentZone);
	}

	public String getPagerPosition() {
		return switchPages ? "top" : "none";
	}

	public int getRows() {
		return switchPages ? 20 : 100000;
	}
}
