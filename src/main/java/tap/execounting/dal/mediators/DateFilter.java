package tap.execounting.dal.mediators;

import java.util.Date;
import java.util.List;

import tap.execounting.entities.interfaces.Dated;

public interface DateFilter {
	public List<Dated> filter(List<Dated> items, Date date1, Date date2);
}
