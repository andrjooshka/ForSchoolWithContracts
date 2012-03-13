package tap.execounting.pages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
//import org.hibernate.Session;

import tap.execounting.components.editors.AddEvent;
import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.entities.Event;
import tap.execounting.entities.Facility;


public class Schedule {
	
	//@Inject
	//private Session session;
	
	@Inject
	private CrudServiceDAO crudService;
	
	@SuppressWarnings("unused")
	@Property
	private Event vent;
	
	@Persist
	@Property
	private Facility facility;
	
	@InjectComponent
	private AddEvent eventEditor;
	
	public List<Event> getEvents()
	{	
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("facilityId", facility.getFacilityId());
		return crudService.findWithNamedQuery(Event.BY_FACILITY_ID, params);
	}	

	public void setup(Facility s){
		this.facility = s;
	}
	
	Object onActionFromAddEvent(){
		eventEditor.setup(facility);
		return eventEditor;
	}
	
	void onActionFromChangeState(Event se){	
		se.setState(!se.getState());
		crudService.update(se);
	}
}
