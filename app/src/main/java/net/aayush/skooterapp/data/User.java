package net.aayush.skooterapp.data;

import java.util.List;

public class User {
    private int mUserId;
    private int mScore;
    private List<Post> mSkoots;
    private List<Comment> mComments;

    public User(int userId, int score, List<Post> skoots, List<Comment> comments) {
        mUserId = userId;
        mScore = score;
        mSkoots = skoots;
        mComments = comments;
    }

    public int getUserId() {
        return mUserId;
    }

    public int getScore() {
        return mScore;
    }

    public List<Post> getSkoots() {
        return mSkoots;
    }

    public List<Comment> getComments() {
        return mComments;
    }

    @Override
    public String toString() {
        return "User{" +
                "mUserId=" + mUserId +
                ", mScore=" + mScore +
                ", mSkoots=" + mSkoots +
                ", mComments=" + mComments +
                '}';
    }
}
