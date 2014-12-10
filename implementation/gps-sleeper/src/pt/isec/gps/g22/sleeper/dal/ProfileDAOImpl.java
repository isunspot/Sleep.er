package pt.isec.gps.g22.sleeper.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import pt.isec.gps.g22.sleeper.core.Profile;

public class ProfileDAOImpl implements ProfileDAO {
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public ProfileDAOImpl(Context context) {
        dbHelper = new DBHelper(context);
    }

    @Override
    public int insertProfile(Profile profile) {
        db = dbHelper.getWritableDatabase();
        int rowId;

        ContentValues values = new ContentValues();
        values.put(Profile.COLUMN_GENDER,profile.getGender());
        values.put(Profile.COLUMN_DATEOFBIRTH,profile.getDateOfBirth());
        values.put(Profile.COLUMN_FIRSTHOUROFTHEDAY,profile.getFirstHourOfTheDay());

        rowId = (int)db.insert(Profile.TABLE_NAME,null,values);
        profile.setId(rowId);
        db.close();
        return rowId;
    }

    @Override
    public int updateProfile(Profile profile) {
        db = dbHelper.getWritableDatabase();
        int affectedRows;
        ContentValues values = new ContentValues();
        values.put(Profile.COLUMN_GENDER,profile.getGender());
        values.put(Profile.COLUMN_DATEOFBIRTH,profile.getDateOfBirth());
        values.put(Profile.COLUMN_FIRSTHOUROFTHEDAY,profile.getFirstHourOfTheDay());

        affectedRows = db.update(Profile.TABLE_NAME, values, Profile.COLUMN_ID + "=?", new String[]{String.valueOf(profile.getId())});
        db.close();
        return affectedRows;
    }

    @Override
    public Profile loadProfile() {
        db = dbHelper.getReadableDatabase();
        Profile profile = null;
        String query = "SELECT" + " * " + "FROM " + Profile.TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            profile = getProfile(cursor);
        }
        db.close();
        return profile;
    }

    private Profile getProfile(Cursor cursor) {
        Profile profile = new Profile();

        profile.setId(cursor.getInt(0));
        profile.setGender(cursor.getInt(1));
        profile.setDateOfBirth(cursor.getLong(2));
        profile.setFirstHourOfTheDay(cursor.getInt(3));
        return profile;
    }
}
