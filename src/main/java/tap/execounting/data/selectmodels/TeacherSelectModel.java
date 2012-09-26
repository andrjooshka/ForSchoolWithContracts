package tap.execounting.data.selectmodels;

import java.util.ArrayList;
import java.util.List;


import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.util.AbstractSelectModel;

import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.entities.Teacher;

public class TeacherSelectModel extends AbstractSelectModel {

	private List<OptionModel> options = new ArrayList<OptionModel>(3);

	public TeacherSelectModel(CRUDServiceDAO dao) {
		List<Teacher> teachers = dao.findWithNamedQuery(Teacher.ALL);
		for (Teacher t : teachers)
			options.add(new OptionModelImpl(t.getName(), t.getId()));
	}

	public List<OptionGroupModel> getOptionGroups() {
		return null;
	}

	public List<OptionModel> getOptions() {
		return options;
	}
}
