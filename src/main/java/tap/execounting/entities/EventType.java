package tap.execounting.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@NamedQueries({ @NamedQuery(name = EventType.ALL, query = "Select et from EventType et" +
		" order by et.title")
})
@Table(name = "event_types")
public class EventType {

	public static final String ALL = "EventType.all";
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String title;
	
	//Event price
	private int money;
	
	//Money which school gets
	private int share;
	
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
	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	/**
	 * @return School money
	 */
	public int getShare() {
		return share;
	}

	public void setShare(int share) {
		this.share = share;
	}
	
	public int getShareTeacher(){
		return money - share;
	}
	
	//Difference between typeTitle and title - that typeTitle returns 
	//only part before delimiter. Standart delimiter is ":"
	public String getTypeTitle(){
		String delimiter = ":";
		String typeTitle = getTitle().split(delimiter)[0];
		return typeTitle;
	}
}
