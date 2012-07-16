package tap.execounting.pages;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.TeacherMediator;
import tap.execounting.entities.Teacher;

public class TeacherPage {

	@Inject
	@Property
	private TeacherMediator tMed;
	
	
	void setup(Teacher context) {
		tMed.setUnit(context);
	}
	
	
	
}
