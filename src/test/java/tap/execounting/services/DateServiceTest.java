package tap.execounting.services;

import static org.junit.Assert.*;

import java.util.GregorianCalendar;

import org.junit.Test;

@SuppressWarnings("unused")
public class DateServiceTest {

	@Test
	public void testDayIncrement() {
		GregorianCalendar c1 = new GregorianCalendar(2012,12,31);
		GregorianCalendar c2 = new GregorianCalendar(2013,1,1);
		c1.add(GregorianCalendar.DAY_OF_WEEK, 1);
		assert c1.equals(c2);
	}

}
