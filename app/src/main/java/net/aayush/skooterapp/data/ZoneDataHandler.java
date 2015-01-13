package net.aayush.skooterapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class ZoneDataHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "zonesDb.db";
    private static final String TABLE_ZONES = "zones";

    private static final String COLUMN_ZONE_ID = "id";
    private static final String COLUMN_ZONE_NAME = "name";
    private static final String COLUMN_ZONE_IS_FOLLOWING = "is_following";

    public ZoneDataHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ZONE_TABLE = "CREATE TABLE " +
                TABLE_ZONES + "("
                + COLUMN_ZONE_ID + " INTEGER PRIMARY KEY," + COLUMN_ZONE_NAME
                + " TEXT," + COLUMN_ZONE_IS_FOLLOWING + " INTEGER" + ")";
        db.execSQL(CREATE_ZONE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TALBE IF EXISTS " + TABLE_ZONES);
        onCreate(db);
    }

    public void addZone(Zone zone) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ZONE_NAME, zone.getZoneName());
        values.put(COLUMN_ZONE_IS_FOLLOWING, zone.getIsFollowing());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_ZONES, null, values);
        db.close();
    }

    public ArrayList<Zone> getAllZones() {
        ArrayList<Zone> zones = new ArrayList<Zone>();
        SQLiteDatabase db = this.getWritableDatabase();

        Zone zone = new Zone();

        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_ZONES + " ORDER BY " + COLUMN_ZONE_IS_FOLLOWING + " DESC", null);

        while(c.moveToNext()) {
            zone.setZoneId(c.getInt(0));
            zone.setZoneName(c.getString(1));
            zone.setIsFollowing(Boolean.parseBoolean(c.getString(2)));
            zones.add(zone);
        }
        c.close();
        db.close();
        return zones;
    }
}