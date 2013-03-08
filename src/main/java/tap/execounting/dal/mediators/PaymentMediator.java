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

import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.dal.QueryParameters;
import tap.execounting.dal.mediators.interfaces.DateFilter;
import tap.execounting.dal.mediators.interfaces.PaymentMed;
import tap.execounting.entities.Client;
import tap.execounting.entities.Contract;
import tap.execounting.entities.Payment;

public class PaymentMediator implements PaymentMed {

	@Inject
	private CRUDServiceDAO dao;

	@Inject
	private DateFilter dateFilter;

	private Payment unit;

	private CRUDServiceDAO getDao() {
		return dao;
	}

	public Payment getUnit() {
		return this.unit;
	}

	public PaymentMed setUnit(Payment unit) {
		this.unit = unit;
		return this;
	}

	public Payment getUnitById(int paymentId) {
		return dao.find(Payment.class, paymentId);
	}

	public boolean getPlanned() {
		return unit.isScheduled();
	}

	public String getComment() {
		try {
			return unit.getComment();
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}
	}

	public int getAmount() {
		return unit.getAmount();
	}

	public Date getDate() {
		return unit.getDate();
	}

	private List<Payment> cache;
	private Map<String, Object> appliedFilters;

	public List<Payment> getGroup() {
		if (cache == null)
			cache = getAllPayments();
		return cache;
	}

	public List<Payment> getGroup(boolean reset) {
		List<Payment> innerCache = getGroup();
		if (reset)
			reset();
		return innerCache;
	}

	public PaymentMed setGroup(List<Payment> payments) {
		cache = payments;
		return this;
	}

    public PaymentMed setGroupFromContracts(List<Contract> contracts) {
        cache = new ArrayList<Payment>();
        for (Contract c : contracts)
            cache.addAll(c.getPayments());
        return this;
    }

    public PaymentMed setGroupFromClients(List<Client> clients){
        cache = new ArrayList<>();
        for(Client c : clients)
            for(Contract con : c.getContracts())
                cache.addAll(con.getPayments());
        return this;
    }

	public List<Payment> getAllPayments() {
		return getDao().findWithNamedQuery(Payment.ALL);
	}

	public void reset() {
		this.appliedFilters = null;
		this.cache = null;
	}

	private Map<String, Object> getAppliedFilters() {
		if (appliedFilters == null)
			appliedFilters = new HashMap<String, Object>(5);
		return appliedFilters;
	}

	public String getFilterState() {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, Object> entry : getAppliedFilters().entrySet())
			sb.append(entry.getKey() + ": " + entry.getValue().toString()
					+ "\n");
		return sb.toString();
	}

	public PaymentMed retainByContract(Contract unit) {
		if (cache == null || appliedFilters == null
				|| appliedFilters.size() == 0) {
			cache = getDao().findWithNamedQuery(
					Payment.BY_CONTRACT_ID,
					QueryParameters.with("contractId", unit.getId())
							.parameters());
		} else {
			for (int i = cache.size() - 1; i >= 0; i--)
				if (cache.get(i).getContractId() != unit.getId())
					cache.remove(i);
		}
		getAppliedFilters().put("Contract", unit);
		return this;
	}

	public PaymentMed retainByDatesEntry(Date date1, Date date2) {
		getGroup();
		// WARN -- commented code does not works properly, need testing
		//
		// if (cache == null && (appliedFilters == null
		// || appliedFilters.size() == 0)) {
		// // Query should not get null values
		// date1 = date1 == null ? new Date(0) : date1;
		// date2 = date2 == null ? new Date(Long.MAX_VALUE - Integer.MAX_VALUE)
		// : date2;
		// cache = getDao().findWithNamedQuery(
		// Payment.BY_DATES,
		// QueryParameters.with("earlierDate", date1)
		// .and("laterDate", date2).parameters());
		// } else
		dateFilter.retainByDatesEntry(cache, date1, date2);
		getAppliedFilters().put("Date1", date1);
		getAppliedFilters().put("Date2", date2);

		return this;
	}

	public PaymentMed retainByState(boolean state) {
		if (cache == null || appliedFilters == null
				|| appliedFilters.size() == 0)
			if (state)
				cache = getDao().findWithNamedQuery(Payment.SCHEDULED);
			else
				cache = getDao().findWithNamedQuery(Payment.NOT_SCHEDULED);
		else {
			for (int i = cache.size() - 1; i >= 0; i--)
				if (cache.get(i).isScheduled() != state)
					cache.remove(i);
		}
		getAppliedFilters().put("StatePlanned", state);
		return this;
	}

	public Integer countGroupSize() {
		try {
			return cache.size();
		} catch (NullPointerException npe) {
			return null;
		}
	}

	public Integer countAmount() {
		int summ = 0;
		List<Payment> cache = getGroup();
		for (Payment p : cache)
			summ += p.getAmount();
		return summ;
	}

	public int countRealPaymentsAmount() {
		int summ = 0;
		for (Payment p : getGroup())
			if (!p.isScheduled())
				summ += p.getAmount();
		return summ;
	}

	public int countScheduledPaymentsAmount() {
		int summ = 0;
		for (Payment p : getGroup())
			if (p.isScheduled())
				summ += p.getAmount();
		return summ;
	}

	public List<Contract> getContracts() {
		Set<Contract> contractSet = new HashSet<Contract>();
		for (Payment p : getGroup())
			contractSet.add(p.getContract());
		return new ArrayList<Contract>(contractSet);
	}

    public List<Client> toClients() {
        Set<Client> set = new HashSet<>();
        for(Payment p : cache)
            set.add(p.getContract().getClient());
        return new ArrayList<>(set);
    }

	public Integer countReturn(Date date1, Date date2) {
		PaymentMed pm = new PaymentMediator();
		return pm.retainByDatesEntry(date1, date2).retainByState(false).countAmount();
	}

}
