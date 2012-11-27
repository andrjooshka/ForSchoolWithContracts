package tap.execounting.services;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.services.ComponentRequestFilter;
import org.apache.tapestry5.services.ComponentRequestHandler;
import org.apache.tapestry5.validator.ValidatorMacro;

import tap.execounting.dal.HibernateModule;
import tap.execounting.dal.mediators.MediatorModule;
import tap.execounting.security.AuthenticationFilter;
import tap.execounting.security.AuthorizationDispatcher;
import tap.execounting.security.DispatcherOne;
import fr.exanpe.t5.lib.services.ExanpeLibraryModule;

/**
 * This module is automatically included as part of the Tapestry IoC Registry,
 * it's a good place to configure and extend Tapestry, or to place your own
 * service definitions.
 */
@SubModule({ HibernateModule.class, MediatorModule.class,
		ExanpeLibraryModule.class, ValidationModule.class })
public class AppModule {
	public static void bind(ServiceBinder binder) {
		binder.bind(Authenticator.class, BasicAuthenticator.class);
		binder.bind(SuperCalendar.class, RusCalendar.class);
		binder.bind(BroadcastingService.class);
		binder.bind(AuthorizationDispatcher.class, DispatcherOne.class);
	}

	public static void contributeFactoryDefaults(
			MappedConfiguration<String, Object> configuration) {
		// configuration.override(SymbolConstants.APPLICATION_VERSION, "3.6");
		configuration.override(SymbolConstants.DATEPICKER, "assets/");
	}

	public static void contributeApplicationDefaults(
			MappedConfiguration<String, Object> configuration) {
		configuration.add(SymbolConstants.SUPPORTED_LOCALES, "ru");
		configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
		configuration.add(SymbolConstants.HMAC_PASSPHRASE, "fjads;lfkja;flkjqf;qlfkjqe;flkwjefw;lefkj");
	}

	public static void contributeClasspathAssetAliasManager(
			MappedConfiguration<String, String> configuration) {
		configuration.add("css", "assets/css");
		configuration.add("js", "assets/js");
	}

	@Contribute(ValidatorMacro.class)
	public static void combineValidators(
			MappedConfiguration<String, String> configuration) {
		configuration.add("username", "required, minlength=4, maxlength=15");
		configuration.add("password", "required, minlength=8, maxlength=12");
	}

	@Contribute(ComponentRequestHandler.class)
	public static void contributeComponentRequestHandler(
			OrderedConfiguration<ComponentRequestFilter> configuration) {
		configuration.addInstance("RequiresLogin", AuthenticationFilter.class);
	}
}
