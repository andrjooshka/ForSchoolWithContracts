package tap.execounting.pages;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.dal.TeacherMediator;
import tap.execounting.entities.Teacher;

public class Teachers {
	@Inject
	@Property
	private TeacherMediator tMed;
	
	@InjectPage
	private TeacherPage page;
	
	private Object onActionFromTLink(Teacher context){
		page.setup(context);
		return page;
	}
	
}