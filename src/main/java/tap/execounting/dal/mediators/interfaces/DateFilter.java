package tap.execounting.dal.mediators.interfaces;

import java.util.Date;
import java.util.List;

import tap.execounting.entities.interfaces.Dated;

public interface DateFilter {
	@SuppressWarnings("rawtypes")
	public List<Dated> filter(List<Dated> items, Date date1, Date date2);
}
