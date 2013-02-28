package tap.execounting.dal.mediators.interfaces;

import java.util.Date;
import java.util.List;

import tap.execounting.entities.interfaces.Dated;

public interface DateFilter {
	public List<? extends Dated> reatinByDatesEntryWithReturn(List<? extends Dated> items,
                                                              Date date1, Date date2);

	public void retainByDatesEntry(List<? extends Dated> items, Date date1, Date date2);
}
