package tap.execounting.services;

import java.util.Date;
import java.util.TimeZone;

public interface SuperCalendar {
	
	Date getTime();
	SuperCalendar setTime(Date time);
	
	int getYear();

	int getDay();

	int getMonth();

	int getDayOfMonth();


	int getDayOfWeek();
	
	void setHour(int value);
	
	void setMinute(int minute);
	
	int rangeInDays(SuperCalendar sc);
	int rangeInDays(Date date);
	
	void maxHoursMinutes();
	void minHoursMinutes();
	
	String getDayOfWeekName();

	String getMonthName();

	void addDays(int days);
	void incrementDay();

	boolean before(SuperCalendar sc);
	boolean after(SuperCalendar sc);
	boolean equals(SuperCalendar sc);
	boolean leapYear();

	SuperCalendar clone();
	String dateString();
	String toString();
}
