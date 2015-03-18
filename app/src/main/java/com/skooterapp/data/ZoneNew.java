package com.skooterapp.data;

/**
 * Created by aayushranaut on 3/16/15.
 * com.skooterapp.data
 */
public class ZoneNew {
    protected int mId;
    protected String mName;
    protected boolean mActiveZone;
    protected String mZoneImage;
    protected String mZoneBackground;

    public String getZoneBackground() {
        return mZoneBackground;
    }

    public void setZoneBackground(String zoneBackground) {
        mZoneBackground = zoneBackground;
    }

    public String getZoneImage() {
        return mZoneImage;
    }

    public void setZoneImage(String zoneImage) {
        mZoneImage = zoneImage;
    }

    public boolean isActiveZone() {
        return mActiveZone;
    }

    public void setActiveZone(boolean activeZone) {
        mActiveZone = activeZone;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public ZoneNew(int id, String name, boolean activeZone, String zoneImage, String zoneBackground) {
        mId = id;
        mName = name;
        mActiveZone = activeZone;
        mZoneImage = zoneImage;
        mZoneBackground = zoneBackground;
    }

    @Override
    public String toString() {
        return "ZoneNew{" +
                "mId=" + mId +
                ", mName='" + mName + '\'' +
                ", mActiveZone=" + mActiveZone +
                ", mZoneImage='" + mZoneImage + '\'' +
                ", mZoneBackground='" + mZoneBackground + '\'' +
                '}';
    }
}
