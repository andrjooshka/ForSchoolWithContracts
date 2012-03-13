package tap.execounting.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "contract_types")
@NamedQueries({ @NamedQuery(name = ContractType.ALL, query = "Select ct from ContractType ct") })
public class ContractType {

	public static final String ALL = "ContractType.all";

	@Id
	@Column(name = "contract_type_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String title;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
