package com.skooterapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class ZoneDataHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Zones.db";
    private static final String TABLE_ZONES = "zones";

    private static final String COLUMN_ZONE_ID = "id";
    private static final String COLUMN_ZONE_NAME = "name";
    private static final String COLUMN_LATITUDE_MINIMUM = "latitude_minimum";
    private static final String COLUMN_LATITUDE_MAXIMUM = "latitude_maximum";
    private static final String COLUMN_LONGITUDE_MINIMUM = "longitude_minimum";
    private static final String COLUMN_LONGITUDE_MAXIMUM = "longitude_maximum";
    private static final String COLUMN_ZONE_IS_FOLLOWING = "is_following";
    private static final String COLUMN_ZONE_IMAGE = "zone_image";

    public ZoneDataHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ZONE_TABLE = "CREATE TABLE " +
                TABLE_ZONES + "("
                + COLUMN_ZONE_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_ZONE_NAME + " TEXT,"
                + COLUMN_LATITUDE_MINIMUM + " FLOAT,"
                + COLUMN_LATITUDE_MAXIMUM + " FLOAT,"
                + COLUMN_LONGITUDE_MINIMUM + " FLOAT,"
                + COLUMN_LONGITUDE_MAXIMUM + " FLOAT,"
                + COLUMN_ZONE_IS_FOLLOWING + " INTEGER,"
                + COLUMN_ZONE_IMAGE + " TEXT" + ")";
        db.execSQL(CREATE_ZONE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TALBE IF EXISTS " + TABLE_ZONES);
        onCreate(db);
    }

    public void addZone(Zone zone) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ZONE_ID, zone.getZoneId());
        values.put(COLUMN_ZONE_NAME, zone.getZoneName());
        values.put(COLUMN_LATITUDE_MINIMUM, zone.getLatitudeMinimum());
        values.put(COLUMN_LATITUDE_MAXIMUM, zone.getLatitudeMaximum());
        values.put(COLUMN_LONGITUDE_MINIMUM, zone.getLongitudeMinimum());
        values.put(COLUMN_LONGITUDE_MAXIMUM, zone.getLongitudeMaximum());
        values.put(COLUMN_ZONE_IS_FOLLOWING, zone.getIsFollowing());
        values.put(COLUMN_ZONE_IMAGE, zone.getZoneImage());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_ZONES, null, values);
        db.close();
    }

    public ArrayList<Zone> getAllZones() {
        ArrayList<Zone> zones = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_ZONES + " ORDER BY " + COLUMN_ZONE_IS_FOLLOWING + " DESC", null);

        while (c.moveToNext()) {
            Zone zone = new Zone();
            zone.setZoneId(c.getInt(0));
            zone.setZoneName(c.getString(1));
            zone.setLatitudeMinimum(c.getFloat(2));
            zone.setLatitudeMaximum(c.getFloat(3));
            zone.setLongitudeMinimum(c.getFloat(4));
            zone.setLongitudeMaximum(c.getFloat(5));
            zone.setIsFollowing("1".equals(c.getString(6)));
            zone.setZoneImage(c.getString(7));
            zones.add(zone);
        }
        c.close();
        db.close();
        return zones;
    }

    public Zone getActiveZone(double currentLatitude, double currentLongitude) {
        SQLiteDatabase db = this.getWritableDatabase();


        String[] arguments = {
                Double.toString(currentLatitude), Double.toString(currentLatitude),
                Double.toString(currentLongitude), Double.toString(currentLongitude),
        };

        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_ZONES + " WHERE " +
                        COLUMN_LATITUDE_MINIMUM + " < ? AND " +
                        COLUMN_LATITUDE_MAXIMUM + " > ? AND " +
                        COLUMN_LONGITUDE_MAXIMUM + " > ? AND " +
                        COLUMN_LONGITUDE_MINIMUM + " < ?",
                arguments);

        if (c.moveToFirst()) {
            Zone zone = new Zone();
            zone.setZoneId(c.getInt(0));
            zone.setZoneName(c.getString(1));
            zone.setLatitudeMinimum(c.getFloat(2));
            zone.setLatitudeMaximum(c.getFloat(3));
            zone.setLongitudeMinimum(c.getFloat(4));
            zone.setLongitudeMaximum(c.getFloat(5));
            zone.setIsFollowing("1".equals(c.getString(6)));
            zone.setZoneImage(c.getString(7));
            return zone;
        }
        c.close();
        db.close();
        return new Zone();
    }

    public void updateZone(Zone zone) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_ZONE_NAME, zone.getZoneName());
        values.put(COLUMN_LATITUDE_MINIMUM, zone.getLatitudeMinimum());
        values.put(COLUMN_LATITUDE_MAXIMUM, zone.getLatitudeMaximum());
        values.put(COLUMN_LONGITUDE_MINIMUM, zone.getLongitudeMinimum());
        values.put(COLUMN_LONGITUDE_MAXIMUM, zone.getLongitudeMaximum());
        values.put(COLUMN_ZONE_IS_FOLLOWING, zone.getIsFollowing());
        values.put(COLUMN_ZONE_IMAGE, zone.getZoneImage());

        db.update(TABLE_ZONES, values, COLUMN_ZONE_ID + " = '" + Integer.toString(zone.getZoneId()) + "'", null);
        db.close();
    }

    public void followZoneById(int i) {
        ContentValues updatedValues = new ContentValues();
        updatedValues.put(COLUMN_ZONE_IS_FOLLOWING, true);

        String where = COLUMN_ZONE_ID + " = ?";
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_ZONES, updatedValues, where, new String[]{Integer.toString(i)});
        db.close();
    }

    public void unFollowZoneById(int i) {
        ContentValues updatedValues = new ContentValues();
        updatedValues.put(COLUMN_ZONE_IS_FOLLOWING, false);

        String where = COLUMN_ZONE_ID + " = ?";
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_ZONES, updatedValues, where, new String[]{Integer.toString(i)});
        db.close();
    }
}