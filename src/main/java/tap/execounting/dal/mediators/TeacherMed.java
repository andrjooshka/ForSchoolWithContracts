package tap.execounting.dal.mediators;

import java.util.List;

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
	
	//state
	public boolean getState();
	
//group methods
	public List<Teacher> getAllTeachers();
	
	//count:
	public Integer countGroupSize();
}
