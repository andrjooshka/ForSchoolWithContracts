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
		// months shorthands init
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

	public Date getTime() {
		return cal.getTime();
	}

	public SuperCalendar setTime(Date time) {
		cal.setTime(time);
		return this;
	}

	public int getYear() {
		return cal.get(year);
	}

	public int getDay() {
		return cal.get(day);
	}

	public int getMonth() {
		return cal.get(month);
	}

	public int getDayOfMonth() {
		return cal.get(dom);
	}

	public int getDayOfWeek() {
		return cal.get(dow);
	}

	public void setHour(int value) {
		cal.set(hour, value);
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

	public String getMonthName() {
		return monthes.get(getMonth());
	}

	public void addDays(int days) {
		cal.add(day, days);
	}

	public void incrementDay() {
		cal.add(day, 1);
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

}
