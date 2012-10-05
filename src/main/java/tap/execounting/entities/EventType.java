package tap.execounting.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.tapestry5.beaneditor.NonVisual;

import tap.execounting.entities.interfaces.Deletable;

@Entity
@NamedQueries({ @NamedQuery(name = EventType.ALL, query = "Select et from EventType et" +
		" order by et.title"),
		@NamedQuery(name = EventType.ACTUAL, query = "from EventType where deleted = false")
})
@Table(name = "event_types")
public class EventType implements Deletable {

	public static final String ALL = "EventType.all";

	public static final String ACTUAL = "EventType.actual";
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String title;
	
	//Event price
	private int price;
	
	//Money which school gets
	private int schoolMoney;
	
	@NonVisual
	private boolean deleted;
	
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

	/**
	 * @return Event price
	 */
	public int getPrice() {
		return price;
	}

	public void setPrice(int money) {
		this.price = money;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	/**
	 * @return School money
	 */
	public int getSchoolMoney() {
		return schoolMoney;
	}

	public void setSchoolMoney(int share) {
		this.schoolMoney = share;
	}
	
	public int getShareTeacher(){
		return price - schoolMoney;
	}
	
	//Difference between typeTitle and title - that typeTitle returns 
	//only part before delimiter. Standard delimiter is ":"
	public String getTypeTitle(){
		String delimiter = ":";
		String typeTitle = getTitle().split(delimiter)[0];
		return typeTitle;
	}
}
