package pt.isec.gps.g22.sleeper.dal;

import pt.isec.gps.g22.sleeper.core.DayRecord;
import pt.isec.gps.g22.sleeper.core.Profile;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "sleeper.db";

    public static final String CREATE_PROFILE_TABLE = "CREATE TABLE " + Profile.TABLE_NAME + "(" +
            Profile.COLUMN_ID + " INTEGER PRIMARY KEY," +
            Profile.COLUMN_GENDER + " INTEGER NOT NULL, " +
            Profile.COLUMN_DATEOFBIRTH + " INTEGER NOT NULL, " +
            Profile.COLUMN_FIRSTHOUROFTHEDAY + " INTEGER NOT NULL" +
            ")";

    public static final String CREATE_DAYRECORD_TABLE = "CREATE TABLE " + DayRecord.TABLE_NAME + "(" +
            DayRecord.COLUMN_ID + " INTEGER PRIMARY KEY," +
            DayRecord.COLUMN_SLEEPDATE + " INTEGER NOT NULL CHECK("+DayRecord.COLUMN_SLEEPDATE+">0), " +
            DayRecord.COLUMN_EXHAUSTION + " INTEGER, " +
            DayRecord.COLUMN_WAKEUPDATE + " INTEGER NOT NULL CHECK("+DayRecord.COLUMN_WAKEUPDATE+">0), " +
            DayRecord.COLUMN_SLEEPQUALITY + " INTEGER" +
            ")";

    public DBHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROFILE_TABLE);
        db.execSQL(CREATE_DAYRECORD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Profile.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DayRecord.TABLE_NAME);
        onCreate(db);
    }
}
