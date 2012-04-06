package tap.execounting.components.grids;

import java.util.List;


import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;

import tap.execounting.components.editors.AddTeacher;
import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.entities.Facility;
import tap.execounting.entities.Teacher;

public class TeacherGrid {
	@Inject
	private BeanModelSource beanModelSource;
	@Inject
	private ComponentResources componentResources;
	@Inject
	private CrudServiceDAO dao;
	@InjectComponent
	private Zone tzone;
	
	@InjectComponent
	private AddTeacher editor;
	@SuppressWarnings("unused")
	@Property
	private boolean editorActive;
	@Property
	private BeanModel<Teacher> model;
	@SuppressWarnings("unused")
	@Property
	private Teacher unit;

	public Facility facility(Integer id) {
		if(id == null) return null;
		return dao.find(Facility.class, id);
	}

	public String state(Teacher t) {
		return t.isActive() ? "да" : "нет";
	}
	
	public List<Teacher> getSource(){
		return dao.findWithNamedQuery(Teacher.ALL);
	}
	
	Object onActionFromEdit(Teacher c) {
		editorActive = true;
		editor.setup(c);
		return tzone.getBody();
	}

	Object onActionFromAdd() {
		editorActive = true;
		editor.reset();
		return tzone.getBody();
	}

	void onDelete(Teacher c) {
		dao.delete(Teacher.class, c.getId());
	}

	void setupRender() {
		if (model == null) {
			model = beanModelSource.createDisplayModel(Teacher.class,
					componentResources.getMessages());
			model.add("Action", null);
			
			model.exclude("id");
			for (String pn : model.getPropertyNames())
				model.get(pn).sortable(false);
			model.get("name").sortable(true);
		}
	}
}
