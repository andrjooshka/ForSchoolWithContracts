package tap.execounting.components.show;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;


import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.components.editors.AddEvent;
import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.entities.Client;
import tap.execounting.entities.Event;
import tap.execounting.entities.EventType;
import tap.execounting.entities.Facility;
import tap.execounting.entities.Room;

public class WideShowEvent {

	@Parameter
	@Property
	private Event lesson;

	@Inject
	private CrudServiceDAO dao;

	@InjectComponent
	private Zone bodyZone;

	@Property
	private boolean updateMode;

	@InjectComponent
	private AddEvent editor;

	Object onEdit(Event e) {
		editor.setup(e,true);
		updateMode = true;
		return bodyZone.getBody();
	}

	public String getType() {
		String title;
		try {
			title = dao.find(EventType.class, lesson.getTypeId()).getTitle();
		} catch (Exception e) {
			title = "не выбран";
		}
		return title;
	}

	public String getFacilityName() {
		return dao.find(Facility.class, lesson.getFacilityId()).getName();
	}

	public String getRoomName() {
		return dao.find(Room.class, lesson.getRoomId()).getName();
	}
	
	public String getComment(){
		if(lesson.getComment()!=null && lesson.getComment()!="")
			return lesson.getComment();
		return null;
	}

	public String getDate() {
		Format formatter = new SimpleDateFormat("dd MMMM  HH:mm");
		return formatter.format(lesson.getDate());
	}
	
	public String getClients(){
		StringBuilder sb = new StringBuilder();
		List<Client> lc = lesson.getClients();
		
		for(int i = 0;i<lc.size();i++){
			sb.append(lc.get(i).getName());
			if(i<lc.size()-1) sb.append(", ");
		}
		return sb.toString();
	}
}
