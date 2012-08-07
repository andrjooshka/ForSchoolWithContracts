package tap.execounting.dal.mediators;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.dal.QueryParameters;
import tap.execounting.dal.mediators.interfaces.ClientMed;
import tap.execounting.dal.mediators.interfaces.ContractMed;
import tap.execounting.dal.mediators.interfaces.DateFilter;
import tap.execounting.dal.mediators.interfaces.PaymentMed;
import tap.execounting.data.ClientState;
import tap.execounting.data.ContractState;
import tap.execounting.entities.Client;
import tap.execounting.entities.Contract;
import tap.execounting.entities.ContractType;
import tap.execounting.entities.Payment;
import tap.execounting.entities.Teacher;
import tap.execounting.entities.interfaces.Dated;

public class ClientMediator implements ClientMed {

	@Inject
	private CrudServiceDAO dao;
	@Persist
	private CrudServiceDAO sureDao;

	@Inject
	private ContractMed contractMed;
	private ContractMed sureContractMed;

	@Inject
	private PaymentMed paymentMed;
	private PaymentMed surePaymentMed;

	@Inject
	private DateFilter dateFilter;

	private Client unit;

	public ClientMed setDao(CrudServiceDAO dao) {
		this.sureDao = dao;
		return this;
	}

	private CrudServiceDAO getDao() {
		return sureDao == null ? dao : sureDao;
	}

	private ContractMed getContractMed() {
		if (sureContractMed != null)
			return sureContractMed;
		if (contractMed == null) {
			sureContractMed = new ContractMediator();
			sureContractMed.setDao(getDao());
			return sureContractMed;
		} else
			return contractMed;
	}

	private PaymentMed getPaymentMed() {
		if (paymentMed == null) {
			surePaymentMed = new PaymentMediator();
			surePaymentMed.setDao(getDao());
			return surePaymentMed;
		} else
			return paymentMed;
	}

	public Client getUnit() {
		try {
			return unit;
		} catch (NullPointerException npe) {
			return null;
		}
	}

	public ClientMed setUnit(Client unit) {
		this.unit = unit;
		return this;
	}

	public boolean hasContracts() {
		try {
			return getContracts().size() > 0;
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return false;
		}
	}

	public List<Contract> getContracts() {
		try {
			// MAP IT!!!
			List<Contract> res = getDao()
					.findWithNamedQuery(
							Contract.WITH_CLIENT,
							QueryParameters.with("clientId", unit.getId())
									.parameters());
			return res;
			// return unit.getContracts();
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}
	}

	public boolean hasActiveContracts() {
		ContractMed contractMed = getContractMed();
		boolean response = contractMed.setGroup(getContracts())
				.filter(ContractState.active).countGroupSize() > 0;
		contractMed.reset();
		return response;
	}

	public List<Contract> getActiveContracts() {
		try {
			ContractMed contractMed = getContractMed();
			return contractMed.setGroup(getContracts())
					.filter(ContractState.active).getGroup();
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}
	}

	public boolean hasFrozenContracts() {
		ContractMed contractMed = getContractMed();
		boolean response = contractMed.setGroup(getContracts())
				.filter(ContractState.frozen).countGroupSize() > 0;
		contractMed.reset();
		return response;
	}

	public List<Contract> getFrozenContracts() {
		try {
			ContractMed contractMed = getContractMed();
			return contractMed.setGroup(getContracts())
					.filter(ContractState.frozen).getGroup();
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}
	}

	public boolean hasCanceledContracts() {
		ContractMed contractMed = getContractMed();
		boolean response = contractMed.setGroup(getContracts())
				.filter(ContractState.canceled).countGroupSize() > 0;
		contractMed.reset();
		return response;
	}

	public List<Contract> getCanceledContracts() {
		try {
			ContractMed contractMed = getContractMed();
			return contractMed.setGroup(getContracts())
					.filter(ContractState.canceled).getGroup();
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}
	}

	public boolean hasTrialContracts() {
		for (Contract c : getContracts())
			if (c.getContractTypeId() == ContractType.Trial)
				return true;
		return false;
	}

	public List<Contract> getTrialContracts() {
		ContractMed contractMed = getContractMed();
		return contractMed.setGroup(getContracts())
				.filterByContractType(ContractType.Trial).getGroup();
	}

	public boolean hasFinishedContracts() {
		ContractMed contractMed = getContractMed();
		boolean response = contractMed.setGroup(getContracts())
				.filter(ContractState.complete).countGroupSize() > 0;
		contractMed.reset();
		return response;
	}

	public List<Contract> getFinishedContracts() {
		try {
			ContractMed contractMed = getContractMed();
			return contractMed.setGroup(getContracts())
					.filter(ContractState.complete).getGroup();
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}
	}

	public int getBalance() {
		return unit.getBalance();
	}

	public ClientState getState() {
		// if client is canceled - he have special mark
		if (unit.isCanceled())
			return ClientState.canceled;
		// check if client is active
		if (hasActiveContracts()) {
			// if he is active, he is one of three:
			// trial | beginner | continuer
			boolean notOnlyTrial = false;
			for (Contract c : getActiveContracts())
				if (c.getContractTypeId() != ContractType.Trial) {
					notOnlyTrial = true;
					break;
				}
			if (!notOnlyTrial)
				return ClientState.trial;
			if (hasFinishedContracts()) {
				notOnlyTrial = false;
				for (Contract c : getFinishedContracts())
					if (c.getContractTypeId() != ContractType.Trial) {
						notOnlyTrial = true;
						break;
					}
				return notOnlyTrial ? ClientState.continuer
						: ClientState.beginner;
			}
			return ClientState.beginner;
		}
		if (hasFrozenContracts())
			return ClientState.frozen;

		return ClientState.undefined;
	}

	public void cancelClient() {
		unit.setCanceled(true);
	}

	public Date getDateOfFirstContract() {
		try {
			return unit.getFirstContractDate();
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}
	}

	public List<Teacher> getActiveTeachers() {
		List<Contract> contracts = getActiveContracts();
		if (contracts == null || contracts.size() == 0)
			return null;
		Set<Teacher> res = new HashSet<Teacher>(5);
		for (Contract c : contracts)
			res.add(c.getTeacher());
		return new ArrayList<Teacher>(res);
	}

	public int getReturn() {
		List<Contract> t = getContracts();
		if (t == null || t.size() == 0)
			return 0;
		int summ = 0;
		for (Contract c : t)
			for (Payment p : c.getPayments())
				if (!p.isScheduled())
					summ += p.getAmount();
		return summ;
	}

	private List<Client> cache;
	private Map<String, Object> appliedFilters;

	private Map<String, Object> getAppliedFilters() {
		if (appliedFilters == null)
			appliedFilters = new HashMap<String, Object>(5);
		return appliedFilters;
	}

	private void load() {
		cache = getDao().findWithNamedQuery(Client.ALL);
		appliedFilters = new HashMap<String, Object>();
	}

	public void reset() {
		cache = null;
		appliedFilters = null;
	}

	public List<Client> getGroup() {
		if (cache == null)
			load();
		return cache;
	}

	public List<Client> getGroup(boolean reset) {
		if (cache == null)
			load();
		List<Client> innerCache = cache;
		if (reset)
			reset();
		return innerCache;
	}

	public ClientMediator setGroup(List<Client> group) {
		cache = group;
		return this;
	}

	public List<Client> getAllClient() {
		return getDao().findWithNamedQuery(Client.ALL);
	}

	public String getFilterState() {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, Object> entry : getAppliedFilters().entrySet())
			sb.append(entry.getKey() + ": " + entry.getValue().toString()
					+ "\n");
		return sb.toString();
	}

	public ClientMed filter(ClientState state) {
		getAppliedFilters().put("ClientState", state);
		List<Client> cache = getGroup();

		// save current unit
		Client unit = getUnit();

		// filter
		if (state != ClientState.active)
			for (int i = cache.size() - 1; i >= 0; i--) {
				setUnit(cache.get(i));
				if (getState() == state)
					continue;
				cache.remove(i);
			}
		else
			for (int i = cache.size() - 1; i >= 0; i--) {
				setUnit(cache.get(i));
				ClientState cs = getState();
				if (cs == ClientState.beginner || cs == ClientState.continuer
						|| cs == ClientState.trial)
					continue;
				cache.remove(i);
			}

		// restore unit
		setUnit(unit);
		return this;
	}

	public ClientMed filterByActiveTeacher(Teacher teacher) {
		getAppliedFilters().put("Active teacher", teacher);

		List<Contract> contractsCache = getContractMed().filter(teacher)
				.filter(ContractState.active).getGroup();
		getContractMed().reset();
		cache = toClients(contractsCache);
		return this;
	}

	private List<Client> toClients(List<Contract> contracts) {
		Set<Client> list = new HashSet<Client>();
		for (Contract c : contracts)
			list.add(c.getClient());
		return new ArrayList<Client>(list);
	}

	public ClientMed filterDateOfFirstContract(Date date1, Date date2) {
		// TODO we can do this through hsql
		getAppliedFilters().put("Date of first contract 1", date1);
		getAppliedFilters().put("Date of first contract 2", date2);
		List<Client> cache = getGroup();
		List<Client> cache2 = new ArrayList<Client>();
		for (Dated item : dateFilter.filterWithReturn(cache, date1, date2))
			cache2.add((Client) item);
		setGroup(cache2);
		return this;
	}

	public ClientMed filterDateOfPlannedPayments(Date date1, Date date2) {
		// TODO we can do this through hsql
		getAppliedFilters().put("Date of planned payments 1", date1);
		getAppliedFilters().put("Date of planned payments 2", date2);
		List<Payment> payments;
		PaymentMed paymentMed = getPaymentMed();

		if (cache != null) {
			List<Client> cache = getGroup();

			// extract payments of current client group
			payments = new ArrayList<Payment>();
			for (Client c : cache)
				for (Contract con : c.getContracts())
					payments.addAll(con.getPayments());
			paymentMed.setGroup(payments);
		}

		payments = paymentMed.filter(true).filter(date1, date2).getGroup();
		Set<Client> clients = new HashSet<Client>();
		for (Payment p : payments)
			clients.add(p.getContract().getClient());
		setGroup(new ArrayList<Client>(clients));
		return this;
	}

	public ClientMed filterName(String name) {
		getAppliedFilters().put("Client name", name);
		if (cache == null) {
			cache = getDao().findWithNamedQuery(Client.BY_NAME,
					QueryParameters.with("name", name).parameters());
			return this;
		}
		List<Client> cache = getGroup();
		Client c;
		for (int i = cache.size() - 1; i >= 0; i--) {
			c = cache.get(i);
			if (!c.getName().toLowerCase().contains(name.toLowerCase()))
				cache.remove(i);
		}
		return this;
	}

	public List<Client> getDebtors() {
		List<Client> cache = getAllClient();
		Client c;
		for (int i = cache.size() - 1; i >= 0; i--) {
			c = cache.get(i);
			if (c.getBalance() >= 0 || c.isCanceled())
				cache.remove(i);
		}
		return cache;
	}

	public List<Client> getClientsWithExpiringContracts() {
		ContractMed contractMed = getContractMed();
		List<Contract> contracts = contractMed.filter(2).getGroup();
		Set<Client> clients = new HashSet<Client>();
		for (Contract c : contracts)
			clients.add(c.getClient());
		return new ArrayList<Client>(clients);
	}

	public Integer countGroupSize() {
		return getGroup().size();
	}

	public Integer count(ClientState state) {
		ClientMediator cm = new ClientMediator();
		return cm.filter(state).countGroupSize();
	}

	public Integer count(ClientState state, Date date1, Date date2) {
		ClientMediator cm = new ClientMediator();
		return cm.filter(state).filterDateOfFirstContract(date1, date2)
				.countGroupSize();
	}

	public Integer countContinuers(Date date1, Date date2) {
		return count(ClientState.continuer, date1, date2);
	}

	public Integer countNewbies(Date date1, Date date2) {
		return count(ClientState.beginner, date1, date2);
	}

	public Integer countTrial(Date date1, Date date2) {
		return count(ClientState.trial, date1, date2);
	}

	public Integer countCanceled(Date date1, Date date2) {
		return count(ClientState.canceled, date1, date2);
	}

	public Integer countUndefined(Date date1, Date date2) {
		return count(ClientState.undefined, date1, date2);
	}

	public Integer countFrozen(Date date1, Date date2) {
		return count(ClientState.frozen, date1, date2);
	}

}
