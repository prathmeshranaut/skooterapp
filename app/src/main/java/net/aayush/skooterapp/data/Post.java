package net.aayush.skooterapp.data;

import java.io.Serializable;

public class Post implements Serializable {

    private static final long serialVersionUID = 1L;

    private int mId;
    private String mHandle;

    public int getId() {
        return mId;
    }

    public String getHandle() {
        return mHandle;
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

    public boolean isIfUserVoted() {
        return mIfUserVoted;
    }

    public boolean isUserVote() {
        return mUserVote;
    }

    public boolean isUserSkoot() {
        return mUserSkoot;
    }

    public String getTimestamp() {
        return mTimestamp;
    }

    private String mContent;
    private int mUpvotes;
    private int mDownvotes;
    private boolean mIfUserVoted;
    private boolean mUserVote;
    private boolean mUserSkoot;

    private String mTimestamp;

    public Post(int id, String handle, String content, int upvotes, int downvotes, boolean ifUserVoted, boolean userVote, boolean userSkoot, String timestamp) {
        mId = id;
        mHandle = handle;
        mContent = content;
        mUpvotes = upvotes;
        mDownvotes = downvotes;
        mIfUserVoted = ifUserVoted;
        mUserVote = userVote;
        mUserSkoot = userSkoot;
        mTimestamp = timestamp;
    }

    @Override
    public String toString() {
        return mContent + "\n" +
                (mUpvotes - mDownvotes) + "\n" +
                mTimestamp;
    }

    public void upvotePost() {
        mUserVote = true;
        mIfUserVoted = true;
    }

    public void downvotePost() {
        mUserVote = false;
        mIfUserVoted = true;
    }
}
