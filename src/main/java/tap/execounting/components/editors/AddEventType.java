package tap.execounting.components.editors;


import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.entities.EventType;

public class AddEventType {


	@Property
	@Persist
	private boolean updateMode;

	@Inject
	private CrudServiceDAO dao;

	@Property
	@Persist
	private EventType etype;

	public void setup(EventType et) {
		updateMode = true;
		etype = et;
	}

	public void reset() {
		etype = new EventType();
		updateMode = false;
	}

	void onSubmit() {
		if (updateMode) {
			dao.update(etype);
		} else {
			dao.create(etype);
		}
	}
}
