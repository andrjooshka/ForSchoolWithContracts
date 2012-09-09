package tap.execounting.pages;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import tap.execounting.entities.Client;

public class ClientsPageTest {

	@Test
	public void testArrayCopy() {
		// SETUP
		List<Client> clients = new ArrayList<Client>();
		// setting first client;
		Client c = new Client();
		c.setName("Tanya");
		clients.add(c);
		// setting second one
		c = new Client();
		c.setName("Masha");
		clients.add(c);

		// ACTION

		// performing copy
		List<Client> copy = new ArrayList<Client>(clients);
		clients.remove(1);

		// ASSERT
		assert (copy.get(1) != null);
	}

}
