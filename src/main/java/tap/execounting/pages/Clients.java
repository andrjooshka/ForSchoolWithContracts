package tap.execounting.pages;

import java.util.Date;
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
import org.hibernate.Session;

import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.dal.mediators.interfaces.ClientMed;
import tap.execounting.dal.mediators.interfaces.ContractMed;
import tap.execounting.dal.mediators.interfaces.PaymentMed;
import tap.execounting.data.ClientState;
import tap.execounting.data.ContractState;
import tap.execounting.data.selectmodels.ContractTypeIdSelectModel;
import tap.execounting.entities.Client;
import tap.execounting.entities.Contract;
import tap.execounting.entities.ContractType;
import tap.execounting.services.SuperCalendar;

@Import(stylesheet = "context:css/filtertable.css")
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
	@Inject
	private PaymentMed paymentMed;

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
	@Property
	@Persist
	private Integer contractTypeId;

	// misc
	// Client name
	@Property
	@Persist
	private String name;
	@Property
	@Persist
	private ClientState state;
	// Status acquisition date1
	@Property
	@Persist
	private Date sa1;
	// Status acquisition date2
	@Property
	@Persist
	private Date sa2;

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
	private List<Client> clientsCache;
	@Persist
	private List<Contract> contractsCache;

	public List<Client> getClients() {
		return clientsCache;
	}

	// @SuppressWarnings("unchecked")
	// Basically, this is the main method for this page.
	// It does filtering and assembling of all client and contract data.
	// At first, client filters are acting:
	// 1) Name
	// 2) State
	// 3) First contract date
	// Then contract filters are applied:
	// 1) Contract type
	// 2) Contracts date
	// Contract filters are applied to clear contractMed.
	// Remaining contracts are converted to clients.
	// Then function retains all the clients from first group, which are listed
	// in the second;
	// Intersection operation it is actually.
	public void dataInit() {
		// Little set up
		List<Client> clients;
		clientMed.reset();
		contractMed.reset();
		paymentMed.reset();

		// Boolean flags processing for filter status
		// Filter on date of first contract
		boolean filterOnFCDate = fcEarlyDate != null || fcLaterDate != null;
		// Filter on date of any contract
		boolean filterOnACDate = acDate1 != null || acDate2 != null;
		// Filter on name
		boolean filterOnNames = name != null && name.length() > 1;
		// Filter on client state
		boolean filterOnState = state != null;
		// Filter on contract type id
		boolean filterOnContractType = contractTypeId != null;
		boolean filterOnPaymentsDate = pfEarlierDate != null
				|| pfLaterDate != null;

		// Client filters
		// First Contract Date filtration
		if (filterOnFCDate)
			clientMed.filterDateOfFirstContract(fcEarlyDate, fcLaterDate);

		// Name Filtration
		if (filterOnNames)
			clientMed.filterName(name);

		// Stud status
		if (filterOnState) { // OLDE CODE
								// clientMed.filter(state);
								// new code calculates also when client has
								// acquired this status
			// Continuer status acquisition

			if (state == ClientState.beginner)
				clientMed.becameNovices(sa1, sa2);
			else if (ClientState.continuer == state)
				clientMed.becameContinuers(sa1, sa2);
			else if (state == ClientState.trial)
				clientMed.becameTrials(sa1, sa2);
			else clientMed.filter(state);
		}
		// Now Clients are fresh filtered and actual
		// Setup 2. We could set up contracts
		// Group 1
		clients = clientMed.getGroup();
		contractMed.setGroupFromClients(clients);

		// Contract filters
		// Any contract Date filter
		if (filterOnACDate || filterOnContractType) {
			if (filterOnACDate)
				contractMed.filter(acDate1, acDate2);
			// Contract Type
			if (filterOnContractType)
				contractMed.filterByContractType(contractTypeId);
			// UPDATE CLIENTS
			clients = contractMed.getClients();
		}
		// Setup 3
		paymentMed.setGroupFromContracts(contractMed.getGroup());
		
		// Payment filter
		// Which also does intersection operation as contractmed
		if(filterOnPaymentsDate){
			paymentMed.filter(pfEarlierDate, pfLaterDate);
			// Transform these into contracts
			contractMed.retain(paymentMed.getContracts());
			// update clients
			clients = contractMed.getClients();
		}
		// clients = contractMed.getClients();

		// Cache this contracts and clients for further explorations
		clientsCache = clients;
		contractsCache = contractMed.getGroup();
		clientMed.setGroup(clients);
	}

	public Block getFilter() {
		return plannedPayments;
	}

	// aggregate fields
	public int getActiveContracts() {
		return contractMed.count(ContractState.active);
	}

	public int getFreezedContracts() {
		return contractMed.count(ContractState.frozen);
	}

	// TODO remove candidate
	public int getTotalContracts() {
		// NEW VERSION count all contracts of selected clients
		// with contract date filter applied
		return contractsCache.size();
	}

	// TODO REMOVE
	public int getNewClients() {
		return clientMed.countNewbies(null, null);
	}

	// TODO REMOVE
	public int getExpClients() {
		return getClientMed().setGroup(getClients())
				.countContinuers(null, null);
	}

	public int getTotalClients() {
		return clientsCache.size();
	}

	public int getRealPayments() {
		return paymentMed.countRealPaymentsAmount();
	}
	
	public int getScheduledPayments(){
		return paymentMed.countScheduledPaymentsAmount();
	}

	public int getCreditorsDebt() {
		int debt = 0;
		for (Client c : clientsCache) {
			int bal = c.getBalance();
			if (bal > 0)
				debt += bal;
		}
		return debt;
	}
	
	public int getCertificateMoney(){
		return contractMed.countCertificateMoney();
	}

	// events
	void onSubmitFromFilterForm() {
		// Reset fields
		clientsCache = null;
		contractsCache = null;
		if (request.isXHR())
			ajaxResponseRenderer.addRender(gridZone).addRender(statZone);
	}

	void setupRender() {
		// Call it to set up the cache.
		dataInit();
		@SuppressWarnings("unchecked")
		List<ContractType> types = session.createCriteria(ContractType.class)
				.list();
		contractTypeIdsModel = new ContractTypeIdSelectModel(types);
	}
}
