package tap.execounting.dal.mediators;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.dal.QueryParameters;
import tap.execounting.dal.mediators.interfaces.DateFilter;
import tap.execounting.dal.mediators.interfaces.PaymentMed;
import tap.execounting.entities.Contract;
import tap.execounting.entities.Payment;

public class PaymentMediator implements PaymentMed {

	@Inject
	private CrudServiceDAO dao;

	@Inject
	private DateFilter dateFilter;

	private Payment unit;

	public Payment getUnit() {
		return this.unit;
	}

	public void setUnit(Payment unit) {
		this.unit = unit;
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

	public PaymentMed setGroup(List<Payment> payments) {
		cache = payments;
		return this;
	}

	public List<Payment> getAllPayments() {
		return dao.findWithNamedQuery(Payment.ALL);
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

	public PaymentMed filter(Contract unit) {
		if (cache == null || appliedFilters == null
				|| appliedFilters.size() == 0) {
			cache = dao.findWithNamedQuery(Payment.BY_CONTRACT_ID,
					QueryParameters.with("contractId", unit.getId())
							.parameters());
		} else {
			for (int i = cache.size() - 1; i >= 0; i--)
				if (cache.get(i).getContractId() != unit.getId())
					cache.remove(i);
		}
		return this;
	}

	public PaymentMed filter(Date date1, Date date2) {
		if (cache == null || appliedFilters == null
				|| appliedFilters.size() == 0)
			cache = dao.findWithNamedQuery(Payment.BY_DATES, QueryParameters
					.with("earlierDate", date1).and("laterDate", date2)
					.parameters());
		else
			dateFilter.filter(cache, date1, date2);

		return this;
	}

	public PaymentMed filter(boolean state) {
		if (cache == null || appliedFilters == null
				|| appliedFilters.size() == 0)
			if (state)
				cache = dao.findWithNamedQuery(Payment.SCHEDULED);
			else
				cache = dao.findWithNamedQuery(Payment.NOT_SCHEDULED);
		else {
			for (int i = cache.size() - 1; i >= 0; i--)
				if (cache.get(i).isScheduled() != state)
					cache.remove(i);
		}
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

	public Integer countReturn(Date date1, Date date2) {
		PaymentMed pm = new PaymentMediator();
		return pm.filter(date1,date2).filter(false).countAmount();
	}

}
