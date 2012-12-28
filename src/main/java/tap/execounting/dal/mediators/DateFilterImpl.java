package tap.execounting.dal.mediators;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tap.execounting.dal.mediators.interfaces.DateFilter;
import tap.execounting.entities.interfaces.Dated;

public class DateFilterImpl implements DateFilter {

	public List<? extends Dated> filterWithReturn(List<? extends Dated> items, Date date1, Date date2) {
		List<Dated> filtered = new ArrayList<Dated>(500);
		if (date1 != null && date2 != null) {
			for(Dated item : items){
				if(!item.getDate().before(date1) && !item.getDate().after(date2))
					filtered.add(item);
			}
		} else if (date1 != null) {
			for(Dated item : items){
				if(!item.getDate().before(date1))
					filtered.add(item);
			}
		} else if (date2 != null) {
			for(Dated item : items){
				if(!item.getDate().after(date2))
					filtered.add(item);
			}
		} else {
			for (Dated item : items)
				filtered.add(item);
		}
		return filtered;
	}

	/**
	 * remove all items that do not fit in that date borders
	 */
	public void filter(List<? extends Dated> items, Date date1, Date date2) {
		Dated item;
		if (date1 != null && date2 != null) {
			for(int i=items.size()-1;i>=0;i--){
				item = items.get(i);
				// FIXME
				if(item.getDate().after(date2) || item.getDate().before(date1))
					items.remove(i);
			}
		} else if (date1 != null) {
			for(int i=items.size()-1;i>=0;i--){
				item = items.get(i);
				if(item.getDate().before(date1))
					items.remove(i);
			}
		} else if (date2 != null) {
			for(int i=items.size()-1;i>=0;i--){
				item = items.get(i);
				if(!item.getDate().before(date2))
					items.remove(i);
			}
		}
	}

}
