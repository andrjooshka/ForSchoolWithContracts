package tap.execounting.components.grids;

import java.util.List;


import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;

import tap.execounting.components.editors.AddFacility;
import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.entities.Facility;

public class FacilityGrid {
	@Inject
	private BeanModelSource beanModelSource;
	@Inject
	private ComponentResources componentResources;
	@Inject
	private CrudServiceDAO dao;
	@InjectComponent
	private Zone ezone;
	
	@InjectComponent
	private AddFacility editor;
	@SuppressWarnings("unused")
	@Property
	private boolean editorActive;
	@Property
	private BeanModel<Facility> model;
	@SuppressWarnings("unused")
	@Property
	private Facility unit;

	public List<Facility> getSource(){
		return dao.findWithNamedQuery(Facility.ALL);
	}
	
	Object onActionFromEdit(Facility c) {
		editorActive = true;
		editor.setup(c);
		return ezone.getBody();
	}

	Object onActionFromAdd() {
		editorActive = true;
		editor.reset();
		return ezone.getBody();
	}

	void onDelete(Facility c) {
		dao.delete(Facility.class, c.getFacilityId());
	}

	void setupRender() {
		if (model == null) {
			model = beanModelSource.createDisplayModel(Facility.class,
					componentResources.getMessages());
			model.add("Action", null);
			model.exclude("facilityId");
		}
	}
}
