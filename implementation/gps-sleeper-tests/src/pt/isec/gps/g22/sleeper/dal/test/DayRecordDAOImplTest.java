package pt.isec.gps.g22.sleeper.dal.test;

import pt.isec.gps.g22.sleeper.core.DayRecord;
import pt.isec.gps.g22.sleeper.dal.DayRecordDAOImpl;
import android.test.InstrumentationTestCase;
import android.test.RenamingDelegatingContext;

public class DayRecordDAOImplTest extends InstrumentationTestCase {
    private DayRecordDAOImpl dayRecordDAOImpl;

    public void setUp() throws Exception {
        super.setUp();
        RenamingDelegatingContext context = new RenamingDelegatingContext(getInstrumentation().getTargetContext(), "test_");
        dayRecordDAOImpl = new DayRecordDAOImpl(context);
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testInsertInvalidDayRecord() throws Exception {
        DayRecord dayRecord = new DayRecord();
        assertEquals("Not empty DayRecord",-1,dayRecordDAOImpl.insertRecord(dayRecord));
    }

    public void testInsertValidDayRecord() throws Exception {
        DayRecord dayRecord = new DayRecord(1417395600,1417420800);
        assertEquals("Empty DayRecord",1,dayRecordDAOImpl.insertRecord(dayRecord));
    }

    public void testLoadDayRecord() throws Exception {
        DayRecord newDayRecord = new DayRecord(1417395600,1417420800);
        int id = dayRecordDAOImpl.insertRecord(newDayRecord);

        DayRecord loadedDayRecord = dayRecordDAOImpl.loadDayRecord(id);

        assertEquals("Profile Not Loaded", loadedDayRecord.getSleepDate(), newDayRecord.getSleepDate());
    }

    public void testUpdateDayRecord() throws Exception {
        DayRecord dayRecord = new DayRecord(1417395600,1417420800);
        int id = dayRecordDAOImpl.insertRecord(dayRecord);

        DayRecord existentDayRecord = dayRecordDAOImpl.loadDayRecord(id);
        existentDayRecord.setWakeupDate(1417420900);
        dayRecordDAOImpl.updateRecord(existentDayRecord);

        DayRecord updatedDayRecord = dayRecordDAOImpl.loadDayRecord(id);
        assertEquals("Not Updated", updatedDayRecord.getWakeupDate(), existentDayRecord.getWakeupDate());
    }

    public void testDeleteDayRecord() throws Exception {
        DayRecord dayRecord = new DayRecord(1417395600,1417420800);
        int id = dayRecordDAOImpl.insertRecord(dayRecord);

        DayRecord existentDayRecord = dayRecordDAOImpl.loadDayRecord(id);

        assertEquals("Not Deleted", 1, dayRecordDAOImpl.deleteRecord(existentDayRecord));
    }

    public void testGetAllRecords() throws Exception {
        DayRecord dayRecordOne = new DayRecord(1417395400,1417395500);
        DayRecord dayRecordTwo = new DayRecord(1417395600,1417395700);
        DayRecord dayRecordThree = new DayRecord(1417395800,1417395900);

        dayRecordDAOImpl.insertRecord(dayRecordOne);
        dayRecordDAOImpl.insertRecord(dayRecordTwo);
        dayRecordDAOImpl.insertRecord(dayRecordThree);

        assertEquals("Return invalid list", 3, dayRecordDAOImpl.getAllRecords().size());
    }
}
