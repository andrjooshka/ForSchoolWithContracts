package tap.execounting.dal.mediators;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.dal.QueryParameters;
import tap.execounting.data.ContractState;
import tap.execounting.entities.Client;
import tap.execounting.entities.Contract;
import tap.execounting.entities.ContractType;
import tap.execounting.entities.Event;
import tap.execounting.entities.EventType;
import tap.execounting.entities.Payment;
import tap.execounting.entities.Teacher;

public class ContractMediator implements ContractMed {

	@Inject
	private CrudServiceDAO dao;

	private Contract unit;

	public Contract getUnit() {
		try {
			return unit;
		} catch (NullPointerException npe) {
			return null;
		}
	}

	public void setUnit(Contract unit) {
		this.unit = unit;
	}

	public String getTeacherName() {
		try {
			String name;
			// way1
			name = unit.getTeacher().getName();
			// way2
			// name = dao.find(Teacher.class, unit.getTeacherId()).getName();
			return name;
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public EventType getEventType() {
		try {
			EventType et;
			// way1
			et = unit.getEventType();
			// way2
			// et = dao.find(EventType.class, unit.getTypeId());
			return et;
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public ContractType getContractType() {
		try {
			ContractType ct;
			// way1
			ct = unit.getContractType();
			// way2
			// ct = dao.find(ContractType.class, unit.getContractTypeId());
			return ct;
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Date getDate() {
		try {
			return unit.getDate();
		} catch (NullPointerException e) {
			e.printStackTrace();
			return null;
		}
	}

	public int getLessonsNumber() {
		try {
			return unit.getLessonsNumber();
		} catch (NullPointerException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public int getPrice() {
		try {
			return unit.getMoney();
		} catch (NullPointerException e) {
			e.printStackTrace();
			return 0;
		}

	}

	public int getBalance() {
		try {
			return unit.getBalance();
		} catch (NullPointerException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public List<Event> getEvents() {
		try {
			List<Event> events;
			events = unit.getEvents();
			return events;
		} catch (NullPointerException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Payment> getPayments() {
		try {
			return unit.getPayments();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private List<Contract> cache;
	private Map<String, Object> appliedFilters;

	public List<Contract> getGroup() {
		if(cache==null)		
			load();
		return cache;
	}
	
	private void load(){
		cache = dao.findWithNamedQuery(Contract.ALL);
		appliedFilters = new HashMap<String, Object>();
	}

	public void setGroup(List<Contract> group) {
cache = group;
	}

	public List<Contract> getAllContracts() {
		return dao.findWithNamedQuery(Contract.ALL);
	}

	public void reset() {
		load();
	}

	public String getFilterState() {
		StringBuilder sb = new StringBuilder();
		for(Entry<String, Object> entry : appliedFilters.entrySet())
			sb.append(entry.getKey() + ": " + entry.getValue().toString() + "\n");
		return sb.toString();
	}

	public ContractMed filter(Client c) {
		appliedFilters.put("Client", c);
		Contract con;
		for(int i = cache.size()-1;i>=0;i--){
			con = cache.get(i);
			if(con.getClientId()==c.getId())
				continue;
			cache.remove(i);
			}
		return this;
	}

	public ContractMed filter(Teacher t) {
		// TODO Auto-generated method stub
		return null;
	}

	public ContractMed filter(ContractState state) {
		// TODO Auto-generated method stub
		return null;
	}

	public ContractMed filter(int remainingLessons) {
		// TODO Auto-generated method stub
		return null;
	}

	public ContractMed filter(Date date1, Date date2) {
		// TODO Auto-generated method stub
		return null;
	}

	public ContractMed filterByPlannedPaymentsDate(Date date1, Date date2) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer countGroupSize() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer count(ContractState state) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer countRemainingLessons() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer countCompleteLessonsMoney() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer countMoneyPaid() {
		// TODO Auto-generated method stub
		return null;
	}

}
