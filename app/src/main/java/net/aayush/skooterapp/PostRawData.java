package net.aayush.skooterapp;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class PostRawData {
    private String LOG_TAG = PostRawData.class.getSimpleName();
    private String mRawUrl;
    private String mData;
    private DownloadStatus mDownloadStatus;

    public PostRawData(String mRawUrl) {
        this.mRawUrl = mRawUrl;
        this.mDownloadStatus = DownloadStatus.IDLE;
    }

    public String getData() {
        return mData;
    }

    public void setRawUrl(String mRawUrl) {
        this.mRawUrl = mRawUrl;
    }

    public DownloadStatus getmDownloadStatus() {
        return mDownloadStatus;
    }

    public void execute()
    {
        this.mDownloadStatus = DownloadStatus.PROCESSING;
        PushRawData pushRawData = new PushRawData();
        pushRawData.execute(mRawUrl);
    }

    public class PushRawData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(params[0]);

            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            for(int i = 1; i < params.length; i += 2)
            {
                pairs.add(new BasicNameValuePair(params[i], params[i + 1]));
            }

            try {
                post.setEntity(new UrlEncodedFormEntity(pairs));
            } catch (UnsupportedEncodingException e) {
                Log.e("PushRawData", "Error posting to the API");
                e.printStackTrace();
            }

            HttpResponse response = null;
            String responseText = null;
            try {
                response = client.execute(post);
                responseText = EntityUtils.toString(response.getEntity());
                return responseText;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String webData) {
            mData = webData;
            Log.v(LOG_TAG, "Data returned was: "+webData);

            if(mData == null) {
                if(mRawUrl == null) {
                    mDownloadStatus = DownloadStatus.NOT_INITIALISED;
                } else
                {
                    mDownloadStatus = DownloadStatus.FAILED_OR_EMPTY;
                }
            } else {
                mDownloadStatus = DownloadStatus.OK;
            }
        }
    }
}