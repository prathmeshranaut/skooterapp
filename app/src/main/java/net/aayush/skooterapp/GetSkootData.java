package net.aayush.skooterapp;

import android.util.Log;

import net.aayush.skooterapp.data.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetSkootData extends GetRawData {

    private String LOG_TAG = GetSkootData.class.getSimpleName();
    private String mRawUrl;

    public List<Post> getPosts() {
        return mPosts;
    }

    protected List<Post> mPosts = new ArrayList<Post>();

    public GetSkootData(String mRawUrl) {
        super(mRawUrl);
        Log.v(LOG_TAG, mRawUrl);
        this.mRawUrl = mRawUrl;
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

        final String SKOOTS = "skoots";
        final String SKOOT_ID = "id";
        final String SKOOT_POST = "content";
        final String SKOOT_HANDLE = "handle";
        final String SKOOT_UPVOTES = "upvotes";
        final String SKOOT_DOWNVOTES = "downvotes";
        final String SKOOT_IF_USER_VOTED = "if_user_voted";
        final String SKOOT_USER_VOTE = "user_vote";
        final String SKOOT_USER_SCOOT = "user_skoot";
        final String SKOOT_CREATED_AT = "created_at";
        final String SKOOT_COMMENTS_COUNT = "comments_count";

        try {
            JSONObject jsonObject = new JSONObject(getmData());
            JSONArray jsonArray = jsonObject.getJSONArray(SKOOTS);

            Log.v(LOG_TAG, jsonArray.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonPost = jsonArray.getJSONObject(i);
                int id = jsonPost.getInt(SKOOT_ID);
                String post = jsonPost.getString(SKOOT_POST);
                String handle = jsonPost.getString(SKOOT_HANDLE);
                int upvotes = jsonPost.getInt(SKOOT_UPVOTES);
                int commentsCount = jsonPost.getInt(SKOOT_COMMENTS_COUNT);
                int downvotes = jsonPost.getInt(SKOOT_DOWNVOTES);
                boolean skoot_if_user_voted = jsonPost.getBoolean(SKOOT_IF_USER_VOTED);
                boolean user_vote = jsonPost.getBoolean(SKOOT_USER_VOTE);
                boolean user_skoot = jsonPost.getBoolean(SKOOT_USER_SCOOT);
                String created_at = jsonPost.getString(SKOOT_CREATED_AT);

                Post postObject = new Post(id, handle, post, commentsCount, upvotes, downvotes, skoot_if_user_voted, user_vote, user_skoot, created_at);
                this.mPosts.add(postObject);
            }
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