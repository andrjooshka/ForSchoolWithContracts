package tap.execounting.dal.mediators.interfaces;

import java.util.List;

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
	
//group methods
	public List<Teacher> getAllTeachers();
	
	//count:
	public Integer countGroupSize();
}
