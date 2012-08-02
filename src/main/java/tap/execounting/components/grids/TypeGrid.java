package tap.execounting.components.grids;

import java.util.List;


import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;

import tap.execounting.components.editors.AddEventType;
import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.entities.EventType;

public class TypeGrid {
	@Inject
	private BeanModelSource beanModelSource;
	@Inject
	private ComponentResources componentResources;
	@Inject
	private CrudServiceDAO dao;
	@InjectComponent
	private Zone ezone;
	
	@InjectComponent
	private AddEventType editor;
	@Property
	private boolean editorActive;
	@Property
	private BeanModel<EventType> model;
	@Property
	private EventType unit;

	public List<EventType> getSource(){
		return dao.findWithNamedQuery(EventType.ALL);
	}
	
	Object onActionFromEdit(EventType c) {
		editorActive = true;
		editor.setup(c);
		return ezone.getBody();
	}

	Object onActionFromAdd() {
		editorActive = true;
		editor.reset();
		return ezone.getBody();
	}

	void onDelete(EventType c) {
		dao.delete(EventType.class, c.getId());
	}

	void setupRender() {
		if (model == null) {
			model = beanModelSource.createDisplayModel(EventType.class,
					componentResources.getMessages());
			model.exclude("id", "typeTitle");
			model.add("Action", null);
		}
	}
}
