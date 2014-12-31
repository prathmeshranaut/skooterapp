package net.aayush.skooterapp.data;

import java.io.Serializable;

public class Comment implements Serializable {
    private static final long serialVersionUID = 1L;

    private String mContent;
    private int mUpvotes;
    private int mDownvotes;
    private String mTimestamp;

    public Comment(String content, int upvotes, int downvotes, String timestamp) {
        mContent = content;
        mUpvotes = upvotes;
        mDownvotes = downvotes;
        mTimestamp = timestamp;
    }

    public String getContent() {
        return mContent;
    }

    public int getUpvotes() {
        return mUpvotes;
    }

    public int getDownvotes() {
        return mDownvotes;
    }

    public String getTimestamp() {
        return mTimestamp;
    }

    @Override
    public String toString() {
        return mContent + "\n" +
                (mUpvotes - mDownvotes) + "\n" +
                mTimestamp;
    }
}
