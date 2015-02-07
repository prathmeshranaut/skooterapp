package net.aayush.skooterapp.data;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import net.aayush.skooterapp.AppController;
import net.aayush.skooterapp.BaseActivity;
import net.aayush.skooterapp.R;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Comment implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String LOG_TAG = Comment.class.getSimpleName();

    private int mId;
    private int mPostId;
    private String mContent;
    private int mUpvotes;
    private int mDownvotes;
    private boolean mIfUserVoted;
    private boolean mUserVote;
    private boolean mUserComment;
    private String mTimestamp;

    public Comment(int id, int postId, String content, int upvotes, int downvotes, boolean ifUserVoted, boolean userVote, boolean userComment, String timestamp) {
        mId = id;
        mPostId = postId;
        mContent = content;
        mUpvotes = upvotes;
        mDownvotes = downvotes;
        mIfUserVoted = ifUserVoted;
        mUserVote = userVote;
        mUserComment = userComment;
        mTimestamp = timestamp;
    }

    public int getId() {
        return mId;
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

    public boolean isUserComment() {
        return mUserComment;
    }

    public String getTimestamp() {
        return BaseActivity.getTimeAgo(mTimestamp);
    }

    @Override
    public String toString() {
        return mContent + "\n" +
                (mUpvotes - mDownvotes) + "\n" +
                mTimestamp;
    }

    public void upvoteComment() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("comment_id", Integer.toString(getId()));

        String url = BaseActivity.substituteString(AppController.getInstance().getResources().getString(R.string.vote_comment), params);

        params = new HashMap<String, String>();
        params.put("user_id", Integer.toString(BaseActivity.userId));
        params.put("vote", "true");
        params.put("location_id", Integer.toString(BaseActivity.locationId));

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(LOG_TAG, "Upvote Comment: " + response.toString());
                mUserVote = true;
                mIfUserVoted = true;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(LOG_TAG, error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();

                if (headers == null
                        || headers.equals(Collections.emptyMap())) {
                    headers = new HashMap<String, String>();
                }

                headers.put("user_id", Integer.toString(BaseActivity.userId));
                headers.put("access_token", BaseActivity.accessToken);

                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(jsonObjectRequest, "upvote_comment");
    }

    public void downvoteComment() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("comment_id", Integer.toString(getId()));

        String url = BaseActivity.substituteString(AppController.getInstance().getResources().getString(R.string.vote_comment), params);

        params = new HashMap<String, String>();
        params.put("user_id", Integer.toString(BaseActivity.userId));
        params.put("vote", "false");
        params.put("location_id", Integer.toString(BaseActivity.locationId));

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(LOG_TAG, "Downvote Comment: " + response.toString());
                mUserVote = false;
                mIfUserVoted = true;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(LOG_TAG, error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();

                if (headers == null
                        || headers.equals(Collections.emptyMap())) {
                    headers = new HashMap<String, String>();
                }

                headers.put("user_id", Integer.toString(BaseActivity.userId));
                headers.put("access_token", BaseActivity.accessToken);

                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(jsonObjectRequest, "downvote_comment");
    }

    public int getVoteCount() {
        return mUpvotes - mDownvotes;
    }
}
