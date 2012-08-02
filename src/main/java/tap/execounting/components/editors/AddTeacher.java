package tap.execounting.components.editors;


import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.data.FacilitySelectModel;
import tap.execounting.entities.Teacher;

public class AddTeacher {

	@Persist
	@Property
	private boolean updateMode;

	@Inject
	private CrudServiceDAO dao;

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

	void onSubmit() {
		if (updateMode) {
			dao.update(teacher);
		} else {
			dao.create(teacher);
		}
	}
	
	void onPrepareForRender(){
		facilitySelect = new FacilitySelectModel(dao);
	}
}
