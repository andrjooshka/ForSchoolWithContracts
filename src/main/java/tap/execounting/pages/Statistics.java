package tap.execounting.pages;

import java.util.Date;
import java.util.List;

import org.apache.tapestry5.SelectModel;
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
import tap.execounting.data.BooleanSelectModel;
import tap.execounting.data.FacilitySelectModel;
import tap.execounting.data.RoomSelectModel;
import tap.execounting.data.TeacherSelectModel;
import tap.execounting.data.TypeSelectModel;
import tap.execounting.entities.Event;
import tap.execounting.entities.EventType;
import tap.execounting.entities.Facility;

public class Statistics {

	// code helpers
	@Inject
	private CrudServiceDAO dao;
	@Inject
	private Session session;
	@Inject
	private Request request;
	@Inject
	private AjaxResponseRenderer renderer;

	// page components
	@SuppressWarnings("unused")
	@Property
	private TeacherSelectModel teacherSelect;
	@SuppressWarnings("unused")
	@Property
	private FacilitySelectModel facilitySelect;
	@SuppressWarnings("unused")
	@Property
	private RoomSelectModel roomSelect;
	@SuppressWarnings("unused")
	@Property
	private TypeSelectModel typeSelect;
	@SuppressWarnings("unused")
	@Property
	private SelectModel boolSelect;
	@Component
	private Zone roomZone;
	@Component
	private Zone resultZone;
	@Component
	private Zone statZone;

	// page properties
	@Property
	@Persist
	private Integer facilityId;
	@Property
	@Persist
	private Integer teacherId;
	@Property
	@Persist
	private Integer typeId;
	@Property
	@Persist
	private Integer roomId;
	@Property
	@Persist
	private String state;
	@Property
	@Persist
	private Date date1;
	@Property
	@Persist
	private Date date2;
	@Property
	@Persist
	private Integer percent;

	@SuppressWarnings("unchecked")
	public List<Event> getEvents() {
		List<Event> events;
		Criteria criteria = session.createCriteria(Event.class);

		if (facilityId != null) {
			criteria.add(Restrictions.eq("facilityId", facilityId));
			if (roomId != null)
				criteria.add(Restrictions.eq("roomId", roomId));
		}
		if (teacherId != null)
			criteria.add(Restrictions.eq("hostId", teacherId));
		if (typeId != null)
			criteria.add(Restrictions.eq("typeId", typeId));
		if (date1 != null)
			criteria.add(Restrictions.gt("date", date1));
		if (date2 != null)
			criteria.add(Restrictions.lt("date", date2));
		if (state != null)
			criteria.add(Restrictions.eq("state", Boolean.parseBoolean(state)));

		events = criteria.list();
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
		int total = 0;
		for (Event e : getEvents())
			total += e.getSchoolShare();
		return total;
	}

	Object onValueChangedFromFacilityId(Integer facId) {
		System.out.println("\n\nInside on value changed\nfacId");
		facilityId = facId;
		roomSelect = facilityId == null ? new RoomSelectModel(null)
				: new RoomSelectModel(dao.find(Facility.class, facilityId));
		return roomZone.getBody();
	}

	void onSubmitFromFilterForm() {
		if(request.isXHR()) renderer.addRender(resultZone).addRender(statZone);
	}

	void onPrepareForRender() {
		teacherSelect = new TeacherSelectModel(dao);
		facilitySelect = new FacilitySelectModel(dao);
		boolSelect = new BooleanSelectModel();
		roomSelect = facilityId == null ? new RoomSelectModel(null)
				: new RoomSelectModel(dao.find(Facility.class, facilityId));
		typeSelect = new TypeSelectModel(dao);
	}
}
