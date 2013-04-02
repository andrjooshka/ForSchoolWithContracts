package tap.execounting.components.grids;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import tap.execounting.components.editors.AddUser;
import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.entities.User;
import tap.execounting.security.AuthorizationDispatcher;

import java.util.List;

public class UserGrid {
	@Inject
	private BeanModelSource beanModelSource;
	@Inject
	private ComponentResources componentResources;
	@Inject
	private CRUDServiceDAO dao;
	@InjectComponent
	private Zone ezone;
	@Inject
	@Property
	private AuthorizationDispatcher dispatcher;

	@InjectComponent
	private AddUser editor;
	@Property
	private boolean editorActive;
	@Property
	private BeanModel<User> model;
	@Property
	private User unit;

	public List<User> getSource() {
		return dao.findWithNamedQuery(User.ALL);
	}

	Object onActionFromEdit(User c) {
		// AUTHORIZATION MOUNT POINT
		if (dispatcher.canEditUsers()) {
			editorActive = true;
			editor.setup(c);
		}
		return ezone.getBody();
	}

	Object onActionFromAdd() {
		// AUTHORIZATION MOUNT POINT
		if (dispatcher.canCreateUsers()) {
			editorActive = true;
			editor.reset();
		}
		return ezone.getBody();
	}

	void onDelete(User c) {
		// AUTHORIZATION MOUNT POINT
		if (dispatcher.canDeleteUsers())
			dao.delete(User.class, c.getId());
	}

	void setupRender() {
		if (model == null) {
			model = beanModelSource.createDisplayModel(User.class,
					componentResources.getMessages());
			model.add("Action", null);
			model.exclude("id", "password", "admin", "manager", "top");
		}
	}
}
