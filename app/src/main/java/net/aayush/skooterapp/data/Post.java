package net.aayush.skooterapp.data;

import android.util.Log;

import net.aayush.skooterapp.BaseActivity;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post implements Serializable {

    private static final long serialVersionUID = 1L;

    private int mId;
    private String mHandle;

    private String mContent;
    private int mUpvotes;
    private int mDownvotes;
    private int mCommentsCount;
    private boolean mIfUserVoted;
    private boolean mUserVote;
    private boolean mUserSkoot;

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
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String time = null;
        try {
            Date date = formatter.parse(mTimestamp.substring(0, 24));
            time = BaseActivity.getTimeAgo(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    private String mTimestamp;

    public Post(int id, String handle, String content, int commentsCount, int upvotes, int downvotes, boolean ifUserVoted, boolean userVote, boolean userSkoot, String timestamp) {
        mId = id;
        mHandle = handle;
        mContent = content;
        mCommentsCount = commentsCount;
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

    public int getVoteCount() {
        return mUpvotes - mDownvotes;
    }

    public void upvotePost() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("https://skooter.herokuapp.com/skoot/" + Post.this.getId());

                try {
                    // Add your data
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                    nameValuePairs.add(new BasicNameValuePair("user_id", Integer.toString(BaseActivity.userId)));
                    nameValuePairs.add(new BasicNameValuePair("vote", "true"));
                    nameValuePairs.add(new BasicNameValuePair("location_id", "1"));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    // Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost);
                    Log.v("Upvote Post", response.toString());
                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                }

                mUserVote = true;
                mIfUserVoted = true;
            }
        }).start();
    }

    public void downvotePost() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("https://skooter.herokuapp.com/skoot/" + Post.this.getId());

                try {
                    // Add your data
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                    nameValuePairs.add(new BasicNameValuePair("user_id", Integer.toString(BaseActivity.userId)));
                    nameValuePairs.add(new BasicNameValuePair("vote", "false"));
                    nameValuePairs.add(new BasicNameValuePair("location_id", "1"));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    // Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost);
                    Log.v("Downvote Post", response.toString());
                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                }

                mUserVote = false;
                mIfUserVoted = true;
            }
        }).start();
    }

    public int getCommentsCount() {
        return mCommentsCount;
    }
}
