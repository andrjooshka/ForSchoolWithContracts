package tap.execounting.components.editors;

import java.util.ArrayList;
import java.util.List;


import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.data.ContractTypeIdSelectModel;
import tap.execounting.entities.Client;
import tap.execounting.entities.Contract;
import tap.execounting.entities.ContractType;
import tap.execounting.entities.EventType;
import tap.execounting.entities.Teacher;

public class AddContract {

	@Inject
	private CrudServiceDAO dao;

	@Property
	@Persist
	private Contract con;

	@Property
	@Persist
	private boolean updateMode;

	@Property
	@Persist
	private String etype;

	@Persist
	private List<EventType> etypes;

	@Property
	@Persist
	private String teacher;

	@Persist
	private List<Teacher> teachers;

	@SuppressWarnings("unused")
	@Property
	private SelectModel contractTypeIdsModel;

	public void setup(Contract con) {
		if (con.getTeacherId() == 0)
			teacher = "";
		else
			teacher = dao.find(Teacher.class, con.getTeacherId()).getName();

		etype = dao.find(EventType.class, con.getTypeId()).getTitle();
		updateMode = true;
		this.con = con;
	}

	public void setup(Client client) {
		reset();
		con.setClientId(client.getId());
	}

	public void reset() {
		con = new Contract();
		updateMode = false;		
	}

	List<String> onProvideCompletionsFromEtypes(String input) {
		List<String> res = new ArrayList<String>(10);
		for (EventType e : types())
			if (e.getTitle().toLowerCase().contains(input.toLowerCase()))
				res.add(e.getTitle());
		return res;
	}

	private List<EventType> types() {
		if (etypes == null)
			etypes = dao.findWithNamedQuery(EventType.ALL);
		return etypes;
	}

	List<String> onProvideCompletionsFromTeachers(String input) {
		List<String> res = new ArrayList<String>(10);
		for (Teacher t : teachers())
			if (t.getName().toLowerCase().contains(input.toLowerCase()))
				res.add(t.getName());
		return res;
	}

	private List<Teacher> teachers() {
		if (teachers == null)
			teachers = dao.findWithNamedQuery(Teacher.ALL);
		return teachers;
	}

	void onSubmit() {
		for (EventType et : types())
			if (et.getTitle().equals(etype)) {
				con.setTypeId(et.getId());
				break;
			}
		if (con.getTypeId() == 0)
			throw new IllegalArgumentException("Тип события " + etype
					+ " не найден");

		for (Teacher t : teachers())
			if (t.getName().equals(teacher)) {
				con.setTeacherId(t.getId());
				break;
			}
		if (con.getTeacherId() == 0)
			throw new IllegalArgumentException("Педагог " + teacher
					+ " не найден");

		if (updateMode) {
			dao.update(con);
		} else {
			dao.create(con);
		}
	}
	
	void setupRender(){
		List<ContractType> allTypes = dao.findWithNamedQuery(ContractType.ALL);
		contractTypeIdsModel = new ContractTypeIdSelectModel(allTypes);
	}
}
