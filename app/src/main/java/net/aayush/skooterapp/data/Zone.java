package net.aayush.skooterapp.data;

public class Zone {
    private int mZoneId;
    private String mZoneName;
    private float mLatitudeMinimum;
    private float mLongitudeMinimum;
    private float mLatitudeMaximum;
    private float mLongitudeMaximum;
    private boolean mIsFollowing;

    public Zone(int zoneId, String zoneName, float latitudeMinimum, float longitudeMinimum, float latitudeMaximum, float longitudeMaximum, boolean isFollowing) {
        mZoneId = zoneId;
        mZoneName = zoneName;
        mLatitudeMinimum = latitudeMinimum;
        mLongitudeMinimum = longitudeMinimum;
        mLatitudeMaximum = latitudeMaximum;
        mLongitudeMaximum = longitudeMaximum;
        mIsFollowing = isFollowing;
    }

    public Zone() {
        mZoneId = 0;
        mZoneName = "";
    }

    public Zone(String zoneName, boolean isFollowing) {
        mZoneName = zoneName;
        mIsFollowing = isFollowing;
    }

    public int getZoneId() {
        return mZoneId;
    }

    public void setZoneId(int zoneId) {
        mZoneId = zoneId;
    }

    public String getZoneName() {
        return mZoneName;
    }

    public float getLatitudeMinimum() {
        return mLatitudeMinimum;
    }

    public void setLatitudeMinimum(float latitudeMinimum) {
        mLatitudeMinimum = latitudeMinimum;
    }

    public float getLongitudeMinimum() {
        return mLongitudeMinimum;
    }

    public void setLongitudeMinimum(float longitudeMinimum) {
        mLongitudeMinimum = longitudeMinimum;
    }

    public float getLatitudeMaximum() {
        return mLatitudeMaximum;
    }

    public void setLatitudeMaximum(float latitudeMaximum) {
        mLatitudeMaximum = latitudeMaximum;
    }

    public float getLongitudeMaximum() {
        return mLongitudeMaximum;
    }

    public void setLongitudeMaximum(float longitudeMaximum) {
        mLongitudeMaximum = longitudeMaximum;
    }

    public void setZoneName(String zoneName) {
        mZoneName = zoneName;
    }

    public boolean getIsFollowing() {
        return mIsFollowing;
    }

    public void setIsFollowing(boolean isFollowing) {
        mIsFollowing = isFollowing;
    }

    @Override
    public String toString() {
        return mZoneName;
    }
}
