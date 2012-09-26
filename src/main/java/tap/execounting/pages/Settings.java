package tap.execounting.pages;

import tap.execounting.services.Authenticator;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.entities.User;


/**
 * Allows the user to modify password
 * 
 * @author karesti
 */
public class Settings
{
    @Inject
    private CRUDServiceDAO crudServiceDAO;

    @Inject
    private Messages messages;

    @Inject
    private Authenticator authenticator;

    @InjectPage
    private Signin signin;
    
    @Property
    private String password;

    @Property
    private String verifyPassword;

    @Component
    private Form settingsForm;

    public Object onSuccess()
    {
        if (!verifyPassword.equals(password))
        {
            settingsForm.recordError(messages.get("error.verifypassword"));

            return null;
        }

        User user = authenticator.getLoggedUser();
        authenticator.logout();

        user.setPassword(password);

        crudServiceDAO.update(user);

        signin.setFlashMessage(messages.get("settings.password-changed"));
        
        return signin;
    }
}
