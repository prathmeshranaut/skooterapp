package net.aayush.skooterapp;

import android.util.Log;

import net.aayush.skooterapp.data.Comment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetSkootComments extends GetRawData {

    private String LOG_TAG = GetSkootComments.class.getSimpleName();
    private String mRawUrl;

    protected List<Comment> mComments = new ArrayList<Comment>();

    public List<Comment> getComments() {
        return mComments;
    }

    public GetSkootComments(String mRawUrl) {
        super(mRawUrl);
        this.mRawUrl = mRawUrl;
    }

    public void execute() {
        DownloadJsonData downloadJsonData = new DownloadJsonData();
    }

    public void processResult() {
        if (getmDownloadStatus() != DownloadStatus.OK) {
            Log.e(LOG_TAG, "Error downloading raw data");
            return;
        }

        final String SKOOTS = "skoot";
        final String SKOOT_ID = "id";
        final String SKOOT_POST = "content";
        final String SKOOT_COMMENTS = "comments";
        final String SKOOT_HANDLE = "handle";
        final String SKOOT_CREATED_AT = "created_at";
        final String SKOOT_UPVOTES = "upvotes";
        final String SKOOT_DOWNVOTES = "downvotes";
        final String SKOOT_IF_USER_VOTED = "if_user_voted";
        final String SKOOT_USER_VOTE = "user_vote";
        final String SKOOT_USER_COMMENT = "user_comment";

        try {
            JSONObject jsonObject = new JSONObject(getmData());
            JSONArray jsonArray = jsonObject.getJSONArray(SKOOT_COMMENTS);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonComment = jsonArray.getJSONObject(i);
                int id = jsonComment.getInt(SKOOT_ID);
                String comment = jsonComment.getString(SKOOT_POST);
                String handle = jsonComment.getString(SKOOT_HANDLE);
                int upvotes = jsonComment.getInt(SKOOT_UPVOTES);
                int downvotes = jsonComment.getInt(SKOOT_DOWNVOTES);
                boolean if_user_voted = jsonComment.getBoolean(SKOOT_IF_USER_VOTED);
                boolean user_vote = false;
                if( if_user_voted) {
                    user_vote = jsonComment.getBoolean(SKOOT_USER_VOTE);
                }
                boolean user_skoot = jsonComment.getBoolean(SKOOT_USER_COMMENT);
                String timestamp = jsonComment.getString(SKOOT_CREATED_AT);

                Comment commentObject = new Comment(id, comment, handle, upvotes, downvotes, if_user_voted, user_vote, user_skoot, timestamp);
                this.mComments.add(commentObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Error processing JSON data");
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