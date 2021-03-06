package tap.execounting.components.editors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.BeanEditForm;
import org.apache.tapestry5.internal.util.CaptureResultCallback;
import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.dal.mediators.interfaces.ContractMed;
import tap.execounting.dal.mediators.interfaces.EventMed;
import tap.execounting.dal.mediators.interfaces.TeacherMed;
import tap.execounting.models.selectmodels.ContractTypeIdSelectModel;
import tap.execounting.entities.Client;
import tap.execounting.entities.Contract;
import tap.execounting.entities.ContractType;
import tap.execounting.entities.EventType;
import tap.execounting.entities.Teacher;
import tap.execounting.util.DateUtil;

@Import(stylesheet = "context:css/addContract.css")
public class AddContract {

	@Inject
	private CRUDServiceDAO dao;
	@Inject
	private ContractMed contractMed;
	@Inject
	private TeacherMed tm;
	@Inject
	private EventMed em;

	@InjectComponent
	private BeanEditForm editor;

	@Inject
	private ComponentResources resources;

	@Property
	@Persist
	private Contract con;

	@Property
	@Persist
	private boolean updateMode;

	// Screen properties
	@Property
	private Date eventsStartDate;
	@Property
	@Persist
	private String etype;

	private List<EventType> etypes;

	@Property
	@Persist
	private String teacher;

	private List<Teacher> teachers;

	@Property
	private SelectModel contractTypeIdsModel;

	private ContractMed getContractMed() {
		return contractMed;
	}

	public void setup(Contract con) {
		if (con.getTeacherId() == 0)
			teacher = "";
		else
			teacher = tm.setId(con.getTeacherId()).getName();

		etype = em.loadEventType(con.getTypeId()).getTitle();
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
			etypes = dao.findWithNamedQuery(EventType.ACTUAL);
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
			teachers = tm.getWorkingTeachers();
		return teachers;
	}

	private Teacher teacher() {
		for (Teacher t : tm.getAllTeachers())
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
		return DateUtil.toString("dd MMM YYYY", con.getDate());
	}

	void onValidateFromEditor() throws ValidationException {
		// Teacher check
		try {
			teacherId();
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Учителя c именем: " + teacher
					+ ", не найдено.");
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
			editor.recordError(errorString);
			throw new IllegalArgumentException(errorString);
			// throw new ValidationException(errorString);
		}
	}

	Object onSuccess() {
		con.setTypeId(typeId());
		con.setTeacherId(teacherId());

		if (updateMode) {
			dao.update(con);
		} else {
			dao.create(con);
		}

		getContractMed().setUnit(con).doPlanEvents(eventsStartDate);

		// Today we do tricks. This code calls onExperiment in the parent
		// component. onExperiment from showContract provides us with the zone
		// body. OnExperiment from the ClientPage -- provides current page.
		CaptureResultCallback<Object> cb = new CaptureResultCallback<Object>();
		resources.triggerEvent("Experiment", new Object[] { con }, cb);
		return cb.getResult();
	}

	Object onTheCancel() {
		// Today we do tricks. This code calls onCancel in the parent
		// component.
		CaptureResultCallback<Object> cb = new CaptureResultCallback<Object>();
		resources.triggerEvent("Cancel", new Object[] { con }, cb);
		return cb.getResult();
	}

	void setupRender() {
		List<ContractType> allTypes = dao.findWithNamedQuery(ContractType.ALL);
		contractTypeIdsModel = new ContractTypeIdSelectModel(allTypes);
	}
}
