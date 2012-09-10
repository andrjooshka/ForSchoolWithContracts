package tap.execounting.pages;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.mediators.interfaces.EventMed;
import tap.execounting.dal.mediators.interfaces.TeacherMed;
import tap.execounting.entities.Contract;
import tap.execounting.entities.Event;
import tap.execounting.entities.TeacherAddition;

@Import(stylesheet = "context:/layout/payroll.css")
public class Payroll {

	@Inject
	@Property
	private TeacherMed tM;
	@Inject
	private EventMed eM;

	private TeacherAddition addition;
	@Property
	@Persist
	private Date dateOne;
	@Property
	@Persist
	private Date dateTwo;
	@Property
	private Contract contract;
	private int iteration;

	void onActivate(int teacherId) {
		tM.setId(teacherId);
	}

	int onPassivate() {
		return tM.getId();
	}

	void setupRender() {
		iteration = 0;
		dateOne = new Date();
		addition = tM.getAddition();
	}

	public int getIteration() {
		return ++iteration;
	}

	public Date getToday() {
		return new Date();
	}

	public String getField1() {
		return addition.getField_1();
	}

	public String getField2() {
		return addition.getField_2();
	}

	public String getField3() {
		return addition.getField_3();
	}

	public String getField4() {
		return addition.getField_4();
	}

	public String getField5() {
		return addition.getField_5();
	}

	public List<Contract> getContracts() {
		List<Event> source = raw();
		filter(source);
		List<Contract> contracts = toContracts(source);
		
		return contracts;
	}

	private List<Event> raw() {
		return eM.filter(dateOne, dateTwo).filter(tM.getUnit()).getGroup();
	}

	private void filter(List<Event> events) {

	}

	private List<Contract> toContracts(List<Event> source) {
		// TODO version for ansamble events
		List<Contract> contracts = new ArrayList<Contract>();
		while (source.size() > 0) {
			Event init = source.get(source.size() - 1);
			contracts.remove(contracts.size() - 1);

			Contract c = new Contract();
			Contract t = init.getContracts().get(0);
			c.setId(t.getId());
			c.setTypeId(t.getTypeId());

			for (int i = source.size() - 1; i >= 0; i--)
				if (source.get(i).getContracts().get(0).getId() == c.getId()) {
					c.getEvents().add(source.get(i));
					source.remove(i);
				}

		}
		return contracts;
	}
}
