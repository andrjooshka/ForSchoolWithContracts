package tap.execounting.components.editors;

import java.util.List;

import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.dal.ChainMap;
import tap.execounting.entities.Client;
import tap.execounting.pages.CRUD;

/**
 * @author truth0
 */
public class AddClient {

	@Property
	@Persist
	private boolean updateMode;

	@Property
	@Persist
	private Client client;
	
	@Component
	private Zone formaZone;
	
	// Useful, infrastructure bits of code
	@Inject
	private Messages messages;
	@Inject
	private CRUDServiceDAO dao;

	public void setup(Client c) {
		updateMode = true;
		client = c;
	}

	public void reset() {
		client = new Client();
		updateMode = false;
	}

	// Submit handling
	void onValidateFromForm() throws ValidationException {
		String duplicateExceptionMessageName = "addclient.error.duplicate";
		ValidationException ve = new ValidationException(messages.get(duplicateExceptionMessageName));
		
		// First -- check if for name duplication
		List<Client> clients = dao.findWithNamedQuery(Client.BY_NAME,
				ChainMap.with("name", client.getName()).yo());
		
		// If there is no clients with such name -- it is good.
		if (clients.size() != 0) {
			// Else -- we could have two situations
			if (updateMode) {
				// If we have an update situation, and clients size more than -- something is wrong, for sure.
				if(clients.size()>1)
					throw ve;
				// If we found only one client -- CHECK ID. We should pass update, not duplicate
				if(clients.get(0).getId() != client.getId())
					throw ve;
			} else 
				// If we have creation situation, then we totally should have zero clients with such name
				throw ve;
		}
	}
	
	Object onFailure(){
		return formaZone;
	}
	
	Object onSuccess() {
		if (updateMode)
			dao.update(client);
		else
			dao.create(client);
		
		// CRUTCH alert. I don't know, how to else I could perform a page reload.
		// There should be a way, but not today.
		return CRUD.class;
	}
}
