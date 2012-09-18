package tap.execounting.security;

import tap.execounting.data.Entities;
import tap.execounting.entities.User;

public class DispatcherOne implements AuthorizationDispatcher {

	public <T> boolean getPermission(User user, Class<T> targetEntityType,
			Operation operation) {
		return getPermission(user.getGroup(), targetEntityType, operation);
	}

	public <T> boolean getPermission(String group, Class<T> targetEntityType,
			Operation operation) {
		String admin = User.ADMIN;
		String manager = User.MANAGER;
		String target = targetEntityType.getSimpleName();

		if (group == admin) {
			return true;
		}
		if (group == manager) {
			byte row = Entities.getCode(target);
			byte column = operation.getCode();
			byte[][] permissionMatrix = new byte[6][4];
			permissionMatrix[Entities.CLIENT] = new byte[] { 1, 1, 1, 0 };
			permissionMatrix[Entities.CONTRACT] = new byte[] { 1, 1, 1, 0 };
			permissionMatrix[Entities.EVENT] = new byte[] { 1, 1, 1, 0 };
			permissionMatrix[Entities.EVENT_TYPE] = new byte[] { 1, 1, 1, 0 };
			permissionMatrix[Entities.PAYMENT] = new byte[] { 1, 1, 1, 0 };
			permissionMatrix[Entities.TEACHER] = new byte[] { 1, 1, 1, 0 };
			return permissionMatrix[row][column] == 1;
		} else
			throw new IllegalArgumentException("Group " + group
					+ " cannot be handled right now");
	}
}
