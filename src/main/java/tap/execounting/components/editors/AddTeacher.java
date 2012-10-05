package tap.execounting.components.editors;


import java.util.List;

import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.data.selectmodels.FacilitySelectModel;
import tap.execounting.entities.Facility;
import tap.execounting.entities.Teacher;

public class AddTeacher {

	@Persist
	@Property
	private boolean updateMode;

	@Inject
	private CRUDServiceDAO dao;

	@Persist
	@Property
	private Teacher teacher;
	
	@Property
	private SelectModel facilitySelect;

	public void setup(Teacher t) {
		teacher = t;
		updateMode = true;
	}

	public void reset() {
		teacher = new Teacher();
		updateMode = false;
	}

	void onSuccess() {
		if (updateMode) {
			dao.update(teacher);
		} else {
			dao.create(teacher);
		}
	}
	
	void onPrepareForRender(){
		List<Facility> facilities = dao.findWithNamedQuery(Facility.ACTUAL);
		facilitySelect = new FacilitySelectModel(facilities);
	}
}
