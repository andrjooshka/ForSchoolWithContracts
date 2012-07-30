package tap.execounting.dal.mediators;

import org.apache.tapestry5.ioc.ServiceBinder;


/**
 * This module is automatically included as part of the Tapestry IoC Registry,
 * it's a good place to configure and extend Tapestry, or to place your own
 * service definitions.
 */
public class MediatorModule {
	public static void bind(ServiceBinder binder) {
		// binder.bind(MyServiceInterface.class, MyServiceImpl.class);

		// Make bind() calls on the binder object to define most IoC services.
		// Use service builder methods (example below) when the implementation
		// is provided inline, or requires more initialization than simply
		// invoking the constructor.
		
		binder.bind(DateFilter.class);
		binder.bind(ContractMed.class, ContractMediator.class);
		binder.bind(ClientMed.class, ClientMediator.class);
		binder.bind(PaymentMed.class, PaymentMediator.class);
		binder.bind(EventMed.class, EventMediator.class);
		//binder.bind(TeacherMed.class, TeacherMedImpl.class);
		binder.bind(TeacherMedImpl.class);
	}

}
