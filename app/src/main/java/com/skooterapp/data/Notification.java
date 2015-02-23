package com.skooterapp.data;

public class Notification {
    private int mId;
    private int mPostId;
    private String mText;
    private String mIconUrl;
    private boolean mPostRedirect;
    private Post mPost;

    public Notification(int id, int postId, String text, String iconUrl, boolean postRedirect) {
        mId = id;
        mPostId = postId;
        mText = text;
        mIconUrl = iconUrl;
        mPostRedirect = postRedirect;
    }

    public Notification(int id, int postId, String text, String iconUrl, boolean postRedirect, Post post) {
        mId = id;
        mPostId = postId;
        mText = text;
        mIconUrl = iconUrl;
        mPostRedirect = postRedirect;
        mPost = post;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getPostId() {
        return mPostId;
    }

    public void setPostId(int postId) {
        mPostId = postId;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public String getIconUrl() {
        return mIconUrl;
    }

    public void setIconUrl(String iconUrl) {
        mIconUrl = iconUrl;
    }

    public boolean isPostRedirect() {
        return mPostRedirect;
    }

    public void setPostRedirect(boolean postRedirect) {
        mPostRedirect = postRedirect;
    }

    public Post getPost() {
        return mPost;
    }

    public void setPost(Post post) {
        mPost = post;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "mId=" + mId +
                ", mPostId=" + mPostId +
                ", mText='" + mText + '\'' +
                ", mIconUrl='" + mIconUrl + '\'' +
                ", mPostRedirect=" + mPostRedirect +
                '}';
    }
}