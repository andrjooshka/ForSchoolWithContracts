package tap.execounting.components.editors;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.BeanEditForm;
import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.dal.mediators.ContractMediator;
import tap.execounting.dal.mediators.interfaces.ContractMed;
import tap.execounting.data.selectmodels.ContractTypeIdSelectModel;
import tap.execounting.entities.Client;
import tap.execounting.entities.Contract;
import tap.execounting.entities.ContractType;
import tap.execounting.entities.EventType;
import tap.execounting.entities.Teacher;
import tap.execounting.services.SuperCalendar;

@Import(stylesheet = "context:/layout/addContract.css")
public class AddContract {

	@Inject
	private CrudServiceDAO dao;
	@Inject
	private SuperCalendar calendar;
	@Inject
	private ContractMed contractMed;

	@Component
	private BeanEditForm editor;

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

	@Property
	private SelectModel contractTypeIdsModel;

	private ContractMed getContractMed() {
		return new ContractMediator().setDao(dao);
		// try {
		// contractMed.setUnit(con);
		// return contractMed;
		// } catch (LinkageError e) {
		// e.printStackTrace();
		// return new ContractMediator().setDao(dao);
		// }
	}

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

	private Teacher teacher() {
		for (Teacher t : teachers())
			if (t.getName().equals(teacher))
				return t;
		return null;
	}

	private int teacherId() {
		return teacher().getId();
	}

	private EventType type() {
		for (EventType et : types())
			if (et.getTitle().equals(etype))
				return et;
		return null;
	}

	private int typeId() {
		return type().getId();
	}

	public String getConDate() {
		return calendar.setTime(con.getDate()).stringByTuple("day", "month",
				"year");
	}

	void onValidateFromEditor() throws ValidationException {
		// Teacher check
		try {
			teacherId();
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Учителя c именем: " + teacher + ", не найдено.");
		}

		// Contract/Teacher Schedule compatibility check
		for (int i = 1; i < 8; i++)
			if (con.getSchedule().get(i))
				if (teacher().getScheduleDay(i) == null)
					throw new IllegalArgumentException(
							String.format(
									"%s не ведет занятий в %s день недели.",
									teacher, i));

		// Type check
		try {
			typeId();
		} catch (Exception e) {
			e.printStackTrace();
			String errorString = "Типа занятий: " + etype + ", не найдено.";
			throw new IllegalArgumentException(errorString);
			//editor.recordError(errorString);
		}
	}

	void onSuccess() {
		con.setTypeId(typeId());
		con.setTeacherId(teacherId());

		if (updateMode) {
			dao.update(con);
		} else {
			dao.create(con);
		}

		getContractMed().setUnit(con).planEvents();
	}

	void setupRender() {
		List<ContractType> allTypes = dao.findWithNamedQuery(ContractType.ALL);
		contractTypeIdsModel = new ContractTypeIdSelectModel(allTypes);
	}
}
