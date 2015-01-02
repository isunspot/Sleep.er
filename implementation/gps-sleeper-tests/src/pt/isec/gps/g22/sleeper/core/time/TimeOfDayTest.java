package pt.isec.gps.g22.sleeper.core.time;

import android.test.AndroidTestCase;

public class TimeOfDayTest extends AndroidTestCase {

    public void setUp() throws Exception {
    	super.setUp();
    }
    
    public void tearDown() throws Exception {
        super.tearDown();
    }
	
	public void testHoursMinutesSecondsBuilder() {
		final TimeOfDay tod = TimeOfDay.at(12, 23, 34);
		
		assertEquals(12, tod.getHours());
		assertEquals(23, tod.getMinutes());
		assertEquals(34, tod.getSeconds());
	}

}
