package tap.execounting.pages;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;

import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.dal.HibernateCrudServiceDAO;
import tap.execounting.dal.mediators.TeacherMediator;
import tap.execounting.data.FacilitySelectModel;
import tap.execounting.entities.Client;
import tap.execounting.entities.Teacher;

public class TeacherPage {

	@Inject
	private AjaxResponseRenderer renderer;
	
	@Component
	private Zone scheduleZone;
	
	@Inject
	@Property
	private TeacherMediator tMed;
	
	@Property
	private boolean scheduleEdit;
	
	@Property
	private Client tClient;
	
	@Property
	private String[] days = {"Пн","Вт","Ср","Чт","Пт","Сб","Вс"};
	
	@Property
	private String day;
	
	@Property
	private FacilitySelectModel facilitySelectModel;
	
	void setup(Teacher context) {
		tMed.setUnit(context);
	}
	
	void onActionFromScheduleEditLink(){
		scheduleEdit = true;
		renderer.addRender(scheduleZone);
	}
	
	@Inject
	private CrudServiceDAO dao;
	
	void onPrepareForRender(){
		if(facilitySelectModel==null)
			facilitySelectModel = new FacilitySelectModel(dao);
	}
}
