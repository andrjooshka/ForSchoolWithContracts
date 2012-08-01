package tap.execounting.dal.mediators;

import org.apache.tapestry5.ioc.ServiceBinder;


public class MediatorModule {
	public static void bind(ServiceBinder binder) {
//		binder.bind(DateFilter.class);
//		binder.bind(ContractMed.class, ContractMediator.class);
//		binder.bind(ClientMed.class, ClientMediator.class);
//		binder.bind(PaymentMed.class, PaymentMediator.class);
//		binder.bind(EventMed.class, EventMediator.class);
		//binder.bind(TeacherMed.class, TeacherMedImpl.class);
		binder.bind(TeacherMed.class);
	}

}
