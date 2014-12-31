package net.aayush.skooterapp.data;

import java.util.ArrayList;
import java.util.List;

public class GroupData {
    private List<Group> mGroups = new ArrayList<Group>();

    public List<Group> getGroups() {
        return mGroups;
    }

    public GroupData() {
        addItem(new Group(1, "IIT Delhi", "Unfollow"));
        addItem(new Group(2, "NSIT", "Follow"));
        addItem(new Group(3, "DTU", "Follow"));
    }

    private void addItem(Group item) {
        mGroups.add(item);
    }
}
