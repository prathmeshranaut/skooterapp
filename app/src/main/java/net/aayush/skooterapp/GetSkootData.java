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

        final String SKOOT_ID = "id";
        final String SKOOT_USER = "user";
        final String SKOOT_POST = "post";
        final String SKOOT_HANDLE = "handle";
        final String SKOOT_CREATED_AT = "created_at";

        try {
            JSONArray jsonArray = new JSONArray(getmData());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonPost = jsonArray.getJSONObject(i);
                int id = jsonPost.getInt(SKOOT_ID);
                String user = jsonPost.getString(SKOOT_USER);
                String post = jsonPost.getString(SKOOT_POST);
                String handle = jsonPost.getString(SKOOT_HANDLE);
                String created_at = jsonPost.getString(SKOOT_CREATED_AT);

                Post postObject = new Post(id, user, handle, post, 100, 50, created_at);
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
