package tap.execounting.components.show;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.data.EventRowElement;
import tap.execounting.data.EventState;
import tap.execounting.entities.Event;

public class EventCell {
	@Property
	@Parameter
	private boolean displayDate;
	@Property
	@Parameter
	private EventRowElement element;
	@InjectComponent
	private Zone cellZone;
	@Inject
	private CrudServiceDAO dao;
	@Persist
	private EventRowElement back;

	@Property
	private boolean editing;

	public String getAddition() {
		if (element.getEvent() != null)
			switch (element.getEvent().getState()) {
			case planned:
				return "planned";
			case complete:
				return "complete";
			case failed:
				return "failed";
			case failedByClient:
				return "failed client";
			case failedByTeacher:
				return "failed teacher";
			default:
				return "";
			}
		return "";
	}

	public int getDate() {
		Calendar c = new GregorianCalendar();
		c.setTime(element.getDate());
		return c.get(Calendar.DAY_OF_MONTH);
	}

	Object onActionFromEdit(int id) {
		Event e = dao.find(Event.class, id);
		element = new EventRowElement(e.getDate(), e);
		back = new EventRowElement(element.getDate(), element.getEvent());
		if(e.getDate().after(new Date())) return cellZone;
		editing = true;
		return cellZone;
	}

	public Object onValueChanged(EventState state) {
		back.getEvent().setState(state);
		dao.update(back.getEvent());
		element = back;
		
		editing = false;
		return cellZone;
	}

	Object onSubmit() {
		dao.update(element.getEvent());
		editing = false;
		return cellZone;
	}
}
