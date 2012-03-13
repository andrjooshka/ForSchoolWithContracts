package tap.execounting.components.grids;

import java.util.List;


import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;

import tap.execounting.components.editors.AddEvent;
import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.entities.Client;
import tap.execounting.entities.Event;
import tap.execounting.entities.EventType;
import tap.execounting.entities.Facility;
import tap.execounting.entities.Room;
import tap.execounting.entities.Teacher;

public class LessonGrid {
	@Inject
	private BeanModelSource beanModelSource;
	@Inject
	private ComponentResources componentResources;
	@Inject
	private CrudServiceDAO dao;
	@InjectComponent
	private Zone ezone;
	
	@InjectComponent
	private AddEvent editor;
	@SuppressWarnings("unused")
	@Property
	private boolean editorActive;
	@Property
	private BeanModel<Event> model;
	@Property
	private Event unit;

	public List<Event> getSource(){
		return dao.findWithNamedQuery(Event.ALL);
	}
	
	Object onActionFromEdit(Event c) {
		editorActive = true;
		editor.setup(c);
		return ezone.getBody();
	}

	Object onActionFromAdd() {
		editorActive = true;
		editor.reset();
		return ezone.getBody();
	}

	void onSubmitFromDeleteForm(Event c) {
		dao.delete(Event.class, c.getId());
	}

	void setupRender() {
		if (model == null) {
			model = beanModelSource.createDisplayModel(Event.class,
					componentResources.getMessages());
			model.add("Delete", null);
			model.add("Clients", null);
			model.add("Edit", null);
			model.exclude("id");
		}
	}
	
	public String getHostName(){
		System.out.println(unit.getId());
		return dao.find(Teacher.class, unit.getHostId()).getName();
	}
	
	public String getFacilityTitle(){
		return dao.find(Facility.class, unit.getFacilityId()).getName();
	}
	
	public String getRoomTitle(){
		return dao.find(Room.class, unit.getRoomId()).getName();
	}
	
	public String getState(){
		return unit.getState() ? "Проведено" : "Не проведено";
	}
	
	public String getTypeTitle(){
		return dao.find(EventType.class, unit.getTypeId()).getTitle();
	}
	
	public int getMoney() {
		EventType et = dao.find(EventType.class, unit.getTypeId());
		if (unit.getClients().size() > 0)
			return et.getMoney() * unit.getClients().size();
		return et.getMoney();
	}
	
	public String getClients(){
		StringBuilder sb = new StringBuilder();
		List<Client> lc = unit.getClients();
		
		for(int i = 0;i<lc.size();i++){
			sb.append(lc.get(i).getName());
			if(i<lc.size()-1) sb.append(", ");
		}
		return sb.toString();
	}
}
