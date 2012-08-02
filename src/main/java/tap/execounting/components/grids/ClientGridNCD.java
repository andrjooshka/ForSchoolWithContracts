package tap.execounting.components.grids;

import java.util.Date;
import java.util.List;

import tap.execounting.pages.ClientPage;
import tap.execounting.services.SuperCalendar;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;

import tap.execounting.entities.Client;
import tap.execounting.entities.Contract;
import tap.execounting.entities.Teacher;

public class ClientGridNCD {
	@Inject
	private BeanModelSource beanModelSource;
	@Inject
	private ComponentResources componentResources;
	@Inject
	private SuperCalendar calendar;

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

	void setupRender() {
		if (model == null) {
			model = beanModelSource.createDisplayModel(Client.class,
					componentResources.getMessages());
			model.exclude("return", "firstPlannedPaymentDate");
			// model.add("teachers", null);
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
		return unit.getContracts();
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
	
	public String getCssForBalance(){
		if(unit.getBalance()<0) return "debtor";
		if(unit.getBalance()>0) return "creditor";
		return "neutral";
	}
}
