package tap.execounting.dal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.entities.Client;
import tap.execounting.entities.Contract;
import tap.execounting.entities.Facility;
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
	
	public List<Contract> getAllContracts(){
		return dao.findWithNamedQuery(Contract.WITH_TEACHER,
				QueryParameters.with("teacherId", unit.getId()).parameters());
	}
	
	public List<Contract> getActualContracts(){
		List<Contract> list= getAllContracts();
		for(int i=list.size()-1;i>=0;i--)
			if(!list.get(i).isActive())
				list.remove(i);
		return list;
	}
	
	public List<Client> getAllClients(){
		Set<Client> clients = new HashSet<Client>(10);
		for(Contract c : getAllContracts())
			clients.add(c.getClient());
		return new ArrayList<Client>(clients);
	}
	
	public List<Client> getActiveClients(){
		Set<Client> clients = new HashSet<Client>(10);
		for(Contract c : getActualContracts())
			clients.add(c.getClient());
		return new ArrayList<Client>(clients);
	}
	
	public void setUnit(Teacher unit){
		this.unit = unit;
	}
	
	public String worksOn(String day){
		Integer code = null;
		if(day.equals("Пн"))
			code=unit.getMonday();
		else
		if(day.equals("Вт"))
			code=unit.getTuesday();
		else
		if(day.equals("Ср"))
			code=unit.getWednesday();
		else
		if(day.equals("Чт"))
			code=unit.getThursday();
		else
		if(day.equals("Пт"))
			code=unit.getFriday();
		else
		if(day.equals("Сб"))
			code=unit.getSaturday();
		else
		if(day.equals("Вс"))
			code=unit.getSunday();
		
		return code==null ? "-" : dao.find(Facility.class, code).getName();
	}

}
