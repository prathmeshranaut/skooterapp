package net.aayush.skooterapp.data;

import java.io.Serializable;

public class Post implements Serializable{

    private static final long serialVersionUID = 1L;

    private String mContent;
    private int mUpvotes;
    private int mDownvotes;
    private String mTimestamp;

    public Post(String content, int upvotes, int downvotes, String timestamp) {
        mContent = content;
        this.mUpvotes = upvotes;
        this.mDownvotes = downvotes;
        this.mTimestamp = timestamp;
    }

    @Override
    public String toString() {
        return mContent + "\n" +
                (mUpvotes - mDownvotes) + "\n" +
                mTimestamp;
    }
}
