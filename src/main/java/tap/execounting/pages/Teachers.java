package tap.execounting.pages;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.mediators.TeacherMediator;
import tap.execounting.entities.Teacher;

public class Teachers {
	@SuppressWarnings("unused")
	@Inject
	@Property
	private TeacherMediator tMed;
	
	@InjectPage
	private TeacherPage page;
	
	@SuppressWarnings("unused")
	private Object onActionFromTLink(Teacher context){
		page.setup(context);
		return page;
	}
	
}