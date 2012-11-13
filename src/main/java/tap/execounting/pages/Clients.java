package tap.execounting.pages;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.dal.mediators.interfaces.ClientMed;
import tap.execounting.dal.mediators.interfaces.ContractMed;
import tap.execounting.data.ClientState;
import tap.execounting.data.selectmodels.ContractTypeIdSelectModel;
import tap.execounting.entities.Client;
import tap.execounting.entities.Contract;
import tap.execounting.entities.ContractType;
import tap.execounting.entities.Payment;
import tap.execounting.services.SuperCalendar;

@Import(stylesheet = "context:layout/filtertable.css")
public class Clients {

	@Inject
	private Session session;
	@Component
	private Zone gridZone;
	@Component
	private Zone statZone;
	@Inject
	private Request request;
	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;
	@Inject
	private CRUDServiceDAO dao;
	@Inject
	private SuperCalendar calendar;

	@Property
	private SelectModel contractTypeIdsModel;

	@Inject
	private Block plannedPayments;

	// Mediators
	@Inject
	private ClientMed clientMed;
	@Inject
	private ContractMed contractMed;

	// filtering fields

	// planned payments
	@Property
	@Persist
	private Date earlyDate;
	@Property
	@Persist
	private Date laterDate;
	// date of first contract
	@Property
	@Persist
	private Date fcEarlyDate;
	@Property
	@Persist
	private Date fcLaterDate;
	// Date of any contract

	// Before
	@Property
	@Persist
	private Date acDate1;
	// After
	@Property
	@Persist
	private Date acDate2;
	// misc
	@Property
	@Persist
	private String name;
	@Property
	@Persist
	private ClientState state;
	@Property
	@Persist
	private Integer contractTypeId;

	// financial statistics filters and etc
	@Property
	@Persist
	private Date pfEarlierDate;
	@Property
	@Persist
	private Date pfLaterDate;

	private ClientMed getClientMed() {
		return clientMed;
	}

	@Persist
	private List<Client> clients;

	@SuppressWarnings("unchecked")
	public List<Client> getClients() {
		if (clients != null)
			return new ArrayList<Client>(clients);
		List<Client> cs;
		Criteria criteria = session.createCriteria(Client.class);

		// filter status
		boolean filterOnPlannedPayments = earlyDate != null
				|| laterDate != null;
		boolean filterOnFCDate = fcEarlyDate != null || fcLaterDate != null;
		boolean filterOnACDate = acDate1 != null || acDate2 != null;
		boolean filterOnNames = name != null && name.length() > 1;
		boolean filterOnState = state != null;
		boolean filterOnContractType = contractTypeId != null;

		cs = criteria.list();

		// Scheduled payments filtration
		if (filterOnPlannedPayments) {
			for (int i = cs.size() - 1; i >= 0; i--) {
				Client client = cs.get(i);
				boolean pass = false;

				for (Payment p : client.getPlannedPayments()) {
					pass = true;
					if (earlyDate != null)
						pass &= earlyDate.before(p.getDate());
					if (laterDate != null)
						pass &= laterDate.after(p.getDate());
					if (pass)
						break;
				}
				if (!pass)
					cs.remove(i);
			}
		}
		// First Contract Date filtration
		if (filterOnFCDate) {
			for (int i = cs.size() - 1; i >= 0; i--) {
				Client client = cs.get(i);
				boolean pass = false;
				Date d = client.getFirstContractDate();
				if (d != null) {
					pass = true;

					if (fcEarlyDate != null)
						pass &= fcEarlyDate.before(d);
					if (fcLaterDate != null)
						pass &= fcLaterDate.after(d);
				} else
					pass = false;
				if (!pass)
					cs.remove(i);
			}
		}

		// Any contract Date filter
		if (filterOnACDate) {
			contractMed.reset();
			List<Client> toRetain = contractMed.filter(acDate1, acDate2)
					.getClients();
			cs.retainAll(toRetain);
		}

		// Name Filtration
		if (filterOnNames) {
			for (int i = cs.size() - 1; i >= 0; i--) {
				if (!cs.get(i).getName().toLowerCase()
						.contains(name.toLowerCase()))
					cs.remove(i);
			}
		}
		// Stud status
		if (filterOnState)
			getClientMed().setGroup(cs).filter(state);
		// Contract Type
		if (filterOnContractType) {
			for (int i = cs.size() - 1; i >= 0; i--) {
				boolean match = false;
				for (Contract c : cs.get(i).getActiveContracts())
					if (c.getContractTypeId() == contractTypeId) {
						match = true;
						break;
					}
				if (!match)
					cs.remove(i);
			}
		}
		clients = cs;
		return getClients();
	}

	public Block getFilter() {
		return plannedPayments;
	}

	// aggregate fields
	public int getActiveContracts() {
		int sum = 0;
		for (Client c : getClients())
			for (Contract t : c.getContracts())
				if (t.isActive())
					sum++;
		return sum;
	}

	public int getFreezedContracts() {
		int sum = 0;
		for (Client c : getClients())
			for (Contract t : c.getContracts())
				if (t.isFreeze())
					sum++;
		return sum;
	}

	public int getTotalContracts() {
		int sum = 0;
		for (Client c : getClients())
			sum += c.getContracts().size();
		return sum;
	}

	public int getNewClients() {
		clientMed.setGroup(getClients());
		return clientMed.countNewbies(null, null);
	}

	public int getExpClients() {
		return getClientMed().setGroup(getClients())
				.countContinuers(null, null);
	}

	public int getTotalClients() {
		return getClients().size();
	}

	// filtered
	private List<Payment> getPayments() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		Date paramEarlier, paramLater;
		if (pfEarlierDate == null) {
			calendar.setTime(new Date());
			calendar.addDays(-5000);
			paramEarlier = calendar.getTime();
		} else
			paramEarlier = pfEarlierDate;
		if (pfLaterDate == null) {
			calendar.setTime(new Date());
			calendar.addDays(5000);
			paramLater = calendar.getTime();
		} else
			paramLater = pfLaterDate;

		params.put("earlierDate", paramEarlier);
		params.put("laterDate", paramLater);
		List<Payment> payments = null;
		try {
			payments = dao.findWithNamedQuery(Payment.BY_DATES, params);
		} catch (HibernateException e) {
			e.printStackTrace();
		}
		return payments;
	}

	public int getTotalPayments() {
		int summ = 0;
		for (Payment p : getPayments()) {
			if (!p.isScheduled())
				summ += p.getAmount();
		}

		return summ;
	}

	public int getCreditorsDebt() {
		int debt = 0;
		for (Client c : getClients()) {
			int bal = c.getBalance();
			if (bal > 0)
				debt += bal;
		}
		return debt;
	}

	// events
	void onSubmitFromFilterForm() {
		clients = null;
		if (request.isXHR())
			ajaxResponseRenderer.addRender(gridZone).addRender(statZone);
	}

	void setupRender() {
		@SuppressWarnings("unchecked")
		List<ContractType> types = session.createCriteria(ContractType.class)
				.list();
		contractTypeIdsModel = new ContractTypeIdSelectModel(types);
	}
}
