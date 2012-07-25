package tap.execounting.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import tap.execounting.services.DateService;

@Entity
@Table(name = "clients")
@NamedQueries({
		@NamedQuery(name = Client.ALL, query = "select c from Client c"),
		@NamedQuery(name = Client.ALL_NAMES, query = "select c.name from Client c"),
		@NamedQuery(name = Client.BY_NAME, query = "select c from Client c where c.name = :name")})
public class Client {

	public static final String ALL = "Client.all";
	public static final String ALL_NAMES = "Client.allNames";
	public static final String BY_NAME = "Client.byName";

	public static final String inactive = "нет активных договоров";
	public static final String frozen = "договор(а) заморожены";
	public static final String active = "занимается";

	public static final String studStateNew = "новичок";
	public static final String studStateExp = "продливший";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "client_id")
	private int id;
	@NotNull
	@Column(unique = true)
	private String name;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "client_id")
	private List<Contract> contracts;

	public Client() {
		setContracts(new ArrayList<Contract>());
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Contract> getContracts() {
		if (contracts == null)
			contracts = new ArrayList<Contract>();
		return contracts;
	}

	public List<Contract> getContracts(boolean sortAsc) {
		List<Contract> list = getContracts();
		Collections.sort(list);
		if (!sortAsc)
			Collections.reverse(list);
		return list;
	}

	public void setContracts(List<Contract> contracts) {
		this.contracts = contracts;
	}

	public List<Contract> getActiveContracts() {
		List<Contract> contracts = new ArrayList<Contract>();
		for (Contract c : getContracts())
			if (c.isActive())
				contracts.add(c);
		return contracts;
	}

	public List<Contract> getUnfinishedContracts() {
		List<Contract> contracts = new ArrayList<Contract>();
		for (Contract c : getContracts())
			if (c.isActive() || c.isFreeze())
				contracts.add(c);
		return contracts;
	}

	public List<Teacher> getCurrentTeachers() {
		Set<Teacher> res = new HashSet<Teacher>();
		for (Contract c : getActiveContracts())
			res.add(c.getTeacher());
		return new ArrayList<Teacher>(res);
	}

	public int getReturn() {
		int total = 0;

		for (Contract c : getContracts()) {
			total += c.getMoney();
		}

		return total;
	}

	public int getBalance() {
		int total = 0;
		Contract contract;
		for (int i = 0; i < getContracts().size(); i++) {
			contract = getContracts().get(i);

			total += contract.getBalance();
		}

		return total;
	}

	public List<Payment> getPlannedPayments() {
		List<Payment> payments = new ArrayList<Payment>();
		for (Contract c : getContracts())
			for (Payment p : c.getPlannedPayments())
				if(p.getDate().before(DateService.fromNowPlusDays(15)))
					payments.add(p);
		return payments;
	}

	public Payment getFirstPlannedPayment() {
		List<Payment> payments = getPlannedPayments();
		if (payments.size() == 0)
			return null;
		Payment p = payments.get(0);
		for (Payment t : payments)
			if (t.getDate().before(p.getDate()))
				p = t;
		return p;
	}

	public Date getFirstPlannedPaymentDate() {
		if (getFirstPlannedPayment() != null)
			return getFirstPlannedPayment().getDate();
		return null;
	}

	public String getState() {

		// Client inactive check
		List<Contract> list = getUnfinishedContracts();
		if (list.size() == 0)
			return inactive;

		for (Contract c : list)
			if (c.isActive())
				return active;

		return frozen;
	}

	public Date getFirstContractDate() {
		List<Contract> contracts = getContracts(true);
		if (contracts.size() == 0)
			return null;
		Date d = contracts.get(0).getDate();
		return d;
	}

	public String getStudentInfo() {
		Contract t;
		boolean firstRealContract = false;
		for (int i = 0; i < contracts.size(); i++) {
			t = contracts.get(i);
			if (!firstRealContract) {
				if (t.getLessonsNumber() > 2)
					firstRealContract = true;
			} else if (t.getLessonsNumber() > 2)
				return Client.studStateExp;
		}
		return Client.studStateNew;
	}
}