package pt.isec.gps.g22.sleeper.dal;

import java.util.ArrayList;
import java.util.List;

import pt.isec.gps.g22.sleeper.core.DayRecord;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DayRecordDAOImpl implements DayRecordDAO {
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public DayRecordDAOImpl(Context context) {
        dbHelper = new DBHelper(context);
    }

    @Override
    public List<DayRecord> getAllRecords() {
        db = dbHelper.getReadableDatabase();
        List<DayRecord> dayRecordList = null;
        String query = "SELECT " + "*" + "FROM " + DayRecord.TABLE_NAME + ";";

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.getCount() > 0) {
            dayRecordList = new ArrayList<DayRecord>();
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                dayRecordList.add(getDayRecord(cursor));
                cursor.moveToNext();
            }
        }
        db.close();
        return dayRecordList;
    }

    @Override
    public DayRecord loadDayRecord(int id) {
        db = dbHelper.getReadableDatabase();
        DayRecord dayRecord = null;
        String query = "SELECT " + "*" + "FROM " + DayRecord.TABLE_NAME + " WHERE " + DayRecord.COLUMN_ID + " = '" + id + "';";

        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            dayRecord = getDayRecord(cursor);
        }
        db.close();
        return dayRecord;
    }

    private DayRecord getDayRecord(Cursor cursor) {
        DayRecord dayRecord = new DayRecord();

        dayRecord.setId(cursor.getInt(0));
        dayRecord.setSleepDate(cursor.getInt(1));
        dayRecord.setExhaustion(cursor.getInt(2));
        dayRecord.setWakeupDate(cursor.getInt(3));
        dayRecord.setSleepQuality(cursor.getInt(4));
        return dayRecord;
    }

    @Override
    public int insertRecord(DayRecord dayRecord) {
        db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DayRecord.COLUMN_SLEEPDATE,dayRecord.getSleepDate());
        values.put(DayRecord.COLUMN_EXHAUSTION,dayRecord.getExhaustion());
        values.put(DayRecord.COLUMN_WAKEUPDATE,dayRecord.getWakeupDate());
        values.put(DayRecord.COLUMN_SLEEPQUALITY,dayRecord.getSleepQuality());

        int rowId = (int)db.insert(DayRecord.TABLE_NAME,null,values);
        db.close();
        return rowId;
    }

    @Override
    public int updateRecord(DayRecord dayRecord) {
        db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DayRecord.COLUMN_SLEEPDATE,dayRecord.getSleepDate());
        values.put(DayRecord.COLUMN_EXHAUSTION,dayRecord.getExhaustion());
        values.put(DayRecord.COLUMN_WAKEUPDATE,dayRecord.getWakeupDate());
        values.put(DayRecord.COLUMN_SLEEPQUALITY,dayRecord.getSleepQuality());

        int affectedRows = db.update(DayRecord.TABLE_NAME, values, DayRecord.COLUMN_ID + "=?", new String[]{String.valueOf(dayRecord.getId())});
        db.close();
        return affectedRows;
    }

    @Override
    public int deleteRecord(DayRecord dayRecord) {
        db = dbHelper.getWritableDatabase();

        int affectedRows = db.delete(DayRecord.TABLE_NAME, DayRecord.COLUMN_ID + "=?",new String[]{String.valueOf(dayRecord.getId())});
        db.close();
        return affectedRows;
    }

	@Override
	public List<DayRecord> getWeekRecords(final long weekStart, final long weekFinal) {
		db = dbHelper.getWritableDatabase();
		
		List<DayRecord> dayRecordList = null;
        String query = "SELECT " + "*" + "FROM " + DayRecord.TABLE_NAME; 
        		query += " WHERE " + DayRecord.COLUMN_WAKEUPDATE + " >= '" + weekStart + " ' AND ";
        		query += " " + DayRecord.COLUMN_SLEEPDATE + "<= '" + weekFinal + "+;";

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.getCount() > 0) {
            dayRecordList = new ArrayList<DayRecord>();
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                dayRecordList.add(getDayRecord(cursor));
                cursor.moveToNext();
            }
        }
        db.close();
        
		return dayRecordList;
	}
}