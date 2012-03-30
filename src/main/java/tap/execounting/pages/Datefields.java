package tap.execounting.pages;

import java.util.Date;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.annotations.AnonymousAccess;
import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.entities.Contract;

@AnonymousAccess
public class Datefields {

	@Property
	private Date date;

	@Inject
	private CrudServiceDAO dao;

	public Contract getCon() {
		return dao.find(Contract.class, 3);
	}
}
