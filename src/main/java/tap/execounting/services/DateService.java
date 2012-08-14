package tap.execounting.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class DateService {

	private TimeZone timeZone;

	private HashMap<String, String> dayOfWeekNames;

	private static int HOUR = Calendar.HOUR;
	private static int MINUTE = Calendar.MINUTE;
	private static int YEAR = Calendar.YEAR;
	private static int MONTH = Calendar.MONTH;
	private static int DAY_OF_YEAR = Calendar.DAY_OF_YEAR;
	//private static int DAY_OF_MONTH = Calendar.DAY_OF_MONTH;

	public DateService() {
		timeZone = TimeZone.getTimeZone("Europe/Moscow");
		dayOfWeekNames = new HashMap<String, String>(7);
		dayOfWeekNames.put("Monday", "Понедельник");
		dayOfWeekNames.put("Tuesday", "Вторник");
		dayOfWeekNames.put("Wednesday", "Среда");
		dayOfWeekNames.put("Thursday", "Четверг");
		dayOfWeekNames.put("Friday", "Пятница");
		dayOfWeekNames.put("Saturday", "Суббота");
		dayOfWeekNames.put("Sunday", "Воскресенье");
	}

	public DateService(TimeZone timeZone) {
		this.setTimeZone(timeZone);
	}

	public Calendar IncrementDay(Calendar c) {
		int day = getDayOfYear(c);
		if (day != 365 && day != 366)
			setDayOfYear(c, day + 1);
		else if (day == 366) {
			setYear(c, getYear(c) + 1);
			setDayOfYear(c, 1);
		} else if (getYear(c) % 4 == 0)
			setDayOfYear(c, 366);
		else {
			setYear(c, getYear(c) + 1);
			setDayOfYear(c, 1);
		}

		return c;
	}

	public Date IncrementDay(Date d) {
		return IncrementDay(getCalendar(d)).getTime();
	}

	public void setYear(Calendar c, int year) {
		c.set(YEAR, year);
	}

	public void setDayOfYear(Calendar c, int day) {
		c.set(DAY_OF_YEAR, day);
	}

	public Calendar getCalendar(Date date) {
		Calendar c = getZonedCalendar();
		c.setTime(date);
		return c;
	}

	public Calendar getCalendar() {
		return getZonedCalendar();
	}

	private Calendar getZonedCalendar() {
		Calendar c = Calendar.getInstance(timeZone);
		return c;
	}

	public Calendar nullHoursAndMinutes(Calendar c) {
		Calendar copy = getCalendar(new Date(c.getTimeInMillis()));
		assert copy.getTimeInMillis() == c.getTimeInMillis();
		setHour(copy, 0);
		setMinute(copy, 0);
		copy.set(Calendar.MILLISECOND, 0);
		copy.set(Calendar.SECOND, 0);
		return copy;
	}

	public Date nullHoursAndMinutes(Date time) {
		Calendar c = getCalendar(time);
		c = nullHoursAndMinutes(c);
		return new Date(c.getTimeInMillis());
	}

	public Calendar maxHoursAndMinutes(Calendar c) {
		Calendar res = getCalendar(c.getTime());
		assert (res.getTimeInMillis() == c.getTimeInMillis());

		setHour(res, 23);
		setMinute(res, 59);
		return res;
	}

	public Date maxHourAndMinutes(Date d) {
		Calendar c = getCalendar(d);
		c = maxHoursAndMinutes(c);
		return c.getTime();
	}

	public int getMinute(Calendar c) {
		return c.get(MINUTE);
	}

	public void setMinute(Calendar c, int value) {
		c.set(MINUTE, value);
	}

	public void setHour(Calendar c, int value) {
		c.set(HOUR, value);
	}

	public boolean compareDays(Calendar c1, Calendar c2) {
		int day1 = getDayOfYear(c1);
		int day2 = getDayOfYear(c2);
		int year1 = getYear(c1);
		int year2 = getYear(c2);

		return year1 == year2 && day1 == day2;
	}

	public boolean compareDays(Date d1, Date d2) {
		Calendar c1 = getCalendar(d1), c2 = getCalendar(d2);
		return compareDays(c1, c2);
	}

	public boolean compareDays(Calendar c, Date d) {
		return compareDays(c, getCalendar(d));
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	public int getYear(Calendar c) {
		return c.get(YEAR);
	}

	public int getYear(Date d) {
		return getYear(getCalendar(d));
	}

	public int getDayOfYear(Calendar c) {
		return c.get(DAY_OF_YEAR);
	}

	public int getDayOfYear(Date d) {
		return getDayOfYear(getCalendar(d));
	}

	public int getMonth(Calendar c) {
		return c.get(MONTH);
	}

	public int getMonth(Date d) {
		return getMonth(getCalendar(d));
	}

	public String getDayOfWeek(Calendar c) {
		return dayOfWeekNames.get(c.getDisplayName(Calendar.DAY_OF_WEEK,
				Calendar.LONG, Locale.ENGLISH));
	}

	public String getDayOfWeek(Date d) {
		return getDayOfWeek(getCalendar(d));
	}

	public int getHour(Calendar c) {
		return c.get(Calendar.HOUR);
	}

	public int getHour(Date d) {
		return getHour(getCalendar(d));
	}

	public void setDayOfWeekNames(String[] names) {
		if (names.length != 7)
			throw new IllegalArgumentException(
					"Length of names array must equals 7");
		dayOfWeekNames = new HashMap<String, String>(7);
		dayOfWeekNames.put("Monday", names[0]);
		dayOfWeekNames.put("Tuesday", names[1]);
		dayOfWeekNames.put("Wednesday", names[2]);
		dayOfWeekNames.put("Thursday", names[3]);
		dayOfWeekNames.put("Friday", names[4]);
		dayOfWeekNames.put("Saturday", names[5]);
		dayOfWeekNames.put("Sunday", names[6]);
	}

	public int dayRange(Date d1, Date d2) {
		return dayRange(getCalendar(d1), getCalendar(d2));
	}

	public int dayRange(Calendar c1, Calendar c2) {
		if (c2.before(c1))
			throw new IllegalArgumentException(
					"Date 1 must be before than date 2");
		if (getYear(c1) == getYear(c2)) {
			return getDayOfYear(c2) - getDayOfYear(c1);
		}
		if (getYear(c1) % 4 == 0) {
			return 366 - getDayOfYear(c1) + getDayOfYear(c2);
		}
		return 365 - getDayOfYear(c1) + getDayOfYear(c2);
	}

	public int getDayOfMonth(Calendar c) {
		return c.get(Calendar.DAY_OF_MONTH);
	}

	public int getDayOfMonth(Date d) {
		return getDayOfMonth(getCalendar(d));
	}

	public void setDayOfYear(Date date, int day) {
		Calendar c = getCalendar();
		setDayOfYear(c, day);
		date.setTime(c.getTimeInMillis());
	}

	public static Date fromNowPlusDays(int days) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(Calendar.DAY_OF_YEAR, days);
		return calendar.getTime();
	}

	public static Date trimToDate(Date date) {
		Calendar c = new GregorianCalendar();
		c.setTime(date);
		Calendar r = new GregorianCalendar();
		r.setTime(new Date(100));
		r.set(YEAR, c.get(YEAR));
		r.set(DAY_OF_YEAR, c.get(DAY_OF_YEAR));
		// r.set(YEAR, c.get(YEAR));
		return r.getTime();
	}

	public static Date datePlusDays(Date date, int days) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_YEAR, days);
		return calendar.getTime();
	}

	public static List<Date> generateDaySet(Date eventsDate, int days) {
		List<Date> list = new ArrayList<Date>(days);
		for (int i = 0; i < days; i++)
			list.add(datePlusDays(eventsDate, i));
		return list;
	}

	public static String monthName(Date eventsDate) {
		return  new RusCalendar().setTime(eventsDate).getMonthName();
	}

	public static Date datePlusMonths(Date d, int months) {
		Calendar c = new GregorianCalendar();
		c.setTime(d);
		c.add(MONTH, months);
		return c.getTime();
	}

	public static Date trimToMonth(Date date) {
		Calendar c = new GregorianCalendar();
		c.setTime(date);
		Calendar r = new GregorianCalendar();
		r.setTimeInMillis(100);
		
		r.set(YEAR, c.get(YEAR));
		r.set(MONTH, c.get(MONTH));
		return r.getTime();
	}
}



