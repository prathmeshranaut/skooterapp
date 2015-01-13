package net.aayush.skooterapp.data;

public class Zone {
    private int mZoneId;
    private String mZoneName;
    private boolean mIsFollowing;

    public Zone() {

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

    public void setZoneName(String zoneName) {
        mZoneName = zoneName;
    }

    public boolean getIsFollowing() {
        return mIsFollowing;
    }

    public void setIsFollowing(boolean isFollowing) {
        mIsFollowing = isFollowing;
    }
}
