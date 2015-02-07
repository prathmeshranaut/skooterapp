package net.aayush.skooterapp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PostUserLogin extends PostRawData {

    private String LOG_TAG = PostUserLogin.class.getSimpleName();
    private String mRawUrl;
    private String[] mPostData;
    public String accessToken;
    public int userId;

    public int getUserId() {
        return userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public PostUserLogin(String mRawUrl, String[] mPostData) {
        super(mRawUrl);
        this.mPostData = mPostData;
        this.mRawUrl = mRawUrl;
    }

    public void execute() {
        PushJsonData pushJsonData = new PushJsonData();
    }

    public class PushJsonData extends PushRawData {
        @Override
        protected String doInBackground(String... params) {

            List<String> par = new ArrayList<String>();
            par.add(mRawUrl);
            for(int i = 0; i < mPostData.length; i++)
            {
                par.add(mPostData[i]);
            }

            return super.doInBackground(par.toArray(new String[par.size()]));
        }

        @Override
        protected void onPostExecute(String webData) {
            super.onPostExecute(webData);
            processResult();
        }
    }

    public void processResult() {
        if(getmDownloadStatus() != DownloadStatus.OK) {
            Log.e(LOG_TAG, "Error posting raw data");
            return;
        }

        final String SKOOT_USER_ID = "user_id";
        final String SKOOT_ACCESS_TOKEN = "access_token";

        try {
            JSONObject jsonObject = new JSONObject(getData());
            this.userId = jsonObject.getInt(SKOOT_USER_ID);
            this.accessToken = jsonObject.getString(SKOOT_ACCESS_TOKEN);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Error processing Json Data");
        }
    }
}
