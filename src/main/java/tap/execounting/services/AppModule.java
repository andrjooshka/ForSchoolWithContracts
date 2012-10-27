package tap.execounting.services;

import java.io.IOException;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.services.ComponentRequestFilter;
import org.apache.tapestry5.services.ComponentRequestHandler;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestHandler;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.validator.ValidatorMacro;
import org.slf4j.Logger;

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
		configuration.override(SymbolConstants.APPLICATION_VERSION, "3.5");
		configuration.override(SymbolConstants.DATEPICKER, "assets/");
	}

	public static void contributeApplicationDefaults(
			MappedConfiguration<String, Object> configuration) {
		configuration.add(SymbolConstants.SUPPORTED_LOCALES, "ru");
		configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
	}

	public static void contributeClasspathAssetAliasManager(
			MappedConfiguration<String, String> configuration) {
		configuration.add("css", "assets/css");
		configuration.add("js", "assets/js");
	}

	@Contribute(ValidatorMacro.class)
	public static void combineValidators(
			MappedConfiguration<String, String> configuration) {
		configuration.add("username", "required, minlength=3, maxlength=15");
		configuration.add("password", "required, minlength=6, maxlength=12");
	}

	@Contribute(ComponentRequestHandler.class)
	public static void contributeComponentRequestHandler(
			OrderedConfiguration<ComponentRequestFilter> configuration) {
		configuration.addInstance("RequiresLogin", AuthenticationFilter.class);
	}

	public RequestFilter buildTimingFilter(final Logger log) {
		return new RequestFilter() {
			public boolean service(Request request, Response response,
					RequestHandler handler) throws IOException {
				long startTime = System.currentTimeMillis();

				try {
					return handler.service(request, response);
				} finally {
					long elapsed = System.currentTimeMillis() - startTime;

					log.info(String.format("Request time: %d ms", elapsed));
				}
			}
		};
	}
	public void contributeRequestHandler(
			OrderedConfiguration<RequestFilter> configuration,
			@Local RequestFilter filter) {
		configuration.add("Timing", filter);
	}
}
