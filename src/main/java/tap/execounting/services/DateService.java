package tap.execounting.services;

import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import tap.execounting.entities.interfaces.Dated;

import java.text.SimpleDateFormat;
import java.util.*;

public class DateService {

    private TimeZone timeZone;

    private HashMap<String, String> dayOfWeekNames;

    private static int HOUR = Calendar.HOUR;
    private static int MINUTE = Calendar.MINUTE;
    private static int YEAR = Calendar.YEAR;
    private static int MONTH = Calendar.MONTH;
    private static int DAY_OF_YEAR = Calendar.DAY_OF_YEAR;
    //private static int DAY_OF_MONTH = Calendar.DAY_OF_MONTH;
    private static int MILLISECOND = Calendar.MILLISECOND;

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
        Calendar calendar = getMoscowCalendar();
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime();
    }

    public static Date trimToDate(Date date) {
        Calendar c = getMoscowCalendar();
        c.setTime(date);
        Calendar r = getMoscowCalendar();
        r.setTimeInMillis(-10800000);
        r.set(YEAR, c.get(YEAR));
        r.set(DAY_OF_YEAR, c.get(DAY_OF_YEAR));
        // r.set(YEAR, c.get(YEAR));
        return r.getTime();
    }

    public static Date datePlusDays(Date date, int days) {
        Calendar calendar = getMoscowCalendar();
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
        return new RusCalendar().setTime(eventsDate).getMonthName();
    }

    public static Date datePlusMonths(Date d, int months) {
        Calendar c = getMoscowCalendar(d);
        c.add(MONTH, months);
        return c.getTime();
    }

    public static Date trimToMonth(Date date) {
        Calendar c = getMoscowCalendar(date);
        Calendar r = getMoscowCalendar();
        r.setTimeInMillis(-10800000);

        r.set(YEAR, c.get(YEAR));
        r.set(MONTH, c.get(MONTH));
        return r.getTime();
    }

    public static Date maxOutDayTime(Date date) {
        Calendar c = getMoscowCalendar(trimToDate(date));
        c.add(DAY_OF_YEAR, 1);
        c.add(MILLISECOND, -1);
        return c.getTime();
    }

    public static TimeZone getMoscowTimeZone() {
        return java.util.TimeZone.getTimeZone("Europe/Moscow");
    }

    public static Calendar getMoscowCalendar() {
        Calendar c = new GregorianCalendar(getMoscowTimeZone());
        return c;
    }

    public static Calendar getMoscowCalendar(Date time) {
        Calendar c = new GregorianCalendar(getMoscowTimeZone());
        c.setTime(time);
        return c;
    }

    /**
     * @param d from which to calculate this day of week
     * @return integer for day of week from 1 to 7
     */
    public static int dayOfWeekRus(Date d) {
        // TODO check
        Calendar date = new GregorianCalendar();
        date.setTime(d);
        int dow = date.get(Calendar.DAY_OF_WEEK);
        dow = dow == 1 ? 7 : dow - 1;
        return dow;
    }

    public static String toString(String format, Date date) {
        SimpleDateFormat f = new SimpleDateFormat(format, new Locale("ru", "RU"));
        return f.format(date);
    }

    public static String fullRepresentation(Date date) {
        return toString("H:m dd MM yyyy", date);
    }

    public static String fullRepresentation(Calendar cal) {
        return toString("H:m dd MM yyyy", cal.getTime());
    }

    public static String formatDayMonthNameYear(Date date) {
        return toString("d M y", date);
    }

    public static void sort(List<? extends Dated> list, boolean descending) {
        Collections.sort(list, new DatedComparator());
        if (descending)
            Collections.reverse(list);
    }

    public static Comparator DateComparator = new DateComparator();
}

class DatedComparator implements Comparator<Dated> {

    public int compare(Dated first, Dated second) {
        return first.getDate().compareTo(second.getDate());
    }
}

class DateComparator implements Comparator<Date> {
    public int compare(Date one, Date two) {
        return one.compareTo(two);
    }
}
