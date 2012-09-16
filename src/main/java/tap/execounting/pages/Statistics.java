package tap.execounting.pages;

import java.util.Date;
import java.util.List;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.dal.mediators.interfaces.EventMed;
import tap.execounting.data.EventState;
import tap.execounting.data.selectmodels.FacilitySelectModel;
import tap.execounting.data.selectmodels.RoomSelectModel;
import tap.execounting.data.selectmodels.TeacherSelectModel;
import tap.execounting.data.selectmodels.TypeTitleSelectModel;
import tap.execounting.entities.Event;
import tap.execounting.entities.EventType;
import tap.execounting.entities.Facility;
import tap.execounting.services.BroadcastingService;

public class Statistics {

	// Code Helpers
	@Inject
	private CrudServiceDAO dao;
	@Inject
	private Session session;
	@Inject
	private Request request;
	@Inject
	private AjaxResponseRenderer renderer;
	@Inject
	private EventMed eventMed;
	@Inject
	private BroadcastingService broadcaster;

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
		// if (typeId != null)
		// criteria.add(Restrictions.eq("typeId", typeId));
		if (date1 != null) {
			criteria.add(Restrictions.gt("date", date1));
			broadcaster.cast(date1.toString());
		}
		if (date2 != null) {
			criteria.add(Restrictions.lt("date", date2));
			broadcaster.cast(date2.toString());
		}
		if (state != null)
			criteria.add(Restrictions.eq("state", state.getCode()));

		events = criteria.list();

		if (typeId != null) {
			for (int i = events.size() - 1; i >= 0; i--)
				if (!events.get(i).getEventType().getTitle().contains(typeId))
					events.remove(i);
		}

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
				summ += et.getMoney() * e.getClients().size();
			else
				summ += et.getMoney();
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
		teacherSelect = new TeacherSelectModel(dao);
		facilitySelect = new FacilitySelectModel(dao);
		roomSelect = facilityId == null ? new RoomSelectModel(null)
				: new RoomSelectModel(dao.find(Facility.class, facilityId));
		typeSelect = new TypeTitleSelectModel(dao);
	}
}
