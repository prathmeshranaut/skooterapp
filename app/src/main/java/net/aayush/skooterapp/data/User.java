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
import net.aayush.skooterapp.SkooterJsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    protected final static String LOG_TAG = User.class.getSimpleName();

    private int mUserId;
    private int mScore;
    private List<Post> mSkoots;
    private List<Comment> mComments;
    private boolean hasNotifications = false;

    public User(int userId, int score, List<Post> skoots, List<Comment> comments) {
        mUserId = userId;
        mScore = score;
        mSkoots = skoots;
        mComments = comments;
    }

    public int getUserId() {
        return mUserId;
    }

    public int getScore() {
        return mScore;
    }

    public List<Post> getSkoots() {
        return mSkoots;
    }

    public List<Comment> getComments() {
        return mComments;
    }

    public boolean isHasNotifications() {
        return hasNotifications;
    }

    public void setHasNotifications(boolean hasNotifications) {
        this.hasNotifications = hasNotifications;
    }

    @Override
    public String toString() {
        return "User{" +
                "mUserId=" + mUserId +
                ", mScore=" + mScore +
                ", mSkoots=" + mSkoots +
                ", mComments=" + mComments +
                '}';
    }

    public static void getUserDetails() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", Integer.toString(BaseActivity.userId));

        String url = BaseActivity.substituteString(AppController.getInstance().getResources().getString(R.string.user), params);

        SkooterJsonObjectRequest jsonObjectRequest = new SkooterJsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                final String SKOOT_SCORE = "score";
                final String SKOOT_POST = "skoots";
                final String SKOOT_COMMENT = "comments";
                final String SKOOT_ID = "id";
                final String SKOOT_HANDLE = "channel";
                final String SKOOT_CONTENT = "content";
                final String SKOOT_UPVOTES = "upvotes";
                final String SKOOT_DOWNVOTES = "downvotes";
                final String SKOOT_CREATED_AT = "created_at";
                final String SKOOT_COMMENTS_COUNT = "comments_count";
                final String SKOOT_POST_ID = "post_id";
                final String SKOOT_FAVORITE_COUNT = "favorites_count";
                final String SKOOT_USER_FAVORITED = "user_favorited";
                final String SKOOT_USER_COMMENTED = "user_commented";
                final String SKOOT_IF_USER_VOTED = "user_voted";
                final String SKOOT_USER_VOTE = "user_vote";
                final String SKOOT_IMAGE_URL = "zone_image";
                final String SKOOT_IMAGE_PRESENT = "image_present";
                final String SKOOT_SMALL_IMAGE_URL = "small_image_url";
                final String SKOOT_LARGE_IMAGE_URL = "large_image_url";

                try {
                    int score = response.getInt(SKOOT_SCORE);
                    JSONArray jsonPosts = response.getJSONArray(SKOOT_POST);
                    JSONArray jsonComments = response.getJSONArray(SKOOT_COMMENT);

                    List<Post> posts = new ArrayList<Post>();
                    for (int i = 0; i < jsonPosts.length(); i++) {
                        JSONObject jsonPost = jsonPosts.getJSONObject(i);
                        int id = jsonPost.getInt(SKOOT_ID);
                        String post = jsonPost.getString(SKOOT_CONTENT);
                        String channel = "";
                        if (!jsonPost.isNull(SKOOT_HANDLE)) {
                            channel = "@" + jsonPost.getString(SKOOT_HANDLE);
                        }
                        int upvotes = jsonPost.getInt(SKOOT_UPVOTES);
                        int downvotes = jsonPost.getInt(SKOOT_DOWNVOTES);
                        int commentsCount = jsonPost.getInt(SKOOT_COMMENTS_COUNT);
                        boolean skoot_if_user_voted = jsonPost.getBoolean(SKOOT_IF_USER_VOTED);
                        boolean user_vote = jsonPost.getBoolean(SKOOT_USER_VOTE);
                        boolean user_favorited = jsonPost.getBoolean(SKOOT_USER_FAVORITED);
                        boolean user_commented = jsonPost.getBoolean(SKOOT_USER_COMMENTED);
                        int favoriteCount = jsonPost.getInt(SKOOT_FAVORITE_COUNT);
                        String created_at = jsonPost.getString(SKOOT_CREATED_AT);
                        String image_url = jsonPost.getString(SKOOT_IMAGE_URL);
                        boolean isImagePresent = jsonPost.getBoolean(SKOOT_IMAGE_PRESENT);
                        String small_image_url = jsonPost.getString(SKOOT_SMALL_IMAGE_URL);
                        String large_image_url = jsonPost.getString(SKOOT_LARGE_IMAGE_URL);

                        Post postObject = new Post(id, channel, post, commentsCount, favoriteCount, upvotes, downvotes, skoot_if_user_voted, user_vote, true, user_favorited, user_commented, created_at, image_url, isImagePresent, small_image_url, large_image_url);
                        posts.add(postObject);
                    }

                    List<Comment> comments = new ArrayList<Comment>();
                    for (int i = 0; i < jsonComments.length(); i++) {
                        JSONObject jsonComment = jsonComments.getJSONObject(i);
                        int id = jsonComment.getInt(SKOOT_ID);
                        String post = jsonComment.getString(SKOOT_CONTENT);
                        int upvotes = jsonComment.getInt(SKOOT_UPVOTES);
                        int downvotes = jsonComment.getInt(SKOOT_DOWNVOTES);
                        int postId = jsonComment.getInt(SKOOT_POST_ID);
                        String created_at = jsonComment.getString(SKOOT_CREATED_AT);

                        Comment commentObject = new Comment(id, postId, post, upvotes, downvotes, false, false, true, created_at);
                        comments.add(commentObject);
                    }
                    BaseActivity.mUser = new User(BaseActivity.userId, score, posts, comments);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, "Error processing Json Data");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(LOG_TAG, error.getMessage());
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjectRequest, "login_user");
    }

    public void updateLocation(){
        String url = BaseActivity.substituteString(AppController.getInstance().getResources().getString(R.string.user_location), new HashMap<String, String>());

        if (BaseActivity.mLocator.canGetLocation()) {
            double latitude = BaseActivity.mLocator.getLatitude();
            double longitude = BaseActivity.mLocator.getLongitude();

            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", Integer.toString(mUserId));
            params.put("lat", Double.toString(latitude));
            params.put("long", Double.toString(longitude));

            Log.v(LOG_TAG, params.toString());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    final String LOCATION_ID = "location_id";

                    try {
                        BaseActivity.locationId = response.getInt(LOCATION_ID);
                        Log.d("Location ID: ", Integer.toString(BaseActivity.locationId));
                        Log.d("Access Token: ", BaseActivity.accessToken);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

            AppController.getInstance().addToRequestQueue(jsonObjectRequest, "location");
        }
    }
}
