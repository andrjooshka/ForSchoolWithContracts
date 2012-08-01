package tap.execounting.dal.mediators;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tap.execounting.entities.interfaces.Dated;

public class DateFilterImpl implements DateFilter {

	public List<? extends Dated> filter(List<? extends Dated> items, Date date1, Date date2) {
		List<Dated> filtered = new ArrayList<Dated>(500);
		if (date1 != null && date2 != null) {
			for(Dated item : items){
				if(item.getDate().after(date1) && item.getDate().before(date2))
					filtered.add(item);
			}
		} else if (date1 != null) {
			for(Dated item : items){
				if(item.getDate().after(date1))
					filtered.add(item);
			}
		} else if (date2 != null) {
			for(Dated item : items){
				if(item.getDate().before(date2))
					filtered.add(item);
			}
		} else {
			for (Dated item : items)
				filtered.add(item);
		}
		return filtered;
	}

}
