package pt.isec.gps.g22.sleeper.core.time;

import static org.junit.Assert.*;

import org.junit.Test;

import android.test.InstrumentationTestCase;

public class TimeOfDayTest {

	@Test
	public void testHoursMinutesSecondsBuilder() {
		final TimeOfDay tod = TimeOfDay.at(12, 23, 34);
		
		assertEquals(12, tod.getHours());
		assertEquals(23, tod.getMinutes());
		assertEquals(34, tod.getSeconds());
	}

}
