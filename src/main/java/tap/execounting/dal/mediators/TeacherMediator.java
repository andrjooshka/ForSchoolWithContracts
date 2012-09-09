package tap.execounting.dal.mediators;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.dal.QueryParameters;
import tap.execounting.dal.mediators.interfaces.ClientMed;
import tap.execounting.dal.mediators.interfaces.ContractMed;
import tap.execounting.dal.mediators.interfaces.EventMed;
import tap.execounting.dal.mediators.interfaces.TeacherMed;
import tap.execounting.data.ContractState;
import tap.execounting.data.EventState;
import tap.execounting.entities.Client;
import tap.execounting.entities.Comment;
import tap.execounting.entities.Contract;
import tap.execounting.entities.Facility;
import tap.execounting.entities.Teacher;

public class TeacherMediator implements TeacherMed {

	@Inject
	private CrudServiceDAO dao;
	@Inject
	private EventMed eventMed;
	@Inject
	private ClientMed clientMed;
	@Inject
	private ContractMed contractMed;

	private CrudServiceDAO getDao() {
		return dao;
	}

	private ClientMed getClientMed() {
		return clientMed;
	}
	private ContractMed getContractMed(){
		return contractMed;
	}
	private EventMed getEventMed() {
		return eventMed;
	}

	public Teacher unit;

	public List<Teacher> getAllTeachers() {
		return getDao().findWithNamedQuery(Teacher.ALL);
	}

	public Teacher getUnit() {
		return unit;
	}

	public TeacherMed setUnit(Teacher unit) {
		this.unit = unit;
		clearCaches();
		return this;
	}

	private void clearCaches() {
		allContractsCache = null;
	}

	public List<Comment> getComments() {
		return getDao().findWithNamedQuery(Comment.BY_TEACHER_ID,
				QueryParameters.with("teacherId", unit.getId()).parameters());
	}

	private List<Contract> allContractsCache;

	public List<Contract> getAllContracts() {
		if (allContractsCache == null)
			allContractsCache = getDao().findWithNamedQuery(
					Contract.WITH_TEACHER,
					QueryParameters.with("teacherId", unit.getId())
							.parameters());
		List<Contract> lst = new ArrayList<Contract>(allContractsCache.size());
		for (Contract c : allContractsCache)
			lst.add(c);
		return lst;
	}

	public List<Contract> getActualContracts() {
		List<Contract> list = getAllContracts();
		for (int i = list.size() - 1; i >= 0; i--)
			if (!list.get(i).isActive())
				list.remove(i);
		return list;
	}

	public List<Client> getAllClients() {
		return null;
		// return clientMed.filter(unit);
	}

	public List<Client> getActiveClients() {
		// own
		// Set<Client> clients = new HashSet<Client>(10);
		// for (Contract c : getActualContracts())
		// clients.add(c.getClient());
		// return new ArrayList<Client>(clients);
		List<Client> res = getClientMed().filterByActiveTeacher(unit)
				.getGroup();
		getClientMed().reset();
		return res;
	}

	public List<Client> getFrozenClients() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Client> getUndefinedClients() {
		// TODO Auto-generated method stub
		return null;
	}

	// contracts
	// frozen
	public List<Contract> getFrozenContracts() {
		return getContractMed().setGroup(getAllContracts())
				.filter(ContractState.frozen).getGroup();
	}

	// inactive
	public List<Contract> getInactiveContracts() {
		return getContractMed().setGroup(getAllContracts())
				.filter(ContractState.undefined).getGroup();
	}

	// canceled
	public List<Contract> getCanceledContracts() {
		return getContractMed().setGroup(getAllContracts())
				.filter(ContractState.canceled).getGroup();
	}

	// complete
	public List<Contract> getCompleteContracts() {
		return getContractMed().setGroup(getAllContracts())
				.filter(ContractState.complete).getGroup();
	}

	public String worksOn(String day) {
		Integer code = null;
		if (day.equals("Пн"))
			code = unit.getMonday();
		else if (day.equals("Вт"))
			code = unit.getTuesday();
		else if (day.equals("Ср"))
			code = unit.getWednesday();
		else if (day.equals("Чт"))
			code = unit.getThursday();
		else if (day.equals("Пт"))
			code = unit.getFriday();
		else if (day.equals("Сб"))
			code = unit.getSaturday();
		else if (day.equals("Вс"))
			code = unit.getSunday();

		return code == null ? "-" : dao.find(Facility.class, code).getName();
	}

	public String getName() {
		try {
			return unit.getName();
		} catch (NullPointerException e) {
			return null;
		}
	}

	public Integer[] getSchedule() {
		try {
			Integer[] sched = new Integer[7];
			sched[0] = unit.getMonday();
			sched[1] = unit.getTuesday();
			sched[2] = unit.getWednesday();
			sched[3] = unit.getThursday();
			sched[4] = unit.getFriday();
			sched[5] = unit.getSaturday();
			sched[6] = unit.getSunday();
			return sched;
		} catch (NullPointerException e) {
			return null;
		}
	}

	public String getSchoolForDay(String day) {
		try {
			Integer[] sched = getSchedule();
			day = day.toLowerCase();
			if (day.equals("пн"))
				return sched[0] == null ? null : getDao().find(Facility.class,
						sched[0]).getName();
			if (day.equals("вт"))
				return sched[1] == null ? null : getDao().find(Facility.class,
						sched[1]).getName();
			if (day.equals("ср"))
				return sched[2] == null ? null : getDao().find(Facility.class,
						sched[2]).getName();
			if (day.equals("чт"))
				return sched[3] == null ? null : getDao().find(Facility.class,
						sched[3]).getName();
			if (day.equals("пт"))
				return sched[4] == null ? null : getDao().find(Facility.class,
						sched[4]).getName();
			if (day.equals("сб"))
				return sched[5] == null ? null : getDao().find(Facility.class,
						sched[5]).getName();
			if (day.equals("вс"))
				return sched[6] == null ? null : getDao().find(Facility.class,
						sched[6]).getName();
			return null;
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}
	}

	public boolean getState() {
		try {
			return unit.isActive();
		} catch (NullPointerException npe) {
			return false;
		}
	}

	public int getLessonsComplete(Date date1, Date date2) {
		int res = getEventMed().filter(unit).filter(date1, date2)
				.countEventsComplete();
		getEventMed().reset();
		return res;
	}

	public int getLessonsFailed(Date date1, Date date2) {
		int res = getEventMed().filter(unit).filter(date1, date2)
				.countEventsFailed();
		getEventMed().reset();
		return res;
	}

	public int getLessonsFailedByTeacher(Date date1, Date date2) {
		int res = getEventMed().filter(unit).filter(date1, date2)
				.countEventsFailedByTeacher();
		getEventMed().reset();
		return res;
	}

	public int getLessonsFailedByClient(Date date1, Date date2) {
		int res = getEventMed().filter(unit).filter(date1, date2)
				.countEventsFailedByClient();
		getEventMed().reset();
		return res;
	}

	public int getDaysWorked(Date date1, Date date2) {
		int res = getEventMed().filter(unit).filter(date1, date2)
				.countDaysInEventsGroup();
		getEventMed().reset();
		return res;
	}

	public int getMoneyEarned(Date date1, Date date2) {
		int res = getEventMed().filter(unit).filter(date1, date2)
				.countMoneyOfCompleteEvents();
		getEventMed().reset();
		return res;
	}

	public int getMoneyEarnedForSchool(Date date1, Date date2) {
		int res = getEventMed().filter(unit).filter(date1, date2)
				.filter(EventState.complete).countSchoolMoney();
		getEventMed().reset();
		return res;
	}

	public int getMoneyEarnedForSelf(Date date1, Date date2) {
		int res = getEventMed().filter(unit).filter(date1, date2)
				.filter(EventState.complete).countTeacherMoney();
		getEventMed().reset();
		return res;
	}

	private List<Teacher> teachersCache;

	public Integer countGroupSize() {
		try {
			return teachersCache.size();
		} catch (NullPointerException npe) {
			return null;
		}
	}

}
