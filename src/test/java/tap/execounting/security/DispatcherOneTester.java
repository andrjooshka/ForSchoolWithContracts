//package tap.execounting.security;
//
//import org.junit.Test;
//
//import tap.execounting.entities.Contract;
//import tap.execounting.entities.Event;
//import tap.execounting.entities.Payment;
//import tap.execounting.entities.User;
//
//public class DispatcherOneTester {
//
//	@Test
//	public void testGetPermissionForGroup() {
//		// SET UP
//		User admin = getUser(User.ADMIN);
//		User manager = getUser(User.MANAGER);
//		DispatcherOne dis = new DispatcherOne();
//		boolean ap, mp;
//		Class<Payment> pay = Payment.class;
//		Class<Event> event = Event.class;
//		Class<Contract> con = Contract.class;
//		Operation delete = Operation.delete, update = Operation.update;
//
//		// ACTION DELETE
//		// admin
//		ap = dis.getPermission(admin, pay, delete);
//		ap &= dis.getPermission(admin, event, delete);
//		ap &= dis.getPermission(admin, con, delete);
//		// manager
//		mp = dis.getPermission(manager, pay, delete);
//		mp |= dis.getPermission(manager, event, delete);
//		mp |= dis.getPermission(manager, con, delete);
//
//		// ASSERT DELETE
//		assert (ap);
//		assert (!mp);
//
//		// ACTION UPDATE
//		// admin
//		ap = dis.getPermission(admin, pay, update);
//		ap &= dis.getPermission(admin, event, update);
//		ap &= dis.getPermission(admin, con, update);
//		// manager
//		mp = dis.getPermission(manager, pay, update);
//		mp |= dis.getPermission(manager, event, update);
//		mp |= dis.getPermission(manager, con, update);
//		
//		// ASSERT DELETE
//		assert (ap);
//		assert (mp);
//	}
//
//	private User getUser(String group) {
//		User u = new User();
//		u.setGroup(group);
//		return u;
//	}
//
//}
