package tap.execounting.pages;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;

import tap.execounting.components.editors.AddComment;
import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.dal.QueryParameters;
import tap.execounting.dal.mediators.interfaces.ContractMed;
import tap.execounting.dal.mediators.interfaces.EventMed;
import tap.execounting.dal.mediators.interfaces.TeacherMed;
import tap.execounting.data.ContractState;
import tap.execounting.data.EventRowElement;
import tap.execounting.data.EventState;
import tap.execounting.data.selectmodels.FacilitySelectModel;
import tap.execounting.entities.Client;
import tap.execounting.entities.Comment;
import tap.execounting.entities.Contract;
import tap.execounting.entities.Event;
import tap.execounting.entities.Facility;
import tap.execounting.entities.Teacher;
import tap.execounting.security.AuthorizationDispatcher;
import tap.execounting.services.ContractByClientComparator;
import tap.execounting.services.DateService;

@Import(stylesheet = { "context:/layout/weekschedule.css",
		"context:/layout/teacherpage.css", "context:/layout/cardtable.css" })
public class TeacherPage {

	@Inject
	private AjaxResponseRenderer renderer;
	@Inject
	private PageRenderLinkSource linkSource;
	@Inject
	private AuthorizationDispatcher dispatcher;

	@InjectComponent
	private Zone scheduleZone;
	@InjectComponent
	private Zone statsZone;
	@InjectComponent
	private Zone contractZone;
	@InjectComponent
	private Zone clientsZone;
	@Component
	private AddComment addComment;

	@InjectPage
	private ClientPage clientPage;

	@Inject
	@Property
	private TeacherMed tMed;

	@Inject
	private EventMed eMed;

	@Inject
	private ContractMed cMed;

	@Property
	private Comment comment;

	@Property
	private boolean scheduleEdit;

	@Property
	private boolean calendarCentric = true;

	@Property
	private boolean showDeleted;

	@Property
	private Contract contract;

	@Property
	private Event lesson;

	@Property
	private String[] days = { "Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс" };

	@Property
	private String day;

	@Property
	@Persist
	private Date payrollDateOne;
	@Property
	@Persist
	private Date payrollDateTwo;
	@Property
	@Persist
	private boolean payrollFiltration;

	@Property
	@Persist
	private Date date1;

	@Property
	@Persist
	private Date date2;

	@Persist
	private Date eventsDate;

	@Property
	private FacilitySelectModel facilitySelectModel;

	void onActivate(int teacherId) {
		tMed.setUnit(dao.find(Teacher.class, teacherId));
		if (date1 == null)
			date1 = DateService.trimToMonth(new Date());
	}

	int onPassivate() {
		return tMed.getUnit().getId();
	}

	void setup(Teacher context) {
		tMed.setUnit(context);
		date1 = DateService.trimToMonth(new Date());
	}

	void onActionFromScheduleEditLink() {
		// AUTHORIZATION MOUNT POINT TEACHER.SCHEDULE EDIT
		if (dispatcher.canEditTeachers())
			scheduleEdit = true;
		renderer.addRender(scheduleZone);
	}

	@Inject
	private CRUDServiceDAO dao;


	void onPrepareForRender() {
		if (facilitySelectModel == null) {
			List<Facility> facilities = dao.findWithNamedQuery(Facility.ACTUAL);
			facilitySelectModel = new FacilitySelectModel(facilities);
		}
	}

	Object onSuccessFromStatsDateForm() {
		return statsZone;
	}

	Object onSuccessFromScheduleForm() {
		scheduleEdit = false;
		dao.update(tMed.getUnit());
		return scheduleZone;
	}

	Object onSubmitFromClientsDateForm() {
		return clientsZone;
	}

	Object onActionFromEventsDateBackwardLink() {
		setEventsDate(DateService.datePlusMonths(getEventsDate(), -1));
		return clientsZone;
	}

	Object onActionFromEventsDateForwardLink() {
		setEventsDate(DateService.datePlusMonths(getEventsDate(), 1));
		return clientsZone;
	}

	Object onActionFromClientPageLink(Client c) {
		clientPage.setup(c);
		return clientPage;
	}

	Object onSuccessFromPayrollForm() {
		String filterString = payrollFiltration ? "FilterOn" : "FilterOff";
		return linkSource.createPageRenderLinkWithContext(Payroll.class, tMed
				.getUnit().getId(), getPayrollDateOneS(), getPayrollDateTwoS(),
				filterString);
	}

	// requests from page
	public Date getEventsDate() {
		if (eventsDate == null)
			eventsDate = DateService.trimToMonth(new Date());
		return eventsDate;
	}

	public void setEventsDate(Date date) {
		eventsDate = date;
	}

	public String getSchool() {
		return tMed.getSchoolForDay(day);
	}

	public String getMonthName() {
		return DateService.monthName(getEventsDate());
	}

	public List<Event> getShiftedLessons() {
		return eMed.filter(tMed.getUnit()).filter(EventState.movedByTeacher)
				.getGroup(true);
	}

	public List<Contract> getActiveContracts() {
		cMed.reset();
		Collections.sort(
				cMed.filter(tMed.getUnit()).filter(ContractState.active).removeComlete()
						.getGroup(), new ContractByClientComparator());
		return cMed.getGroup();
	}

	public List<Contract> getOtherContracts() {
		List<Contract> other = dao.findWithNamedQuery(Contract.WITH_CLIENT,
				QueryParameters.with("clientId", contract.getClientId())
						.parameters());
		for (int i = other.size() - 1; i >= 0; i--)
			if (other.get(i).getId() == contract.getId())
				other.remove(i);
		Collections.sort(other);
		Collections.reverse(other);
		if (other.size() > 15)
			other = other.subList(0, 15);
		return other;
	}

	public List<Contract> getFrozenContracts() {
		return tMed.getFrozenContracts();
	}

	public List<Contract> getOtherTeacherContracts() {
		List<Contract> list = tMed.getInactiveContracts();
		list.addAll(tMed.getCanceledContracts());
		list.addAll(tMed.getCompleteContracts());
		return list;
	}

	public String getClientName() {
		return contract.getClient().getName();
	}

	public String getDiscipline() {
		return contract.getEventType().getTypeTitle();
	}

	public List<EventRowElement> getElements() {
		List<EventRowElement> list = new ArrayList<EventRowElement>();

		for (Date d : DateService.generateDaySet(getEventsDate(),
				getRenderDays())) {
			List<Event> events = contract.getEvents(d);
			if (events.size() == 0)
				list.add(new EventRowElement(d, null));
			else
				for (Event e : events)
					list.add(new EventRowElement(d, e));
		}
		return list;
	}

	public int getWidth() {
		return getRenderDays() * 60 + 300;
	}

	private int getRenderDays() {
		Calendar c = DateService.getMoscowCalendar();
		c.setTime(getEventsDate());
		return c.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	// Get dates for calendar
	public List<EventRowElement> getDates() {
		List<EventRowElement> list = new ArrayList<EventRowElement>();
		Date now = DateService.trimToDate(new Date());

		// Generating date set. Magically.
		for (Date d : DateService.generateDaySet(getEventsDate(),
				getRenderDays())) {
			Event e = new Event();
			e.setDate(d);
			int dow = DateService.dayOfWeekRus(d);

			// First do something with days that are today or later.
			if (!d.before(now)) {
				Integer sched = tMed.getUnit().getScheduleDay(dow);
				if (sched != null && sched > 0)
					e.setState(EventState.planned);
				else
					e = null;
			} else {
				// Then think about days before today.
				// Find events
				List<Event> evs = dao.findWithNamedQuery(
						Event.BY_TEACHER_ID_AND_DATE,
						QueryParameters
								.with("teacherId", tMed.getUnit().getId())
								.and("earlierDate", d).and("laterDate", d)
								.parameters());
				if (evs.size() > 0) {
					// If you did find some, then mark the day as planned
					e.setState(EventState.planned);

					// Then search for complete events to mark day as complete
					for (Event ev : evs) {
						EventState es = ev.getState();
						if (es == EventState.complete) {
							e.setState(es);
							break;
						}
					}

				} else if (tMed.getUnit().getScheduleDay(dow) != null)
					e.setState(EventState.planned);
				else
					e = null;
			}

			list.add(new EventRowElement(d, e));
		}

		return list;
	}

	public int getLessonsComplete() {
		return tMed.getLessonsComplete(date1, date2);
	}

	public int getLessonsFailed() {
		return tMed.getLessonsFailed(date1, date2);
	}

	public int getLessonsFailedByClient() {
		return tMed.getLessonsFailedByClient(date1, date2);
	}

	public int getLessonsFailedByTeacher() {
		return tMed.getLessonsFailedByTeacher(date1, date2);
	}

	public int getDaysWorked() {
		return tMed.getDaysWorked(date1, date2);
	}

	public int getMoneyEarned() {
		return tMed.getMoneyEarned(date1, date2);
	}

	public int getMoneyEarnedForSchool() {
		return tMed.getMoneyEarnedForSchool(date1, date2);
	}

	public int getMoneyEarnedForSelf() {
		return tMed.getMoneyEarnedForSelf(date1, date2);
	}

	public List<Comment> getComments() {
		return tMed.getComments();
	}

	public String getPayrollDateOneS() {
		SimpleDateFormat t = new SimpleDateFormat("dd.MM.YYYY");
		return t.format(payrollDateOne);
	}

	public String getPayrollDateTwoS() {
		SimpleDateFormat t = new SimpleDateFormat("dd.MM.YYYY");
		return t.format(payrollDateTwo);
	}
}
