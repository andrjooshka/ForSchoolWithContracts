package tap.execounting.dal;

import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.entities.Contract;
import tap.execounting.entities.Teacher;

public class TeacherMediator {
	
	@Inject
	private CrudServiceDAO dao;
	
	public Teacher unit;
	
	public List<Teacher> getAll(){
		return dao.findWithNamedQuery(Teacher.ALL);
	}
	
	public Teacher getUnit(){
		return unit;
	}
	
	public List<Contract> getContracts(){
		return dao.findWithNamedQuery(Contract.WITH_TEACHER,
				QueryParameters.with("teacherId", unit.getId()).parameters());
	}
	
	public L
	
	public void setUnit(Teacher unit){
		this.unit = unit;
	}

}
