package net.aayush.skooterapp.data;

public class Group {
    private int mGroupId;
    private String mGroupName;
    private String mFollowed;

    public Group(int groupId, String groupName, String followed) {
        mGroupId = groupId;
        mGroupName = groupName;
        mFollowed = followed;
    }

    @Override
    public String toString() {
        return mGroupName + " \n" + mFollowed;
    }

    public int getGroupId() {
        return mGroupId;
    }

    public String getGroupName() {
        return mGroupName;
    }

    public String getFollowed() {
        return mFollowed;
    }
}
