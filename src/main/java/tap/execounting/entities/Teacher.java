package tap.execounting.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@NamedQueries({
	@NamedQuery(name = Teacher.ALL, query = "Select t from Teacher t order by t.name"),
	@NamedQuery(name = Teacher.ALL_NAMES, query = "Select t.name from Teacher t order by t.name"),
	@NamedQuery(name = Teacher.BY_ACTIVE_STATUS, query = "Select t from Teacher t where t.active = :status"),
	@NamedQuery(name = Teacher.BY_NAME, query = "Select t from Teacher t where t.name = :name")
})
@Table(name = "teachers")
public class Teacher {
	
	public static final String ALL = "Teacher.all";
	public static final String ALL_NAMES = "Teacher.allNames";
	public static final String BY_ACTIVE_STATUS = "Teacher.byActiveStatus";
	public static final String BY_NAME = "Teacher.byName";
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "teacher_id")
	private int id;
	@NotNull
	@Size(min=3, max=50)
	@Column(nullable = false, unique = true)
	private String name;
	@NotNull
	private boolean active;
	private Integer monday;
	private Integer tuesday;
	private Integer wednesday;
	private Integer thursday;
	private Integer friday;
	private Integer saturday;
	private Integer sunday;
	
	
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
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public Integer getMonday() {
		return monday;
	}
	public void setMonday(Integer monday) {
		this.monday = monday;
	}
	public Integer getTuesday() {
		return tuesday;
	}
	public void setTuesday(Integer tuesday) {
		this.tuesday = tuesday;
	}
	public Integer getWednesday() {
		return wednesday;
	}
	public void setWednesday(Integer wednesday) {
		this.wednesday = wednesday;
	}
	public Integer getThursday() {
		return thursday;
	}
	public void setThursday(Integer thursday) {
		this.thursday = thursday;
	}
	public Integer getFriday() {
		return friday;
	}
	public void setFriday(Integer friday) {
		this.friday = friday;
	}
	public Integer getSaturday() {
		return saturday;
	}
	public void setSaturday(Integer saturday) {
		this.saturday = saturday;
	}
	public Integer getSunday() {
		return sunday;
	}
	public void setSunday(Integer sunday) {
		this.sunday = sunday;
	}
	
}
