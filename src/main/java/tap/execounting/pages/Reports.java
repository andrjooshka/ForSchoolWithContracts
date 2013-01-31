package tap.execounting.pages;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.RequestParameter;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.hibernate.Session;

import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.dal.QueryParameters;
import tap.execounting.dal.mediators.interfaces.ClientMed;
import tap.execounting.dal.mediators.interfaces.ContractMed;
import tap.execounting.dal.mediators.interfaces.EventMed;
import tap.execounting.data.ClientState;
import tap.execounting.data.EventState;
import tap.execounting.entities.Client;
import tap.execounting.entities.Comment;
import tap.execounting.entities.Contract;
import tap.execounting.entities.Event;
import tap.execounting.entities.Payment;
import tap.execounting.security.AuthorizationDispatcher;
import tap.execounting.services.DateService;
import tap.execounting.services.SuperCalendar;

@Import(library = { "context:js/jquery-1.8.3.min.js",
		"context:js/reportsAjax.js" }, stylesheet = {
		"context:css/datatable.css", "context:css/reports.css", "context:css/comments.css" })
public class Reports {

	// Activation context
	// Screen fields
	// Generally useful bits and pieces
	@Inject
	private Session session;
	@Inject
	private Messages messages;
	@Inject
	private AjaxResponseRenderer renderer;
	@Inject
	private AuthorizationDispatcher dispatcher;
	@Inject
	private ClientMed clientMed;
	@Inject
	private ContractMed contractMed;
	@Inject
	private EventMed eventMed;
	@Inject
	private ComponentResources resources;

	// Page components
	@Component
	private Zone paymentZone;
	@Inject
	private BeanModelSource beanModelSource;
	@Property
	private BeanModel<Contract> modelOfEnding;
	@Property
	private BeanModel<Client> modelOfEnded;
	@Property
	private BeanModel<Client> modelOfPayments;
	@Property
	private BeanModel<Client> modelOfDebtors;
	@Property
	private BeanModel<Client> modelOfFrozen;
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
	@Property
	private Contract contract;
	@InjectPage
	private ClientPage clientPage;

	// The code

	// // Render setup
	void setupRender() {
		// end of subscription
		if (modelOfEnding == null) {
			modelOfEnding = beanModelSource.createDisplayModel(Contract.class,
					componentResources.getMessages());
			modelOfEnding.add("name", null);
			modelOfEnding.add("endingInfo", null);
			modelOfEnding.add("lastEventDate", null);
			modelOfEnding.exclude("id", "balance", "gift", "discount", "false",
					"lessonsNumber", "active", "state", "clientId", "typeId",
					"teacherId", "freeze", "canceled", "contractTypeId",
					"money", "moneyPaid", "complete","paid","giftMoney", "completelessonsCost",
					"completeLessonsCost", "singleLessonCost", "date", "lessonsRemain");
			modelOfEnding.reorder("name", "endingInfo", "lastEventDate");
		}

		// subscription ended
		if (modelOfEnded == null) {
			modelOfEnded = beanModelSource.createDisplayModel(Client.class,
					componentResources.getMessages());
			modelOfEnded.add("endedInfo", null);
			modelOfEnded.add("comment", null);
			modelOfEnded.exclude("return", "date", "id", "balance",
					"studentInfo", "firstContractDate", "state",
					"firstPlannedPaymentDate");
		}

		// soon payments
		if (modelOfPayments == null) {
			modelOfPayments = beanModelSource.createDisplayModel(Client.class,
					componentResources.getMessages());
			modelOfPayments.add("paymentsInfo", null);
			modelOfPayments.add("comment", null);
			modelOfPayments.exclude("return", "date", "id", "balance",
					"studentInfo", "firstContractDate", "state");
		}

		// frozen guys
		if (modelOfFrozen == null) {
			modelOfFrozen = beanModelSource.createDisplayModel(Client.class,
					componentResources.getMessages());
			modelOfFrozen.add("comment", null);
			modelOfFrozen.exclude("return", "date", "id", "balance",
					"studentInfo", "firstContractDate", "state",
					"firstPlannedPaymentDate");
		}

		// debtors
		if (modelOfDebtors == null) {
			modelOfDebtors = beanModelSource.createDisplayModel(Client.class,
					componentResources.getMessages());
			modelOfDebtors.add("debtInfo", null);
			modelOfDebtors.add("comment", null);
			modelOfDebtors.exclude("return", "date", "id", "balance",
					"studentInfo", "firstContractDate", "state",
					"firstPlannedPaymentDate");
		}
	}

	public JSONObject onAJpoll(@RequestParameter("timeStamp") long timestamp) {
		JSONObject js = new JSONObject("{'status':'ok'}");
		List<Comment> list = dao.findWithNamedQuery(Comment.CLIENT_AFTER_DATE,
				QueryParameters.with("date", new Date(timestamp)).parameters());
		if (list.size() > 0) {
			JSONArray jr = new JSONArray();
			for (Comment c : list)
				jr.put(new JSONObject("id", c.getEntityId() + "", "comment", c
						.getText(), "timeStamp", c.getDate().getTime() + ""));
			js.put("updates", jr);
		}
		return js;
	}

	public JSONObject onAJ(@RequestParameter("id") int id,
			@RequestParameter("comment") String text,
			@RequestParameter("timeStamp") long timeStamp) {

		clientMed.setUnitId(id).comment(text, timeStamp);
		JSONObject js = new JSONObject("{'status':'ok'}");
		return js;
	}

	public String getComment() {
		Comment c = clientMed.setUnit(client).getComment();
		return c == null ? "" : c.getText();
	}

	public Date getCommentDate() {
		Comment c = clientMed.setUnit(client).getComment();
		return c == null ? null : c.getDate();
	}

	// TODO refactor candidate, should be pushed into different package
	private void nameSort(List<Client> clients) {
		Client t;
		for (int i = 0; i < clients.size(); i++)
			for (int j = clients.size() - 1; j > i; j--) {
				if (clients.get(i).getName()
						.compareTo(clients.get(j).getName()) > 0) {
					t = clients.get(i);
					clients.set(i, clients.get(j));
					clients.set(j, t);
				}
			}
	}

	// //// getters
	// TODO check if SQL will work better
	// Question is: should we remember about frozen contracts?
	public List<Contract> getEndingContracts() {
		int minLessonsRemaining = 1;
		List<Contract> list = getAllContracts();
		// complete filter
		for (int i = list.size() - 1; i >= 0; i--)
			if (list.get(i).isComplete())
				list.remove(i);
		// remaining lessons
		for (int i = list.size() - 1; i >= 0; i--)
			if (list.get(i).getLessonsRemain() > minLessonsRemaining || list.get(i).isCanceled())
				list.remove(i);

		// Sort
		contractMed.setGroup(list).sortByDate(false);
		list = contractMed.getGroup();
		return list;
	}

	public List<Client> getEndedContracts() {
		clientMed.reset();
		List<Client> list = clientMed.filter(ClientState.undefined).getGroup(
				true);
		// Sort
		nameSort(list);
		return list;
	}

	public String getEndedInfo() {
		// TODO optimize that by caching recent events
		if (client.getContracts().size() == 0)
			return messages.get("no-contracts");
		Event lastEvent = null;
		List<Event> cache = eventMed.filter(DateService.fromNowPlusDays(-31),
				DateService.fromNowPlusDays(1)).getGroup();
		try {
			lastEvent = eventMed
					.setGroup(cache.subList(0, eventMed.countGroupSize()))
					.filter(client).sortByDate(false).getGroup().get(0);
		} catch (IndexOutOfBoundsException e) {
			return messages.get("no-events");
		} finally {
			eventMed.reset();
		}
		String result = DateService.formatDayMonthNameYear(lastEvent.getDate());
		result += "\t" + lastEvent.getEventType().getTitle();
		return result;
	}

	public String getEndingInfo() {
		StringBuilder builder = new StringBuilder();
		
		if (!contract.isComplete() && contract.getLessonsRemain() < 3) {
			builder.append(contract.getEventType().getTitle() + ": ");
			builder.append(contract.getLessonsRemain());
			if (contract.getScheduledLessons().size() > 0)
				builder.append(" и " + contract.getScheduledLessons().size()
						+ " уже запланировано");
		}
		if (builder.substring(builder.length() - 2).equals(", "))
			builder.replace(builder.length() - 2, builder.length(), "");
		return builder.toString();
	}
	
	public Date getLastEventDate(){
		for(Event e : contract.getEvents())
			if(e.getState()==EventState.planned)
				return e.getDate();
		return null;
	}
	
	public Date getCommentDateFromContract(){
		Comment c = clientMed.setUnit(contract.getClient()).getComment();
		return c == null ? null : c.getDate();
	}
	public String getCommentFromContract(){
		Comment c = clientMed.setUnit(contract.getClient()).getComment();
		return c == null ? "" : c.getText();
	}

	public List<Client> getFrozenClients() {
		clientMed.reset();
		List<Client> list = clientMed.filter(ClientState.frozen).getGroup(true);
		// Sort
		nameSort(list);
		return list;
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

		List<Client> clientlist = new ArrayList<Client>(set);

		// Sort
		nameSort(clientlist);
		return clientlist;
	}

	public List<Client> getDebtors() {
		@SuppressWarnings("unchecked")
		List<Client> list = session.createCriteria(Client.class).list();
		for (int i = list.size() - 1; i >= 0; i--)
			if (list.get(i).getBalance() >= 0)
				list.remove(i);

		// Sort
		nameSort(list);
		return list;
	}

	public String getDebtInfo() {
		return client.getBalance() * (-1) + "";
	}

	ClientPage onDetails(Client c) {
		clientPage.setup(c);
		return clientPage;
	}

	private List<Contract> getAllContracts() {
		return contractMed.getAllContracts();
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

	public Block getDeleteBlock() {
		if (dispatcher.canDeletePayments())
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
		return switchPages ? "both" : "none";
	}

	public int getRows() {
		return switchPages ? 20 : 100000;
	}
}
