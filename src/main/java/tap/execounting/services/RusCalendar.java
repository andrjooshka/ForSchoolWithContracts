package tap.execounting.services;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;

public class RusCalendar implements SuperCalendar {

	private GregorianCalendar cal;
	private int year = Calendar.YEAR;
	private int day = Calendar.DAY_OF_YEAR;
	private int month = Calendar.MONTH;
	private int dom = Calendar.DAY_OF_MONTH;
	private int week = Calendar.WEEK_OF_YEAR;
	private int dow = Calendar.DAY_OF_WEEK;
	private int hour = Calendar.HOUR;
	private int minute = Calendar.MINUTE;

	private HashMap<Integer, String> daysOfWeek;
	private HashMap<Integer, String> monthes;
	private HashMap<Integer, String> monthesShorthands;
	private HashMap<Integer, String> daysOfWeekShorthands;

	public RusCalendar() {
		cal = new GregorianCalendar();
		cal.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
		cal.setFirstDayOfWeek(Calendar.MONDAY);

		// days of week init
		daysOfWeek = new HashMap<Integer, String>(7);
		daysOfWeek.put(Calendar.MONDAY, "Понедельник");
		daysOfWeek.put(Calendar.TUESDAY, "Вторник");
		daysOfWeek.put(Calendar.WEDNESDAY, "Среда");
		daysOfWeek.put(Calendar.THURSDAY, "Четверг");
		daysOfWeek.put(Calendar.FRIDAY, "Пятница");
		daysOfWeek.put(Calendar.SATURDAY, "Суббота");
		daysOfWeek.put(Calendar.SUNDAY, "Воскресенье");
		// days of week shorthands init
		daysOfWeekShorthands = new HashMap<Integer, String>(7);
		daysOfWeekShorthands.put(Calendar.MONDAY, "Пн");
		daysOfWeekShorthands.put(Calendar.TUESDAY, "Вт");
		daysOfWeekShorthands.put(Calendar.WEDNESDAY, "Ср");
		daysOfWeekShorthands.put(Calendar.THURSDAY, "Чт");
		daysOfWeekShorthands.put(Calendar.FRIDAY, "Пт");
		daysOfWeekShorthands.put(Calendar.SATURDAY, "Сб");
		daysOfWeekShorthands.put(Calendar.SUNDAY, "Вс");

		// monthes init
		monthes = new HashMap<Integer, String>(12);
		monthes.put(Calendar.JANUARY, "Январь");
		monthes.put(Calendar.FEBRUARY, "Февраль");
		monthes.put(Calendar.MARCH, "Март");
		monthes.put(Calendar.APRIL, "Апрель");
		monthes.put(Calendar.MAY, "Май");
		monthes.put(Calendar.JUNE, "Июнь");
		monthes.put(Calendar.JULY, "Июль");
		monthes.put(Calendar.AUGUST, "Август");
		monthes.put(Calendar.SEPTEMBER, "Сентябрь");
		monthes.put(Calendar.OCTOBER, "Октябрь");
		monthes.put(Calendar.NOVEMBER, "Ноябрь");
		monthes.put(Calendar.DECEMBER, "Декабрь");
		// monthes shorthands init
		monthesShorthands = new HashMap<Integer, String>(12);
		monthesShorthands.put(Calendar.JANUARY, "Янв");
		monthesShorthands.put(Calendar.FEBRUARY, "Фев");
		monthesShorthands.put(Calendar.MARCH, "Март");
		monthesShorthands.put(Calendar.APRIL, "Апр");
		monthesShorthands.put(Calendar.MAY, "Май");
		monthesShorthands.put(Calendar.JUNE, "Июнь");
		monthesShorthands.put(Calendar.JULY, "Июль");
		monthesShorthands.put(Calendar.AUGUST, "Авг");
		monthesShorthands.put(Calendar.SEPTEMBER, "Сен");
		monthesShorthands.put(Calendar.OCTOBER, "Окт");
		monthesShorthands.put(Calendar.NOVEMBER, "Ноя");
		monthesShorthands.put(Calendar.DECEMBER, "Дек");

	}

	// public RusCalendar(Date d){
	// this();
	// cal.setTime(d);
	// }

	public Date getTime() {
		return cal.getTime();
	}

	public SuperCalendar setTime(Date time) {
		cal.setTime(time);
		return this;
	}

	public TimeZone getTimeZone() {
		return cal.getTimeZone();
	}

	public void setTimeZone(TimeZone timeZone) {
		cal.setTimeZone(timeZone);
	}

	public int getYear() {
		return cal.get(year);
	}

	public void setYear(int value) {
		cal.set(year, value);
	}

	public int getDay() {
		return cal.get(day);
	}

	public void setDay(int value) {
		cal.set(day, value);
	}

	public int getMonth() {
		return cal.get(month);
	}

	public void setMonth(int value) {
		cal.set(month, value);
	}

	public int getDayOfMonth() {
		return cal.get(dom);
	}

	public int getWeek() {
		return cal.get(week);
	}

	public int getDayOfWeek() {
		return cal.get(dow);
	}

	public int getHour() {
		return cal.get(hour);
	}

	public void setHour(int value) {
		cal.set(hour, value);
	}

	public int getMinute() {
		return cal.get(minute);
	}

	public void setMinute(int value) {
		cal.set(minute, value);
	}

	public int rangeInDays(SuperCalendar sc) {
		// TODO we can't compare times that more than year differ
		if (before(sc)) {
			if (sc.getYear() > getYear()) {
				int daysInYear = leapYear() ? 366 : 365;
				return sc.getDay() + daysInYear - getDay();
			}
			return sc.getDay() - getDay();
		}
		if (sc.getYear() < getYear()) {
			int daysInYear = sc.leapYear() ? 366 : 365;
			return -sc.getDay() + daysInYear + getDay();
		}
		return sc.getDay() - getDay();
	}

	public int rangeInDays(Date date) {
		RusCalendar rc = new RusCalendar();
		rc.setTime(date);
		return rangeInDays(rc);
	}

	public String getDayOfWeekName() {
		return daysOfWeek.get(getDayOfWeek());
	}

	public String getDayOfWeekName(int dayOfWeek) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getMonthName() {
		return monthes.get(getMonth());
	}

	public String getMonthName(int month) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setMonthNames(String[] names) {
		if (names.length != 12)
			throw new IllegalArgumentException(
					"Please provide names for 12 monthes");
		monthes = new HashMap<Integer, String>(12);
		for (int i = 0; i < 12; i++) {
			monthes.put(i, names[i]);
		}
	}

	public String[] getMonthNames() {
		return monthes.values().toArray(new String[12]);
	}

	public void setDayOfWeekNames(String[] names) {
		if (names.length != 7)
			throw new IllegalArgumentException(
					"Please provide names for all 7 days");
		daysOfWeek = new HashMap<Integer, String>(7);
		for (int i = 0; i < 7; i++) {
			daysOfWeek.put(i, names[i]);
		}
	}

	public String[] getDayOfWeekNames() {
		return daysOfWeek.values().toArray(new String[7]);
	}

	public void addDays(int days) {
		cal.add(day, days);
	}

	public void addHours(int hours) {
		cal.add(hour, hours);
	}

	public void addMinutes(int minutes) {
		cal.add(minute, minutes);
	}

	public void incrementDay() {
		cal.add(day, 1);
	}

	public void decrementDay() {
		cal.add(day, -1);
	}

	public void maxHoursMinutes() {
		setHour(23);
		setMinute(59);
	}

	public void minHoursMinutes() {
		setHour(0);
		setMinute(0);
	}

	public boolean before(SuperCalendar sc) {
		return cal.before(sc.getTime());
	}

	public boolean after(SuperCalendar sc) {
		return cal.after(sc.getTime());
	}

	public boolean equals(SuperCalendar sc) {
		return cal.equals(sc.getTime());
	}

	public boolean leapYear() {
		return cal.isLeapYear(getYear());
	}

	public RusCalendar clone() {
		RusCalendar rc = new RusCalendar();
		rc.setTime(getTime());
		return rc;
	}

	public String toString() {
		return getTime().toString();
	}

	public String dateString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getDayOfWeekName() + " ");
		sb.append(getDayOfMonth() + " ");
		sb.append(getMonthName() + " ");
		sb.append(getYear() + " ");
		return sb.toString();
	}

	public String stringByTuple(String ... tuple) {
		StringBuilder res = new StringBuilder();
		for(int i=0;i<tuple.length;i++){
			res.append(map(tuple[i]));
			if(i<tuple.length-1) res.append(" ");
		}
		return res.toString();
	}
	
	private String map(String element){
		if(element=="month")
			return getMonthName();
		else if(element == "dayOfWeek")
			return getDayOfWeekName();
		else if(element == "dayOfMonth")
			return getDayOfMonth() + "";
		else if(element == "year")
			return getYear() + "";
		else if(element == "day")
			return getDayOfMonth() + ""; 
		else return "";
	}
}
