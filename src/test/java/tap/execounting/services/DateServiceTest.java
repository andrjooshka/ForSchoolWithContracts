//package tap.execounting.services;
//
//import static org.junit.Assert.*;
//import static tap.execounting.services.DateService.*;
//import static java.lang.System.out;
//
//import java.util.Calendar;
//import java.util.Date;
//import java.util.GregorianCalendar;
//import java.util.TimeZone;
//
//import org.junit.Test;
//
//@SuppressWarnings("unused")
//public class DateServiceTest {
//
//	@Test
//	public void testDayIncrement() {
//		GregorianCalendar c1 = new GregorianCalendar(2012, 12, 31);
//		GregorianCalendar c2 = new GregorianCalendar(2013, 1, 1);
//		c1.add(GregorianCalendar.DAY_OF_WEEK, 1);
//		assert c1.equals(c2);
//	}
//	@Test
//	public void testMaxOutDateTime(){
//		// SETUP
//		Calendar c = getMoscowCalendar();
//		c.set(Calendar.DAY_OF_MONTH, 1);
//		Date test = c.getTime();
//		
//		// ACTION
//		test = maxOutDayTime(test);
//		c.setTime(test);
//		// ASSERT
//		assert(c.get(Calendar.HOUR)==23);
//		assert(c.get(Calendar.MINUTE)==59);
//		assert(c.get(Calendar.SECOND)==59);
//	}
//
//	@Test
//	public void testDateTrimming() {
//		Calendar cal = getMoscowCalendar();
//		Date d = trimToDate(new Date());
//		cal.setTime(d);
//		out.println("Moscow");
//		out.println(cal);
//		out.println(fullRepresentation(cal));
//		out.println(fullRepresentation(cal.getTime()));
//
//		out.println("\nLondon");
//		cal.setTimeZone(TimeZone.getTimeZone("Europe/London"));
//		out.println(cal);
//		out.println(fullRepresentation(cal));
//		out.println(fullRepresentation(cal.getTime()));
//
//		out.println("\n\nMaxTime");
//		d = maxOutDayTime(d);
//		cal.setTime(d);
//		cal.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
//		out.println("Moscow");
//		out.println(cal);
//		out.println(fullRepresentation(cal));
//		out.println(fullRepresentation(cal.getTime()));
//
//		out.println("\nLondon");
//		cal.setTimeZone(TimeZone.getTimeZone("Europe/London"));
//		out.println(cal);
//		out.println(fullRepresentation(cal));
//		out.println(fullRepresentation(cal.getTime()));
//	}
//
//}
