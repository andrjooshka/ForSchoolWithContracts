package tap.execounting.pages;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import tap.execounting.services.RusCalendar;
import tap.execounting.services.SuperCalendar;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.components.editors.AddEvent;
import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.entities.Event;
import tap.execounting.entities.Facility;
import tap.execounting.entities.Teacher;

public class TeacherSchedule {

	@Property
	@Persist
	private Teacher teacher;

	@SuppressWarnings("unused")
	@Property
	private boolean adding;

	@SuppressWarnings("unused")
	@Property
	private String eventTime;

	@Component
	private Zone editZone;

	@Component
	private AddEvent eventEditor;

	@Persist
	private SuperCalendar firstDate;

	@Persist
	private SuperCalendar secondDate;

	@Persist
	@Property
	private Date date;

	@Persist
	private Event[][] eventsArrayCached;

	@SuppressWarnings("unused")
	@Property
	private String singledate;

	@Inject
	private CrudServiceDAO dao;

	@Persist
	@Property
	private int row;

	void onActionFromWeekBackward() {
		firstDate.addDays(-7);
		secondDate.addDays(-7);
	}

	void onActionFromWeekForward() {
		firstDate.addDays(7);
		secondDate.addDays(7);
	}

	void onSubmitFromDateController() {
		firstDate.minHoursMinutes();
		secondDate.setTime(firstDate.getTime());
		secondDate.addDays(7);
		secondDate.maxHoursMinutes();
	}

	public List<String> getDateData() {
		List<String> list = new ArrayList<String>();
		int p = datesRange();

		RusCalendar rc = new RusCalendar();
		rc.setTime(getFirstDate());

		for (int i = 0; i < p; i++) {
			String s = rc.getDayOfWeekName() + " | " + rc.getDayOfMonth();
			list.add(s);
			rc.incrementDay();
		}
		return list;
	}

	Object onActionFromAddNew() {
		try {
			adding = true;
			eventEditor.setup(teacher);
			return editZone.getBody();
		} catch (Exception exc) {
			return null;
		}
	}

	public void setup(Teacher t) {
		System.out.println("\n\nTeacherSchedule.Setup");
		this.teacher = t;

		firstDate = new RusCalendar();
		firstDate.minHoursMinutes();
		secondDate = new RusCalendar();
		secondDate.addDays(7);
		secondDate.maxHoursMinutes();
	}

	public String getFacilityNameById(Long id) {
		return dao.find(Facility.class, id).getName();
	}

	public List<Date> getDates() {
		RusCalendar t = new RusCalendar();
		t.setTime(firstDate.getTime());
		List<Date> dates = new ArrayList<Date>(7);

		for (int i = 0; i < datesRange(); i++, t.incrementDay())
			dates.add(t.getTime());

		return dates;
	}

	public Event getEvent() {
		int column = firstDate.rangeInDays(date);
		Event result = eventsArrayCached[column][row - 1];
		System.out.printf("Row %d \tColumn %d \tDate: %s \tEvent date: %s\n",
				row - 1, column, date, result != null ? result.getDate()
						: result);

		return result;
	}

	public void setEvent(Event e) {
		dao.update(e);
	}

	public List<Integer> getRows() {
		if (eventsArrayCached == null)
			return null;

		int rows = 0;
		for (Event[] l : eventsArrayCached)
			if (l.length > rows)
				rows = l.length;

		List<Integer> result = new ArrayList<Integer>(rows);
		for (int i = 0; i < rows; i++)
			result.add(i + 1);
		return result;
	}

	public List<Event> datedEvents() {
		HashMap<String, Object> params = new HashMap<String, Object>(3);
		System.out.printf(
				"\n\n Loading events from db; Date1: %s, date2: %s\n\n",
				firstDate.getTime(), secondDate.getTime());
		params.put("teacherId", teacher.getId());
		params.put("earlierDate", firstDate.getTime());
		params.put("laterDate", secondDate.getTime());
		return dao.findWithNamedQuery(Event.BY_TEACHER_ID_AND_DATE, params);
	}

	private int datesRange() {
		if (secondDate.before(firstDate))
			throw new IllegalArgumentException(
					"second date should be after first, not vice versa");
		return firstDate.rangeInDays(secondDate);
	}

	public Date getFirstDate() {
		return firstDate.getTime();
	}

	public void setFirstDate(Date date) {
		firstDate.setTime(date);
	}

	public Date getSecondDate() {
		return secondDate.getTime();
	}

	private Event[][] getEventsArray() throws Exception {
		// preparations for processing
		System.out.println("\n\nprocessnig events");
		int length = datesRange();
		List<Event> eventsToProcess = datedEvents();

		if (eventsToProcess == null || eventsToProcess.size() == 0)
			return null;
		@SuppressWarnings("unchecked")
		List<Event>[] temp = new ArrayList[length];
		SuperCalendar rc = firstDate.clone();
		int rows = 0;

		// processing
		for (int i = 0; i < length; i++) {
			temp[i] = new ArrayList<Event>();

			for (int j = eventsToProcess.size() - 1; j >= 0; j--) {

				Event processingEvent = eventsToProcess.get(j);
				SuperCalendar etime = new RusCalendar();
				etime.setTime(processingEvent.getDate());

				System.out.println("Event time: " + etime + "\t Current time: "
						+ rc);
				if (etime.rangeInDays(rc) == 0) {
					temp[i].add(processingEvent);
					eventsToProcess.remove(j);
				}
			}
			if (temp[i].size() > rows)
				rows = temp[i].size();
			sort(temp[i]);

			System.out.println("increment tdate");
			rc.incrementDay();
		}
		if (eventsToProcess.size() != 0)
			throw new Exception(
					"Bad algorythm, unprocessed events remains after proccessing");

		// postprocessing
		Event[][] result = new Event[length][rows];
		for (int i = 0; i < result.length; i++) {
			ListIterator<Event> iter = temp[i].listIterator();
			int j = 0;
			while (iter.hasNext()) {
				result[i][j] = iter.next();
				j++;
			}
		}
		System.out.println("events processed");
		System.out.println("max rows: " + rows + "\n");
		return result;
	}

	private void sort(List<Event> list) {
		for (int i = 0; i < list.size(); i++)
			for (int j = list.size() - 1; j >= 0; j--) {
				Event ei = list.get(i);
				Event ej = list.get(j);
				if (ei.getDate().before(ej.getDate())) {
					list.set(i, ej);
					list.set(j, ei);
				}
			}
	}

	@SetupRender
	void onPrepareForRender() {
		StringBuilder sb = new StringBuilder();
		sb.append("datedEvents().size(): " + datedEvents().size());
		sb.append("\nschedule size: " + datesRange());
		System.out.println("\n\n" + sb.toString() + "\n\n");

		try {
			eventsArrayCached = getEventsArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
