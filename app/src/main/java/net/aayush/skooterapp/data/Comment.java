package net.aayush.skooterapp.data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import net.aayush.skooterapp.AppController;
import net.aayush.skooterapp.BaseActivity;
import net.aayush.skooterapp.R;
import net.aayush.skooterapp.SkooterJsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
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

    public int getPostId() {
        return mPostId;
    }

    public void upvoteComment() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("comment_id", Integer.toString(getId()));

        String url = BaseActivity.substituteString(AppController.getInstance().getResources().getString(R.string.vote_comment), params);

        params = new HashMap<String, String>();
        params.put("user_id", Integer.toString(BaseActivity.userId));
        params.put("vote", "true");
        params.put("location_id", Integer.toString(BaseActivity.locationId));

        SkooterJsonObjectRequest jsonObjectRequest = new SkooterJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
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
        });

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

        SkooterJsonObjectRequest jsonObjectRequest = new SkooterJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
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
        });

        AppController.getInstance().addToRequestQueue(jsonObjectRequest, "downvote_comment");
    }

    public int getVoteCount() {
        return mUpvotes - mDownvotes;
    }

    public static int findCommentPositionInListById(List<Comment> commentList, int commentId) {
        int i = 0;
        for (Comment comment : commentList) {
            if (comment.getId() == commentId) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public static Comment parseCommentFromJSONObject(JSONObject response) {
        final String SKOOT_ID = "id";
        final String SKOOT_POST_ID = "post_id";
        final String SKOOT_POST = "content";
        final String SKOOT_CREATED_AT = "created_at";
        final String SKOOT_UPVOTES = "upvotes";
        final String SKOOT_DOWNVOTES = "downvotes";
        final String SKOOT_IF_USER_VOTED = "if_user_voted";
        final String SKOOT_USER_VOTE = "user_vote";
        final String SKOOT_USER_COMMENT = "user_comment";

        try {
            int id = response.getInt(SKOOT_ID);
            String comment = response.getString(SKOOT_POST);
            int post_id = response.getInt(SKOOT_POST_ID);
            int upvotes = response.getInt(SKOOT_UPVOTES);
            int downvotes = response.getInt(SKOOT_DOWNVOTES);
            boolean if_user_voted = response.getBoolean(SKOOT_IF_USER_VOTED);
            boolean user_vote = response.getBoolean(SKOOT_USER_VOTE);
            boolean user_skoot = response.getBoolean(SKOOT_USER_COMMENT);
            String timestamp = response.getString(SKOOT_CREATED_AT);

            return new Comment(id, post_id, comment, upvotes, downvotes, if_user_voted, user_vote, user_skoot, timestamp);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Error processing JSON data");
        }
        return null;
    }
}
