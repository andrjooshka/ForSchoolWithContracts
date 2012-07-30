package tap.execounting.dal.mediators.interfaces;

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
}
