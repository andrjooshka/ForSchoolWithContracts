package tap.execounting.dal.mediators.interfaces;

import java.util.Date;
import java.util.List;

import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.entities.Client;
import tap.execounting.entities.Teacher;

public interface TeacherMed {
//unit methods
	//getters:
	
	//unit
	public Teacher getUnit();
	public void setUnit(Teacher unit);
	
	//name
	public String getName();
	
	//schedule
	public Integer[] getSchedule();
	public String getSchoolForDay(String day);
	
	//state
	public boolean getState();
	
	//clients
		//all
	public List<Client> getAllClients();
		//active
	public List<Client> getActiveClients();
		//frozen
	public List<Client> getFrozenClients();
		//undefined
	public List<Client> getUndefinedClients();
	//lessons
		//complete
	public int getLessonsComplete(Date date1, Date date2);
		//failed
	public int getLessonsFailed(Date date1, Date date2);
		//failed by teacher
	public int getLessonsFailedByTeacher(Date date1, Date date2);
		//failed by client
	public int getLessonsFailedByClient(Date date1, Date date2);
	//days worked
	public int getDaysWorked(Date date1, Date date2);
	//money earned
	public int getMoneyEarned(Date date1, Date date2);
		//for school
	public int getMoneyEarnedForSchool(Date date1, Date date2);
		//for self
	public int getMoneyEarnedForSelf(Date date1, Date date2);
	
//group methods
	public List<Teacher> getAllTeachers();
	
	//count:
	public Integer countGroupSize();
	
	
	//service methods which we should avoid to use
	public void setDao(CrudServiceDAO dao);
	
}
