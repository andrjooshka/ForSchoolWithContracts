package tap.execounting.pages;

import static java.lang.System.out;

import java.util.Date;
import java.util.List;

import tap.execounting.services.SuperCalendar;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.components.editors.AddContract;
import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.entities.Client;
import tap.execounting.entities.Contract;

public class ClientPage {

	@Property
	@Persist
	private Client client;
	@Inject
	private CrudServiceDAO dao;
	private boolean editorActive;
	@Property
	@Persist
	private boolean editorActiveAlways;
	@Component
	private Zone ezone;
	@Persist
	@Property
	private boolean constantEventEditor;

	@InjectComponent
	private AddContract editor;
	@SuppressWarnings("unused")
	@Property
	private Contract contract;
	@SuppressWarnings("unused")
	@Property
	private boolean mode;
	@Inject
	private SuperCalendar calendar;

	public void setup(Client c) {
		client = c;
		c.getContracts().size();
		editor.setup(c);
	}

	public List<Contract> getContracts() {
		List<Contract> cl = client.getContracts();
		for (int i = 0; i < cl.size(); i++)
			for (int j = cl.size() - 1; j > i; j--) {
				if (cl.get(i).getDate().before(cl.get(j).getDate())) {
					Contract c = cl.get(i);
					cl.set(i, cl.get(j));
					cl.set(j, c);
				}
			}
		return cl;
	}

	public String getFirstContractDate() {
		if (getContracts().size() == 0)
			return "договоров по данному клиенту нет в базе";
		Date d = getContracts().get(0).getDate();
		for (Contract c : client.getContracts()) {
			if (c.getDate().compareTo(d) < 0)
				d = c.getDate();
		}
		calendar.setTime(d);
		return calendar.stringByTuple("dayOfMonth", "month", "year");
	}

	public String getCurrentTeachersInfo() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < client.getCurrentTeachers().size(); i++) {
			sb.append(client.getCurrentTeachers().get(i).getName());
			if (i < client.getCurrentTeachers().size() - 1)
				sb.append(", ");
		}
		return sb.toString();
	}

	public boolean getEditorActive() {
		return editorActive || editorActiveAlways;
	}

	// event handling
	void onPrepare() {
		out.println("\n\nOnPrepare");
		out.println("Number of contracts: " + client.getContracts().size());
		client = dao.find(Client.class, client.getId());
		setup(client);
	}

	Object onAddContract() {
		editorActive = true;
		return ezone.getBody();
	}
}