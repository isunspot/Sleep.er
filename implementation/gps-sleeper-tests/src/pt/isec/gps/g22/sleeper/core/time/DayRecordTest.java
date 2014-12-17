package pt.isec.gps.g22.sleeper.core.time;

import android.test.AndroidTestCase;

import java.util.ArrayList;

import pt.isec.gps.g22.sleeper.core.DayRecord;

public class DayRecordTest extends AndroidTestCase {

    private DayRecord dayRecordToBeTested,dayRecordIntersectedAtLeft,dayRecordIntersectedAtRight,majorDayRecord,minorDayRecord;
    private ArrayList<DayRecord> tempList;

    public void setUp() throws Exception {
    	super.setUp();
        tempList = new ArrayList<DayRecord>();
        dayRecordToBeTested = new DayRecord(1418824800,1418839200); //12/17/2014 @ 2:00pm / @ 6:00pm
        dayRecordIntersectedAtLeft = new DayRecord(1418821200,1418828400); //12/17/2014 @ 1:00pm / @ 3:00pm
        dayRecordIntersectedAtRight = new DayRecord(1418835600,1418842800); //12/17/2014 @ 17:00pm / @ 7:00pm
        minorDayRecord = new DayRecord(1418832000,1418832600); //12/17/2014 @ 4:00pm / @ 4:10pm
        majorDayRecord = new DayRecord(1418821200,1418842800); //12/17/2014 @ 1:00pm / @ 7:00pm
    }
    
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void recordOverlapsedAtLeft() throws Exception {
        tempList.add(dayRecordIntersectedAtLeft);
        assertEquals("The new record is intersecting another record at left",false,dayRecordToBeTested.recordOverlap(tempList));
    }

    public void recordOverlapsedAtRight() throws Exception {
        tempList.add(dayRecordIntersectedAtRight);
        assertEquals("The new record is intersecting another record at right",true,dayRecordToBeTested.recordOverlap(tempList));
    }

    public void recordOverlaps() throws Exception {
        tempList.add(minorDayRecord);
        assertEquals("The new record overlaps another record",true,dayRecordToBeTested.recordOverlap(tempList));
    }

    public void recordOverlapsed() throws Exception {
        tempList.add(majorDayRecord);
        assertEquals("The new record is ovelapsed by another record",true,dayRecordToBeTested.recordOverlap(tempList));
    }
	
}
