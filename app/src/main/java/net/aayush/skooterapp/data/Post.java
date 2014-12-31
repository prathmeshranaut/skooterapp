package net.aayush.skooterapp.data;

import java.io.Serializable;

public class Post implements Serializable{

    private static final long serialVersionUID = 1L;

    private int mId;
    private String mUser;
    private String mHandle;
    private String mContent;
    private int mUpvotes;
    private int mDownvotes;
    private String mTimestamp;

    public Post(int id, String user, String handle, String content, int upvotes, int downvotes, String timestamp) {
        mId = id;
        mUser = user;
        mHandle = handle;
        mContent = content;
        mUpvotes = upvotes;
        mDownvotes = downvotes;
        mTimestamp = timestamp;
    }

    @Override
    public String toString() {
        return mContent + "\n" +
                (mUpvotes - mDownvotes) + "\n" +
                mTimestamp;
    }
}
