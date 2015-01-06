package net.aayush.skooterapp;

import android.util.Log;

import net.aayush.skooterapp.data.Comment;
import net.aayush.skooterapp.data.Post;
import net.aayush.skooterapp.data.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetUserDetails extends GetRawData {

    private String LOG_TAG = GetSkootData.class.getSimpleName();
    private String mRawUrl;
    private User mUser;
    private int mUserId;

    public User getUser() {
        return mUser;
    }

    public GetUserDetails(String mRawUrl, int mUserId) {
        super(mRawUrl);
        Log.v(LOG_TAG, mRawUrl);
        this.mRawUrl = mRawUrl;
        this.mUserId = mUserId;
    }

    public void execute() {
        DownloadJsonData downloadJsonData = new DownloadJsonData();
        //downloadJsonData.execute(mRawUrl);
    }

    public void processResult() {
        if (getmDownloadStatus() != DownloadStatus.OK) {
            Log.e(LOG_TAG, "Error downloading raw data");
            return;
        }

        final String SKOOT_SCORE = "score";
        final String SKOOT_POST = "skoots";
        final String SKOOT_COMMENT = "comments";
        final String SKOOT_ID = "id";
        final String SKOOT_HANDLE = "handle";
        final String SKOOT_CONTENT = "content";
        final String SKOOT_UPVOTES = "upvotes";
        final String SKOOT_DOWNVOTES = "downvotes";
        final String SKOOT_CREATED_AT = "created_at";
        final String SKOOT_COMMENTS_COUNT = "comments_count";
        final String SKOOT_POST_ID = "post_id";

        try {
            JSONObject jsonObject = new JSONObject(getmData());
            int score = jsonObject.getInt(SKOOT_SCORE);
            JSONArray jsonPosts = jsonObject.getJSONArray(SKOOT_POST);
            JSONArray jsonComments = jsonObject.getJSONArray(SKOOT_COMMENT);

            List<Post> posts = new ArrayList<Post>();
            for (int i = 0; i < jsonPosts.length(); i++) {
                JSONObject jsonPost = jsonPosts.getJSONObject(i);
                int id = jsonPost.getInt(SKOOT_ID);
                String post = jsonPost.getString(SKOOT_CONTENT);
                String handle = jsonPost.getString(SKOOT_HANDLE);
                int upvotes = jsonPost.getInt(SKOOT_UPVOTES);
                int downvotes = jsonPost.getInt(SKOOT_DOWNVOTES);
                int commentsCount = jsonPost.getInt(SKOOT_COMMENTS_COUNT);
                String created_at = jsonPost.getString(SKOOT_CREATED_AT);

                Post postObject = new Post(id, handle, post, commentsCount, upvotes, downvotes, false, false, true, created_at);
                posts.add(postObject);
            }

            List<Comment> comments = new ArrayList<Comment>();
            for (int i = 0; i < jsonComments.length(); i++) {
                JSONObject jsonComment = jsonComments.getJSONObject(i);
                int id = jsonComment.getInt(SKOOT_ID);
                String post = jsonComment.getString(SKOOT_CONTENT);
                String handle = jsonComment.getString(SKOOT_HANDLE);
                int upvotes = jsonComment.getInt(SKOOT_UPVOTES);
                int downvotes = jsonComment.getInt(SKOOT_DOWNVOTES);
                int postId = jsonComment.getInt(SKOOT_POST_ID);
                String created_at = jsonComment.getString(SKOOT_CREATED_AT);

                Comment commentObject = new Comment(id, postId, handle, post, upvotes, downvotes, false, false, true, created_at);
                comments.add(commentObject);
            }

            mUser = new User(mUserId, score, posts, comments);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Error processing Json Data");
        }

    }

    public class DownloadJsonData extends DownloadRawData {
        @Override
        protected void onPostExecute(String webData) {
            super.onPostExecute(webData);
            processResult();
        }

        @Override
        protected String doInBackground(String... params) {
            String[] par = {mRawUrl};
            return super.doInBackground(par);
        }
    }
}
