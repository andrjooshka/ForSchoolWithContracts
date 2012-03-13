package tap.execounting.pages;

import tap.execounting.annotations.AnonymousAccess;
import tap.execounting.services.Authenticator;

import org.apache.tapestry5.ioc.annotations.Inject;


/**
 * Start page of application tapestry5-hotel-booking.
 */
@AnonymousAccess
public class Index
{
    @Inject
    private Authenticator authenticator;

    public Object onActivate()
    {
        return authenticator.isLoggedIn() ? General.class : Signin.class;
    }
}
