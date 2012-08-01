package tap.execounting.dal.mediators.interfaces;

import java.util.Date;
import java.util.List;

import tap.execounting.entities.interfaces.Dated;

public interface DateFilter {
	public List<? extends Dated> filterWithReturn(List<? extends Dated> items,
			Date date1, Date date2);

	public void filter(List<? extends Dated> items, Date date1, Date date2);
}
