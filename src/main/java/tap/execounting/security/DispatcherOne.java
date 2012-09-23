package tap.execounting.security;

import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.data.Entities;
import tap.execounting.entities.Client;
import tap.execounting.entities.Contract;
import tap.execounting.entities.Event;
import tap.execounting.entities.EventType;
import tap.execounting.entities.Facility;
import tap.execounting.entities.Payment;
import tap.execounting.entities.Teacher;
import tap.execounting.entities.User;
import tap.execounting.services.Authenticator;

public class DispatcherOne implements AuthorizationDispatcher {

	@Inject
	private Authenticator authenticator;

	public <T> boolean getPermission(User user, Class<T> targetEntityType,
			Operation operation) {
		return getPermission(user.getGroup(), targetEntityType, operation);
	}

	public <T> boolean getPermission(String group, Class<T> targetEntityType,
			Operation operation) {
		String admin = User.ADMIN;
		String manager = User.MANAGER;
		String target = targetEntityType.getSimpleName();

		if (group.equals(admin)) {
			return true;
		}
		if (group.equals(manager)) {
			byte row = Entities.getCode(target);
			byte column = operation.getCode();
			byte[][] permissionMatrix = new byte[7][4];
			permissionMatrix[Entities.CLIENT] = new byte[] { 1, 1, 1, 0 };
			permissionMatrix[Entities.CONTRACT] = new byte[] { 1, 1, 1, 0 };
			permissionMatrix[Entities.EVENT] = new byte[] { 1, 1, 1, 0 };
			permissionMatrix[Entities.EVENT_TYPE] = new byte[] { 1, 1, 1, 0 };
			permissionMatrix[Entities.PAYMENT] = new byte[] { 1, 1, 1, 0 };
			permissionMatrix[Entities.TEACHER] = new byte[] { 1, 1, 1, 0 };
			permissionMatrix[Entities.FACILITY] = new byte[] { 1, 1, 1, 0 };
			return permissionMatrix[row][column] == 1;
		} else
			throw new IllegalArgumentException("Group " + group
					+ " cannot be handled right now");
	}

	private User loggedUser() {
		return authenticator.getLoggedUser();
	}

	// PAYMENT
	// delete
	public boolean canDeletePayments() {
		return getPermission(loggedUser(), Payment.class, Operation.delete);
	}

	// update
	public boolean canEditPayments() {
		return getPermission(loggedUser(), Payment.class, Operation.update);
	}

	// create
	public boolean canCreatePayments() {
		return getPermission(loggedUser(), Payment.class, Operation.create);
	}

	// EVENT
	// create
	public boolean canCreateEvents() {
		return getPermission(loggedUser(), Event.class, Operation.create);
	}

	// delete
	public boolean canDeleteEvents() {
		return getPermission(loggedUser(), Event.class, Operation.delete);
	}

	// update
	public boolean canEditEvents() {
		return getPermission(loggedUser(), Event.class, Operation.update);
	}

	// CONTRACT
	// create
	public boolean canCreateContracts() {
		return getPermission(loggedUser(), Contract.class, Operation.create);
	}

	// delete
	public boolean canDeleteContracts() {
		return getPermission(loggedUser(), Contract.class, Operation.delete);
	}

	// update
	public boolean canEditContracts() {
		return getPermission(loggedUser(), Contract.class, Operation.update);
	}

	// TEACHER
	// delete
	public boolean canDeleteTeachers() {
		return getPermission(loggedUser(), Teacher.class, Operation.delete);
	}

	// create
	public boolean canCreateTeachers() {
		return getPermission(loggedUser(), Teacher.class, Operation.create);
	}

	// update
	public boolean canEditTeachers() {
		return getPermission(loggedUser(), Teacher.class, Operation.update);
	}

	// EVENT TYPE
	// update
	public boolean canEditEventTypes() {
		return getPermission(loggedUser(), EventType.class, Operation.update);
	}

	// create
	public boolean canCreateEventTypes() {
		return getPermission(loggedUser(), EventType.class, Operation.create);
	}

	// delete
	public boolean canDeleteEventTypes() {
		return getPermission(loggedUser(), EventType.class, Operation.delete);
	}

	// FACILITY
	// update
	public boolean canEditFacilities() {
		return getPermission(loggedUser(), Facility.class, Operation.update);
	}

	// create
	public boolean canCreateFacilities() {
		return getPermission(loggedUser(), Facility.class, Operation.create);
	}

	// delete
	public boolean canDeleteFacilities() {
		return getPermission(loggedUser(), Facility.class, Operation.delete);
	}

	// CLIENT
	// update
	public boolean canEditClients() {
		return getPermission(loggedUser(), Client.class, Operation.update);
	}

	// create
	public boolean canCreateClients() {
		return getPermission(loggedUser(), Client.class, Operation.create);
	}

	// delete
	public boolean canDeleteClients() {
		return getPermission(loggedUser(), Client.class, Operation.delete);
	}
}
