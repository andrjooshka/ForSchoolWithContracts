package tap.execounting.pages;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.mediators.interfaces.ContractMed;
import tap.execounting.dal.mediators.interfaces.EventMed;
import tap.execounting.dal.mediators.interfaces.TeacherMed;
import tap.execounting.entities.Contract;
import tap.execounting.entities.ContractType;
import tap.execounting.entities.Event;
import tap.execounting.entities.TeacherAddition;
import tap.execounting.services.DateService;

@Import(stylesheet = "context:/layout/payroll.css")
public class Payroll {

	@Inject
	@Property
	private TeacherMed tM;
	@Inject
	private EventMed eM;
	@Inject
	private ContractMed cM;

	private TeacherAddition addition;
	@Property
	private Date dateOne;
	@Property
	private Date dateTwo;
	@Property
	private String filtration;
	@Property
	private Contract contract;
	private int iteration;
	private int totalMoney = 0;

	void onActivate(int teacherId) {
		onActivate(teacherId, null, null, "FilterOn");
	}

	boolean onActivate(int teacherId, String one, String two, String filtration) {
		tM.setId(teacherId);
		setDates(one, two);
		this.filtration = filtration;
		return true;
	}

	Object[] onPassivate() {
		return new Object[] { tM.getId(), dateOne, dateTwo, filtration };
	}

	void setupRender() {
		iteration = 0;
		addition = tM.getAddition();
	}

	private Date toDate(String s) {
		int day = Integer.valueOf(s.substring(0, 2));
		int month = Integer.valueOf(s.substring(3, 5));
		int year = Integer.valueOf(s.substring(6, 10));
		GregorianCalendar c = new GregorianCalendar();
		c.set(year, month - 1, day);
		return DateService.trimToDate(c.getTime());
	}

	private void setDates(String oneS, String twoS) {
		Date one = toDate(oneS);
		Date two = toDate(twoS);
		dateOne = one == null ? new Date() : one;
		dateTwo = two == null ? new Date() : two;
	}

	public int getIteration() {
		cM.setUnitId(contract.getId());
		totalMoney += getLessonPrice() * getLessonsNumber();
		return ++iteration;
	}

	public Date getToday() {
		return new Date();
	}

	public String getField1() {
		try {
			return addition.getField_1();
		} catch (NullPointerException e) {
			return "нет данных для отображения";
		}
	}

	public String getField2() {
		try {
			return addition.getField_2();
		} catch (NullPointerException e) {
			return "нет данных для отображения";
		}
	}

	public String getField3() {
		try {
			return addition.getField_3();
		} catch (NullPointerException e) {
			return "нет данных для отображения";
		}
	}

	public String getField4() {
		try {
			return addition.getField_4();
		} catch (NullPointerException e) {
			return "нет данных для отображения";
		}
	}

	public String getField5() {
		try {
			return addition.getField_5();
		} catch (NullPointerException e) {
			return "нет данных для отображения";
		}
	}

	public List<Contract> getContracts() {
		List<Event> source = raw();
		if (filtration.contains("On"))
			filter(source);
		List<Contract> contracts = toContracts(source);

		return contracts;
	}

	private List<Event> raw() {
		// step1
		eM.reset();
		return eM.filter(dateOne, dateTwo).filter(tM.getUnit()).getGroup();
	}

	private void filter(List<Event> events) {
		/*
		 * Logic of this algorithm is simple. For some teachers exist students
		 * that study with them for free. Such students are stored in the
		 * Map<Teacher,Student[]>. If in the contracts of the event one of those
		 * students exist, he will be automatically removed
		 */
		int hostid = tM.getUnit().getId();

		Set<Integer> tofilter = filterMap().keySet();

		for (Integer j : tofilter)
			if (hostid == j) {

				Integer[] clientsToFilter = filterMap().get(j);
				List<Contract> contracts;
				for (int i = events.size() - 1; i >= 0; i--) {
					contracts = events.get(i).getContracts();
					for (int k = contracts.size() - 1; k >= 0; k--) {
						for (Integer x : clientsToFilter) {
							if (contracts.size() > 0) {
								if (x == contracts.get(k).getClientId())
									contracts.remove(k);
							} else
								break;
						}
					}
					if (contracts.size() == 0)
						events.remove(i);

				}
				break;
			}

		/*
		 * In this part of the filter we will remove all events of trial
		 * contracts and where type contains "раз"
		 */
		for (int i = events.size() - 1; i >= 0; i--) {
			List<Contract> cts = events.get(i).getContracts();
			for (int j = cts.size() - 1; j >= 0; j--)
				if (cts.get(j).getContractTypeId() == ContractType.Trial
						|| cts.get(j).getEventType().getTitle().contains("раз"))
					cts.remove(j);
			if (cts.size() == 0)
				events.remove(i);
		}
	}

	private Map<Integer, Integer[]> filterMap() {
		Map<Integer, Integer[]> fm = new HashMap<Integer, Integer[]>();
		fm.put(6, new Integer[] { 21, 22, 23, 475 });
		fm.put(9, new Integer[] { 38 });
		fm.put(14, new Integer[] { 9, 14, 37, 39, 40, 112, 113, 116, 117, 260,
				404 });
		fm.put(17, new Integer[] { 133, 136, 141, 142, 143, 343, 344, 407 });
		fm.put(50, new Integer[] { 462 });

		return fm;
	}

	List<Contract> toContracts(List<Event> source) {
		List<Contract> contracts = new ArrayList<Contract>();
		while (source.size() > 0) {
			Event init = source.get(source.size() - 1);
			List<Contract> cts = init.getContracts();

			Contract c = new Contract();
			contracts.add(c);
			Contract t = cts.get(cts.size() - 1);
			cts.remove(cts.size() - 1);
			if (cts.size() == 0)
				source.remove(source.size() - 1);
			c.setId(t.getId());
			c.setTypeId(t.getTypeId());
			c.getEvents().add(init);

			for (int i = source.size() - 1; i >= 0; i--) {
				cts = source.get(i).getContracts();
				for (int j = cts.size() - 1; j > -1; j--)
					if (cts.get(j).getId() == c.getId()) {
						c.getEvents().add(source.get(i));
						cts.remove(j);
						if (cts.size() == 0)
							source.remove(i);
					}
			}
		}
		return contracts;
	}

	public String getName() {
		return cM.getClientName();
	}

	public String getType() {
		return eM.loadEventType(contract.getTypeId()).getTypeTitle();
	}

	public int getLessonPrice() {
		return eM.loadEventType(contract.getTypeId()).getShareTeacher();
	}

	public int getLessonsNumber() {
		return contract.getEvents().size();
	}

	public int getTotalMoney() {
		return totalMoney;
	}

	public int getTax() {
		return totalMoney * 13 / 100;
	}

	public int getTaxed() {
		return getTotalMoney() - getTax();
	}
}
