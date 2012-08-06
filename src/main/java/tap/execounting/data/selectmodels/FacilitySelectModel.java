package tap.execounting.data.selectmodels;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.util.AbstractSelectModel;

import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.entities.Facility;

public class FacilitySelectModel extends AbstractSelectModel {
	
	private List<OptionModel> options = new ArrayList<OptionModel>(3);

	public FacilitySelectModel(CrudServiceDAO dao) {
		List<Facility> facilities = dao.findWithNamedQuery(Facility.ALL);
		for (Facility f : facilities)
			options.add(new OptionModelImpl(f.getName(), f.getFacilityId()));
	}

	public List<OptionGroupModel> getOptionGroups() {
		return null;
	}

	public List<OptionModel> getOptions() {
		return options;
	}
}
