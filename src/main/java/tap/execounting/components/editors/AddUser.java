package tap.execounting.components.editors;


import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.dal.ChainMap;
import tap.execounting.entities.User;
import tap.execounting.pages.CRUD;
import tap.execounting.services.Authenticator;

import java.util.List;

public class AddUser {

	@Persist
	@Property
	private boolean updateMode;

    @Property
    private String newPassword;

    @Inject
    private Authenticator authenticator;

	@Inject
	private CRUDServiceDAO dao;

	@Persist
    @Property
	private User user;

    @Component
    private Zone formaZone;

    @Inject
    private Messages messages;

	public void setup(User f) {
        user = f;
		updateMode = true;
	}

	public void reset() {
		updateMode = false;
        user = new User();
	}

	Object onSuccess() {
        if (newPassword != null) {
            user.setPassword( User.generatePasswordHash(newPassword) );
        }

		if (updateMode) {
			dao.update(user);
		} else {
			dao.create(user);
		}

        return CRUD.class;
	}

    Object onFailure() {
        return formaZone.getBody();
    }

    void onValidateFromForm() throws ValidationException {
        List<User> users;

        if( !updateMode ) {
            if ( newPassword == null || newPassword.isEmpty() )
                throw new ValidationException( messages.get("password-required") );

            users = dao.findWithNamedQuery(User.BY_USERNAME,
                ChainMap.with("username", user.getUsername()));

            if ( users.size() != 0 )
                throw new ValidationException( messages.get("username-exists") );
        }

        ValidationException ve = new ValidationException( messages.get("fullname-exists") );
        users = dao.findWithNamedQuery(User.BY_FULLNAME,
                ChainMap.with("fullname", user.getFullname()));

        if ( users.size() != 0 ) {
            if ( updateMode ) {
                if ( users.size() > 1 )
                    throw ve;

                if (users.get(0).getId() != user.getId())
                    throw ve;
            } else {
                throw ve;
            }
        }
    }

    public boolean getAuthenticatedUser() {
        return updateMode && authenticator.getLoggedUser().getId() == user.getId();
    }
}
