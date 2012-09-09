package tap.execounting.components.grids;

import java.util.Date;
import java.util.List;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;

import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.dal.mediators.interfaces.ClientMed;
import tap.execounting.entities.Client;
import tap.execounting.entities.Contract;
import tap.execounting.entities.Teacher;
import tap.execounting.pages.ClientPage;
import tap.execounting.services.SuperCalendar;

public class ClientGridNCD {
	@Inject
	private BeanModelSource beanModelSource;
	@Inject
	private ComponentResources componentResources;
	@Inject
	private SuperCalendar calendar;
	@Inject
	private ClientMed clientMed;
	@Inject
	private CrudServiceDAO dao;

	@Property
	@Parameter
	private List<Client> source;

	@Property
	private boolean editorActive;

	private BeanModel<Client> model;
	@Property
	private Client unit;
	@Property
	private Contract loopContract;
	@InjectPage
	private ClientPage clientPage;

	private ClientMed getClientMed() {
		return clientMed;
	}

	void setupRender() {
		if (model == null) {
			model = beanModelSource.createDisplayModel(Client.class,
					componentResources.getMessages());
			model.exclude("return", "firstPlannedPaymentDate", "date",
					"canceled");
			// model.add("teachers", null);
			model.add("state", null);
			model.add("contracts", null);
			model.reorder("id", "name", "contracts");
		}
	}

	public BeanModel<Client> getModel() {
		if (model == null) {
			model = beanModelSource.createDisplayModel(Client.class,
					componentResources.getMessages());
			// model.exclude("id,return");
			model.add("teachers", null);
			model.add("contracts", null);
			model.reorder("contracts");
		}
		return model;
	}

	Object onActionFromDetails(Client c) {
		clientPage.setup(c);
		return clientPage;
	}

	public String getTeachers() {
		StringBuilder sb = new StringBuilder();
		List<Teacher> ts = unit.getCurrentTeachers();
		for (int i = 0; i < ts.size(); i++) {
			sb.append(ts.get(i).getName());
			if (i < ts.size() - 1)
				sb.append(", ");
		}
		return sb.toString();
	}

	public String getFirstContractDate() {
		Date d = unit.getFirstContractDate();
		if (d == null)
			return "договоров по данному клиенту нет в базе";
		calendar.setTime(d);
		return calendar.stringByTuple("dayOfMonth", "month", "year");
	}

	public List<Contract> getContracts() {
		return getClientMed().setUnit(unit).getActiveContracts();
	}

	public String getContractInfo() {
		StringBuilder sb = new StringBuilder();
		Contract c = loopContract;
		if (c.getContractTypeId() != 1)
			sb.append(c.getContractType().getTitle() + ". ");

		sb.append(c.getEventType().getTitle() + " (" + c.getTeacher().getName()
				+ "). ");
		sb.append("Баланс: " + c.getBalance());

		return sb.toString();
	}

	public String getCssForBalance() {
		int balance = 0;
		Client c = dao.find(Client.class, unit.getId());
		balance = c.getBalance();

		// balance = unit.getBalance(); // good old
		if (balance < 0)
			return "debtor";
		if (balance > 0)
			return "creditor";
		return "neutral";
	}

	public String getState() {
		return getClientMed().setUnit(unit).getState().toString();
	}
}
