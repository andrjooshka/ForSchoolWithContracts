package tap.execounting.components.show;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

import tap.execounting.data.EventRowElement;

public class EventCell {
	@Property
	@Parameter
	private boolean displayDate;
	@Property
	@Parameter
	private EventRowElement element;

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
	
	public int getDate(){
		Calendar c = new GregorianCalendar();
		c.setTime(element.getDate());
		return c.get(Calendar.DAY_OF_MONTH);
	}
}
