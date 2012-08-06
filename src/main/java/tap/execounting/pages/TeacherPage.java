package tap.execounting.pages;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;

import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.dal.mediators.ContractMediator;
import tap.execounting.dal.mediators.interfaces.ContractMed;
import tap.execounting.dal.mediators.interfaces.EventMed;
import tap.execounting.dal.mediators.interfaces.TeacherMed;
import tap.execounting.data.ContractState;
import tap.execounting.data.EventRowElement;
import tap.execounting.data.selectmodels.FacilitySelectModel;
import tap.execounting.entities.Contract;
import tap.execounting.entities.Event;
import tap.execounting.entities.Teacher;
import tap.execounting.services.DateService;
@Import(stylesheet="context:/layout/weekschedule.css")
public class TeacherPage {

	@Inject
	private AjaxResponseRenderer renderer;

	@Component
	private Zone scheduleZone;

	@Component
	private Zone statsZone;

	@Inject
	@Property
	private TeacherMed tMed;

	@Inject
	private EventMed eMed;

	@Inject
	private ContractMed cMed;

	@Property
	private boolean scheduleEdit;

	@Property
	private boolean calendarCentric=true;

	@Property
	private Contract contract;

	@Property
	private String[] days = { "Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс" };

	@Property
	private String day;

	@Property
	@Persist
	private Date date1;

	@Property
	@Persist
	private Date date2;

	@Property
	@Persist
	private Date eventsDate;

	@Property
	private FacilitySelectModel facilitySelectModel;

	void setup(Teacher context) {
		tMed.setUnit(context);
	}

	void onActionFromScheduleEditLink() {
		scheduleEdit = true;
		renderer.addRender(scheduleZone);
	}

	@Inject
	private CrudServiceDAO dao;

	void onPrepareForRender() {
		if (facilitySelectModel == null)
			facilitySelectModel = new FacilitySelectModel(dao);
	}
	void onActivate(){
		eventsDate = new Date();
	}

	Object onSuccessFromStatsDateForm() {
		return statsZone;
	}
	
	Object onSuccessFromScheduleForm(){
		scheduleEdit= false;
		dao.update(tMed.getUnit());
		return scheduleZone;
	}

	// requests from page
	public String getSchool() {
		return tMed.getSchoolForDay(day);
	}
	
	public String getMonthName(){
		return DateService.monthName(eventsDate);
	}

	public List<Contract> getContracts() {
		ContractMed cMed = new ContractMediator();
		cMed.setDao(dao);
		return cMed.filter(tMed.getUnit()).filter(ContractState.active)
				.getGroup();
	}

	public String getClientName() {
		return contract.getClient().getName();
	}

	public List<EventRowElement> getElements() {
		int days = 9;
		List<EventRowElement> list = new ArrayList<EventRowElement>();
		eventsDate = new Date();
		for (Date d : DateService.generateDaySet(eventsDate, days)) {
			List<Event> events = contract.getEvents(d);
			if (events.size() == 0)
				list.add(new EventRowElement(d, null));
			else
				for (Event e : events)
					list.add(new EventRowElement(d, e));
		}
		return list;
	}

	public List<EventRowElement> getDates() {
		int days = 9;
		List<EventRowElement> list = new ArrayList<EventRowElement>();
		eventsDate = new Date();
		for (Date d : DateService.generateDaySet(eventsDate, days))
			list.add(new EventRowElement(d, null));
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
}
