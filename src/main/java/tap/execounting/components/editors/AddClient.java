package tap.execounting.components.editors;

import java.util.ArrayList;
import java.util.List;


import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.entities.Client;
import tap.execounting.entities.Teacher;

public class AddClient {

	@Property
	@Persist
	private boolean updateMode;

	@Inject
	private CrudServiceDAO dao;

	@Property
	@Persist
	private Client client;

	public void setup(Client c) {
		updateMode = true;
		client = c;
	}

	public void reset() {
		client = new Client();
		updateMode = false;
	}

	void onSuccess() {
		if (updateMode) {
			dao.update(client);
		} else {
			dao.create(client);
		}
	}

	List<String> onProvideCompletions(String starts) {
		List<String> res = new ArrayList<String>();
		for (String name : names())
			if (name.toLowerCase().contains(starts.toLowerCase()))
				res.add(name);
		return res;
	}

	private List<String> names() {
		return dao.findWithNamedQuery(Teacher.ALL_NAMES);
	}
}
