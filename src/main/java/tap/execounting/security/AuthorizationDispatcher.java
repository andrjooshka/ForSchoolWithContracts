package tap.execounting.security;

import tap.execounting.entities.User;

public interface AuthorizationDispatcher {

	public <T> boolean getPermission(User user, Class<T> targetEntityType,
			Operation operation);

	public <T> boolean getPermission(String group, Class<T> targetEntityType,
			Operation operation);
}
