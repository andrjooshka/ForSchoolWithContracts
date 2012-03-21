package tap.execounting.services;

import java.util.Date;
import java.util.TimeZone;

public interface SuperCalendar {
	
	Date getTime();
	SuperCalendar setTime(Date time);
	
	TimeZone getTimeZone();
	void setTimeZone(TimeZone timeZone);

	int getYear();
	void setYear(int year);

	int getDay();
	void setDay(int day);

	int getMonth();
	void setMonth(int month);

	int getDayOfMonth();

	int getWeek();

	int getDayOfWeek();
	
	int getHour();
	void setHour(int value);
	
	int getMinute();
	void setMinute(int minute);
	
	int rangeInDays(SuperCalendar sc);
	int rangeInDays(Date date);
	
	void maxHoursMinutes();
	void minHoursMinutes();
	
	String getDayOfWeekName();
	String getDayOfWeekName(int dayOfWeek);
	
	String getMonthName();
	String getMonthName(int month);
	
	void setMonthNames(String[] names);
	String[] getMonthNames();
	
	void setDayOfWeekNames(String[] names);
	String[] getDayOfWeekNames();
	
	void addDays(int days);
	void addHours(int hours);
	void addMinutes(int minutes);
	void incrementDay();
	void decrementDay();
	
	boolean before(SuperCalendar sc);
	boolean after(SuperCalendar sc);
	boolean equals(SuperCalendar sc);
	boolean leapYear();

	SuperCalendar clone();
	String dateString();
	String dateString(boolean shorthand);
	String stringByTuple(String ... tuple);
	String toString();
}
