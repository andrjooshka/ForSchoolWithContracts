package tap.execounting.pages;

import java.util.Date;
import java.util.List;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.dal.mediators.interfaces.EventMed;
import tap.execounting.data.EventState;
import tap.execounting.data.selectmodels.FacilitySelectModel;
import tap.execounting.data.selectmodels.RoomSelectModel;
import tap.execounting.data.selectmodels.TeacherSelectModel;
import tap.execounting.data.selectmodels.TypeTitleSelectModel;
import tap.execounting.entities.Event;
import tap.execounting.entities.EventType;
import tap.execounting.entities.Facility;
import tap.execounting.entities.Teacher;
import tap.execounting.services.DateService;

@Import(library = "context:/js/updateEffects.js")
public class Statistics {

	// Code Helpers
	@Inject
	private CRUDServiceDAO dao;
	@Inject
	private Session session;
	@Inject
	private Request request;
	@Inject
	private AjaxResponseRenderer renderer;
	@Inject
	private EventMed eventMed;

	// Page Components
	@Property
	private TeacherSelectModel teacherSelect;
	@Property
	private FacilitySelectModel facilitySelect;
	@Property
	private RoomSelectModel roomSelect;
	@Property
	private TypeTitleSelectModel typeSelect;
	@Component
	private Zone roomZone;
	@Component
	private Zone resultZone;
	@Component
	private Zone statZone;

	// Page Properties
	@Property
	@Persist
	private Integer facilityId;
	@Property
	@Persist
	private Integer teacherId;
	@Property
	@Persist
	private String typeId;
	@Property
	@Persist
	private Integer roomId;
	@Property
	@Persist
	private EventState state;
	@Property
	@Persist
	private Date date1;
	@Property
	@Persist
	private Date date2;
	@Property
	@Persist
	private Integer percent;

	private EventMed getEventMed() {
		return eventMed;
	}

	private List<Event> eventsCache;

	@SuppressWarnings("unchecked")
	public List<Event> getEvents() {
		if (eventsCache != null)
			return eventsCache.subList(0, eventsCache.size());
		List<Event> events;
		Criteria criteria = session.createCriteria(Event.class);

		if (facilityId != null) {
			criteria.add(Restrictions.eq("facilityId", facilityId));
			if (roomId != null)
				criteria.add(Restrictions.eq("roomId", roomId));
		}
		if (teacherId != null)
			criteria.add(Restrictions.eq("hostId", teacherId));
		if (date1 != null)
			criteria.add(Restrictions.ge("date", date1));

		if (date2 != null) {
			date2 = DateService.maxOutDayTime(date2);
			criteria.add(Restrictions.le("date", date2));
		}
		if (state != null && state != EventState.paid)
			criteria.add(Restrictions.eq("state", state.getCode()));

		events = criteria.list();

		if (typeId != null) {
			for (int i = events.size() - 1; i >= 0; i--)
				if (!events.get(i).getEventType().getTitle().contains(typeId))
					events.remove(i);
		}

		if (state == EventState.paid)
			eventMed.setGroup(events).filter(state);

		eventsCache = events;

		return events;
	}

	public int getMoney() {
		List<Event> events = getEvents();
		int summ = 0;
		for (Event e : events) {
			int typeId = e.getTypeId();
			EventType et = dao.find(EventType.class, typeId);
			if (e.getClients().size() > 0)
				summ += et.getPrice() * e.getClients().size();
			else
				summ += et.getPrice();
		}

		return summ;
	}

	public int getPercentedMoney() {
		if (percent != null) {
			return getMoney() * percent / 100;
		}
		return 0;
	}

	// school share
	public int getShare() {
		// int total = 0;
		// for (Event e : getEvents())
		// total += e.getSchoolShare();
		return getEventMed().setGroup(getEvents()).countSchoolMoney();
	}

	Object onValueChangedFromFacilityId(Integer facId) {
		System.out.println("\n\nInside on value changed\nfacId");
		facilityId = facId;
		roomSelect = facilityId == null ? new RoomSelectModel(null)
				: new RoomSelectModel(dao.find(Facility.class, facilityId));
		return roomZone.getBody();
	}

	void onSubmitFromFilterForm() {
		if (request.isXHR())
			renderer.addRender(resultZone).addRender(statZone);
	}

	void onPrepareForRender() {
		List<Teacher> teachers = dao.findWithNamedQuery(Teacher.ALL);
		teacherSelect = new TeacherSelectModel(teachers);
		List<Facility> facilities = dao.findWithNamedQuery(Facility.ALL);
		facilitySelect = new FacilitySelectModel(facilities);

		roomSelect = facilityId == null ? new RoomSelectModel(null)
				: new RoomSelectModel(dao.find(Facility.class, facilityId));
		typeSelect = new TypeTitleSelectModel(dao);
	}
}
