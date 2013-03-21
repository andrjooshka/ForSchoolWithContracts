package tap.execounting.dal.mediators;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.ChainMap;
import tap.execounting.dal.mediators.interfaces.ClientMed;
import tap.execounting.dal.mediators.interfaces.ContractMed;
import tap.execounting.dal.mediators.interfaces.DateFilter;
import tap.execounting.dal.mediators.interfaces.PaymentMed;
import tap.execounting.data.ClientState;
import tap.execounting.data.ContractState;
import tap.execounting.entities.Client;
import tap.execounting.entities.Comment;
import tap.execounting.entities.Contract;
import tap.execounting.entities.ContractType;
import tap.execounting.entities.Payment;
import tap.execounting.entities.Teacher;
import tap.execounting.entities.interfaces.Dated;
import tap.execounting.security.AuthorizationDispatcher;
import tap.execounting.services.Authenticator;
import tap.execounting.services.DateService;

import static tap.execounting.data.ContractState.active;
import static tap.execounting.data.ContractState.frozen;

public class ClientMediator extends ProtoMediator<Client> implements ClientMed {
	@Inject
	private ContractMed contractMed;
	@Inject
	private PaymentMed paymentMed;
	@Inject
	private Authenticator authenticator;
	@Inject
	private AuthorizationDispatcher dispatcher;

    public ClientMediator(){
        clazz = Client.class;
    }

	private ContractMed getContractMed() {
		return contractMed;
	}

	private PaymentMed getPaymentMed() {
		return paymentMed;
	}

	public ClientMed setUnit(Client unit) {
		this.unit = unit;
		return this;
	}

	public ClientMed setUnitById(int id) {
		this.unit = dao.find(Client.class, id);
		return this;
	}

	public void delete(Client c) {
		// AUTHORIZATION MOUNT POINT DELETE
		if (dispatcher.canDeleteClients())
			if (c.getContracts().size() > 0)
				// TODO JAVASCRIPT ALERT MOUNT POINT
				throw new IllegalArgumentException(
						"У данного клиента заключены с вами договора, пожалуйста сначала удалите их.");
			else {
				c.setName(c.getName() + " [deleted]");
				dao.update(c);
				dao.delete(Client.class, c.getId());
			}
	}

	public void comment(String text, long time) {
		// if text=="null" (a string) comment will be deleted
		// TODO upgrade later to support multiple comments for one client
		Comment c = getComment();
		if (c == null) {
			if (!text.equals("null")) {
				c = new Comment(Comment.ClientCode, authenticator
						.getLoggedUser().getId(), unit.getId());
				c.setText(text);
				c.setDate(new Date(time));
				dao.create(c);
			}
		} else {
			if (text.equals("null"))
				dao.delete(Comment.class, c.getId());
			else {
				c.setText(text);
				c.setDate(new Date(time));
				dao.update(c);
			}
		}

	}

	public Comment getComment() {
		// NEW VERSION
		Comment c = dao.findUniqueWithNamedQuery(Comment.BY_CLIENT_ID,
				ChainMap.with("id", unit.getId()).yo());
		return c;
	}

    public void setClientComment(String comment){
        unit.setComment(comment);
        unit.setCommentDate(new Date());
        dao.update(unit);
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
			return unit.getContracts();
		} catch (Exception e) {
			e.printStackTrace();
			setUnitById(unit.getId());
			return unit.getContracts();
		}
	}

	public boolean hasActiveContracts() {
		boolean response = contractMed.setGroup(getContracts()).count(active) > 0;
		contractMed.reset();
		return response;
	}

    /**
     * @return list of active contracts of the current unit
     */
    public List<Contract> getActiveContracts() {
		try {
			return contractMed.setGroup(new ArrayList(getContracts()))
					.retainByState(ContractState.active).getGroup(true);
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}
	}

    /**
     * @return true if client has frozen contracts
     */
	public boolean hasFrozenContracts() {
		boolean response = contractMed.setGroup(getContracts()).count(frozen) > 0;
		contractMed.reset();
		return response;
	}

	public ClientMed becameContinuers(Date date1, Date date2) {
		List<Client> list = getGroup();
		for (int i = list.size() - 1; i >= 0; i--) {
			setUnit(list.get(i));
			if (!becameContinuer(date1, date2))
				list.remove(i);
		}
		return this;
	}

	public ClientMed becameNovices(Date date1, Date date2) {
		List<Client> list = getGroup();
		for (int i = list.size() - 1; i >= 0; i--) {
			setUnit(list.get(i));
			if (!becameNovice(date1, date2))
				list.remove(i);
		}
		return this;
	}

	public ClientMed becameTrials(Date date1, Date date2) {
		List<Client> list = getGroup();
		for (int i = list.size() - 1; i >= 0; i--) {
			setUnit(list.get(i));
			if (!becameTrial(date1, date2))
				list.remove(i);
		}
		return this;
	}

	public boolean becameTrial(Date date1, Date date2) {
		contractMed.setGroup(getContracts()).retainByDates(null, date2);
		int countBeforeDate2 = contractMed.countGroupSize();
		// That means that he did not have contracts in that period at all
		if (countBeforeDate2 < 1)
			return false;
		contractMed.retainByDates(null, date1);
		int countBeforeDate1 = contractMed.countGroupSize();
		// If he already has contracts before date1 -- he already tried
		// something
		if (countBeforeDate1 > 0)
			return false;
		int notTrial = contractMed.countNotTrial();
		// If he has more contracts in that period, than he have usual, nontrial
		// contracts
		// That means he have trial contracts here
		if (countBeforeDate2 > notTrial)
			return true;
		return false;
	}

	/**
	 * 
	 * @param date1
	 * @param date2
	 * @return true if client (unit) became novice between this two dates
	 */
	public boolean becameNovice(Date date1, Date date2) {
		contractMed.setGroup(getContracts()).retainByDates(null, date2);
		int countBeforeDate2 = contractMed.countNotTrial();
		if (countBeforeDate2 != 1) // he is not novice at all
			return false;
		contractMed.retainByDates(null, date1);
		int countBeforeDate1 = contractMed.countNotTrial();
		// If he was a novice before date1 -- he is not interesting for us
		if (countBeforeDate1 > 0)
			return false;
		return true;
	}

	/**
	 * @param date1
	 * @param date2
	 * @return true if client (unit) have acquired continuer status between this
	 *         two dates
	 */
	public boolean becameContinuer(Date date1, Date date2) {
		// We need to say if client have acquired this state
		// between this dates
		// This means -- we are not interested in those who acquired this state
		// no before nor after.
		// First try could be to get all his contracts before date2 and see if
		// he has continuer state.
		// Then we could remove contract after date1 and see if state changed
		// If so -- return true
		// Else return false;

		// So -- take contracts before the second date
		contractMed.setGroup(getContracts()).retainByDates(null, date2);
		List<Contract> list = contractMed.getGroup();
		int countBeforeDate2 = 0;
		for (int i = 0; i < list.size(); i++)
			if (list.get(i).notTrial())
				countBeforeDate2++;
		// If count < 2 -- he is not continuer at all. If count >= 2 -- he is a
		// continuer
		if (countBeforeDate2 < 2)
			return false;
		// OK -- he is a continuer.
		// Lets look if he was a continuer already
		// Take only those contracts before the date1
		contractMed.retainByDates(null, date1);
		list = contractMed.getGroup();
		int countBeforeDate1 = 0;
		for (int i = 0; i < list.size(); i++)
			if (list.get(i).notTrial())
				countBeforeDate1++;
		// That means -- he already was a continuer, before this date
		if (countBeforeDate1 == countBeforeDate2)
			return false;
		if (countBeforeDate1 > countBeforeDate2)
			System.out.println("DAFUQ");
		return true;
	}

	public List<Contract> getFrozenContracts() {
		try {
			ContractMed contractMed = getContractMed();
			return contractMed.setGroup(getContracts())
					.retainByState(frozen).getGroup();
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}
	}

	public boolean hasCanceledContracts() {
		ContractMed contractMed = getContractMed();
		boolean response = contractMed.setGroup(getContracts())
				.retainByState(ContractState.canceled).countGroupSize() > 0;
		contractMed.reset();
		return response;
	}

	public List<Contract> getCanceledContracts() {
		try {
			ContractMed contractMed = getContractMed();
			return contractMed.setGroup(getContracts())
					.retainByState(ContractState.canceled).getGroup();
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
				.retainByContractType(ContractType.Trial).getGroup();
	}

	public boolean hasFinishedContracts() {
		ContractMed contractMed = getContractMed();
		boolean response = contractMed.setGroup(getContracts())
				.retainByState(ContractState.complete).countGroupSize() > 0;
		contractMed.reset();
		return response;
	}

	public List<Contract> getFinishedContracts() {
		try {
			ContractMed contractMed = getContractMed();
			return contractMed.setGroup(getContracts())
					.retainByState(ContractState.complete).getGroup();
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}
	}

	public int getBalance() {
		return unit.getBalance();
	}

	public ClientState getState() {
		// From the last session 26.12.12
		// Only trial contracts -- trial
		// Only one standard contract -- standard
		// More than one standard contract -- continuer.
		// No inactive state

		// if client is canceled - he have special mark
		if (unit.isCanceled())
			return ClientState.canceled;
		if (hasFrozenContracts())
			return ClientState.frozen;
        if(!hasContracts() || !hasActiveContracts())
            return ClientState.inactive;

		int notTrialCounter = 0;
		for (Contract c : unit.getContracts())
			if (c.notTrial())
				notTrialCounter++;
		switch (notTrialCounter) {
		case 0:
			return ClientState.trial;
		case 1:
			return ClientState.beginner;
		default:
			return ClientState.continuer;
		}
	}

    private boolean doesNotHaveActiveContracts() {
        return getActiveContracts().size() == 0;
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

	private Map<String, Object> appliedFilters;

	private Map<String, Object> getAppliedFilters() {
		if (appliedFilters == null)
			appliedFilters = new HashMap<String, Object>(5);
		return appliedFilters;
	}

	private void load() {
		cache = dao.findWithNamedQuery(Client.ALL);
		appliedFilters = new HashMap<String, Object>();
	}

	public ClientMed reset() {
		cache = null;
		appliedFilters = null;
        return this;
	}

    @Override
	public List<Client> getGroup() {
		if (cache == null)
			load();
		return cache;
	}

	public List<Client> getGroup(boolean reset) {
		List<Client> innerCache = getGroup();
		if (reset)
			reset();
		return innerCache;
	}

	public ClientMed setGroup(List<Client> group) {
		cache = group;
		return this;
	}

	public List<Client> getAllClient() {
		return dao.findWithNamedQuery(Client.ALL);
	}

	public String getFilterState() {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, Object> entry : getAppliedFilters().entrySet())
			sb.append(entry.getKey() + ": " + entry.getValue().toString()
					+ "\n");
		return sb.toString();
	}

	public ClientMed retainByState(ClientState state) {
		getAppliedFilters().put("ClientState", state);
        // Canceled is persisted property -> optimized method is call
        if(state == ClientState.canceled)
            return retainCanceled();

        List<Client> cache = getGroup();
        // save current unit
		Client unit = getUnit();

		// retainByState
		if (state == ClientState.active)
            retainActive();
        else
			for (int i = cache.size() - 1; i >= 0; i--) {
				setUnit(cache.get(i));
				if (getState() == state)
					continue;
				cache.remove(i);
			}

		// restore unit
		setUnit(unit);
		return this;
	}

    private ClientMed retainCanceled(){
        if(cacheIsNull())
            cache = dao.findWithNamedQuery(Client.CANCELED);
        else
            for(int i = cache.size()-1;i>=0;i--)
                if(!cache.get(i).isCanceled())
                    cache.remove(i);
        return this;
    }

    private ClientMed retainActive() {
        getGroup();
        for (int i = cache.size() - 1; i >= 0; i--) {
            setUnit(cache.get(i));
            ClientState cs = getState();
            if (cs == ClientState.beginner || cs == ClientState.continuer
                    || cs == ClientState.trial)
                continue;
            cache.remove(i);
        }
        return this;
    }

    public ClientMed retainByActiveTeacher(Teacher teacher) {
		getAppliedFilters().put("Active teacher", teacher);

		List<Contract> contractsCache = getContractMed().retainByTeacher(teacher)
				.retainByState(ContractState.active).getGroup();
		getContractMed().reset();
		cache = contractsToClients(contractsCache);
		return this;
	}

	public List<Client> contractsToClients(List<Contract> contracts) {
		Set<Client> list = new HashSet<Client>();
		for (Contract c : contracts)
			list.add(c.getClient());
		return new ArrayList<Client>(list);
	}

	public ClientMed retainByDateOfFirstContract(Date date1, Date date2) {
		getAppliedFilters().put("Date of first contract 1", date1);
		getAppliedFilters().put("Date of first contract 2", date2);
		List<Client> cache = getGroup();
		List<Client> cache2 = new ArrayList<Client>();
		DateFilter dateFilter = new DateFilterImpl();
		for (Dated item : dateFilter.reatinByDatesEntryWithReturn(cache, date1, date2))
			cache2.add((Client) item);
		setGroup(cache2);
		return this;
	}

	public ClientMed retainByScheduledPayments(Date date1, Date date2) {
		getAppliedFilters().put("Date of planned payments 1", date1);
		getAppliedFilters().put("Date of planned payments 2", date2);
		getGroup();

		setGroup(paymentMed.setGroupFromClients(cache).retainByState(true).retainByDatesEntry(date1, date2).toClients());
		return this;
	}
    public ClientMed retainBySoonPayments(int days) {
        return retainByScheduledPayments(null, DateService.fromNowPlusDays(14));
    }

	public ClientMed retainByName(String name) {
		getAppliedFilters().put("Client name", name);
		// Setup
		name = name.toLowerCase();

		// GO
		if (cache == null) {
			cache = dao.findWithNamedQuery(
                    Client.BY_NAME,
                    ChainMap.with("name", '%' + name + '%')
                            .yo());
			return this;
		}
		List<Client> cache = getGroup();
		String s;
		for (int i = cache.size() - 1; i >= 0; i--) {
			s = cache.get(i).getName().toLowerCase();
			if (!s.contains(name) && !s.equals(name))
				cache.remove(i);
		}
		return this;
	}

	public ClientMed retainDebtors() {
		Client c;
        List<Client> cache = getGroup();
		for (int i = cache.size() - 1; i >= 0; i--) {
			c = cache.get(i);
			if (c.getBalance() >= 0 || c.isCanceled())
				cache.remove(i);
		}
		return this;
	}

	public Integer countGroupSize() {
		return getGroup().size();
	}

	// TODO REDO
	public Integer count(ClientState state, Date date1, Date date2) {
		return retainByDateOfFirstContract(date1, date2).retainByState(state)
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
		return count(ClientState.inactive, date1, date2);
	}

	public Integer countFrozen(Date date1, Date date2) {
		return count(ClientState.frozen, date1, date2);
	}

    public ClientMed sortByName(){
        Client t;
        List<Client> cache = getGroup();
        int i = 0, j,
                len = cache.size(),
                last = len - 1;
        for (i = 0; i < len; i++)
            for (j = last; j > i; j--)
                if (cache.get(i).getName()
                        .compareTo(cache.get(j).getName()) > 0) {
                    t = cache.get(i);
                    cache.set(i, cache.get(j));
                    cache.set(j, t);
                }
        return this;
    }

    // retains only unique clients in the group
    private void unique(){
        cache = new ArrayList<Client>(new HashSet<Client>(cache));
    }
}
