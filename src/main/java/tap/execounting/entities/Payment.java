package tap.execounting.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.tapestry5.beaneditor.Validate;

import tap.execounting.entities.interfaces.Dated;

@Entity
@Table(name = "payments")
@NamedQueries({
		@NamedQuery(name = Payment.ALL, query = "Select p from Payment p"),
		@NamedQuery(name = Payment.BY_CONTRACT_ID, query = "Select p from Payment p where p.contractId = :contractId order by p.date desc"),
		@NamedQuery(name = Payment.BY_DATES, query = "Select p from Payment p where p.date between "
				+ ":earlierDate and :laterDate") })
public class Payment implements Dated<Payment>{

	public static final String ALL = "Payment.all";

	public static final String BY_CONTRACT_ID = "Payment.byContractId";

	public static final String BY_DATES = "Payment.byDates";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payment_id")
	private int id;

	private int amount;

	private boolean scheduled;
	
	private String comment;

	@Validate(value = "required")
	private Date date;

	@Column(name = "contract_id")
	private int contractId;
	
	@OneToOne(optional = false)
	@JoinColumn(name = "contract_id", insertable = false, updatable = false)
	private Contract contract;

	public Contract getContract() {
		return contract;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getContractId() {
		return contractId;
	}

	public void setContractId(int contractId) {
		this.contractId = contractId;
	}

	public boolean isScheduled() {
		return scheduled;
	}

	public void setScheduled(boolean scheduled) {
		this.scheduled = scheduled;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
