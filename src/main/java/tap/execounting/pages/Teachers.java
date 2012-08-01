package tap.execounting.pages;

import java.util.List;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.mediators.TeacherMed;
import tap.execounting.entities.Teacher;

public class Teachers {
	
	@Inject
	private TeacherMed tMed;
	
	@InjectPage
	private TeacherPage page;
	
	@SuppressWarnings("unused")
	private Object onActionFromTLink(Teacher context){
		page.setup(context);
		return page;
	}
	
	public List<Teacher> getAll(){
		return tMed.getAllTeachers();
	}
	
	public Teacher getUnit(){
		return tMed.getUnit();
	}
	
	public void setUnit(Teacher unit){
		tMed.setUnit(unit);
	}
}