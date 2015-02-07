package net.aayush.skooterapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import net.aayush.skooterapp.data.Comment;
import net.aayush.skooterapp.data.Post;
import net.aayush.skooterapp.data.User;
import net.aayush.skooterapp.data.Zone;
import net.aayush.skooterapp.data.ZoneDataHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LoadingActivity extends BaseActivity {

    protected SharedPreferences mSettings;
    protected TextView mLoadingTextView;
    protected GPSLocator mLocator;
    protected final String LOG_TAG = LoadingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        mLoadingTextView = (TextView) findViewById(R.id.loadingText);
        mSettings = getSharedPreferences(PREFS_NAME, 0);
        userId = mSettings.getInt("userId", 0);
        accessToken = mSettings.getString("access_token", "");
        mLocator = new GPSLocator(this);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoadingActivity.this);
            alertDialogBuilder.setMessage("Looks like you aren't connected to the internet! Would you please mind doing so?");
            alertDialogBuilder.setPositiveButton("Ok!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {

                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        if (userId == 0) {
            loginUser();
        } else {
            getUserDetails();
        }
    }

    private void downloadZones() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", Integer.toString(BaseActivity.userId));
        params.put("location_id", Integer.toString(BaseActivity.locationId));

        final String url = BaseActivity.substituteString(getResources().getString(R.string.zones), params);
        final ZoneDataHandler dataHandler = new ZoneDataHandler(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                final String ZONE_ID = "zone_id";
                final String NAME = "name";
                final String LATITUDE_MINIMUM = "lat_min";
                final String LATITUDE_MAXIMUM = "lat_max";
                final String LONGITUDE_MINIMUM = "long_min";
                final String LONGITUDE_MAXIMUM = "long_max";
                final String USER_FOLLOWS = "user_follows";

                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        int zoneId = jsonObject.getInt(ZONE_ID);
                        String name = jsonObject.getString(NAME);
                        float latitudeMinimum = (float) jsonObject.getDouble(LATITUDE_MINIMUM);
                        float latitudeMaximum = (float) jsonObject.getDouble(LATITUDE_MAXIMUM);
                        float longitudeMinimum = (float) jsonObject.getDouble(LONGITUDE_MINIMUM);
                        float longitudeMaximum = (float) jsonObject.getDouble(LONGITUDE_MAXIMUM);
                        boolean userFollows = jsonObject.getBoolean(USER_FOLLOWS);

                        Zone zone = new Zone(zoneId, name, latitudeMinimum, latitudeMaximum, longitudeMinimum, longitudeMaximum, userFollows);

                        List<Zone> zones = dataHandler.getAllZones();
                        boolean flag = false;
                        for(Zone z: zones) {
                            if(z.getZoneId() == zone.getZoneId()) {
                                flag = true;
                                break;
                            }
                        }
                        if(!flag) {
                            //Add
                            dataHandler.addZone(zone);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(LOG_TAG, error.networkResponse);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();

                if (headers == null
                        || headers.equals(Collections.emptyMap())) {
                    headers = new HashMap<String, String>();
                }

                headers.put("access_token", BaseActivity.accessToken);

                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(jsonArrayRequest, "zones");
    }

    public void addUserLocation() {
        String url = BaseActivity.substituteString(getResources().getString(R.string.user_location), new HashMap<String, String>());

        if (mLocator.canGetLocation()) {
            double latitude = mLocator.getLatitude();
            double longitude = mLocator.getLongitude();

            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", Integer.toString(mUser.getUserId()));
            params.put("lat", Double.toString(latitude));
            params.put("long", Double.toString(longitude));

            Log.v(LOG_TAG, params.toString());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    final String LOCATION_ID = "location_id";

                    try {
                        BaseActivity.locationId = response.getInt(LOCATION_ID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(LOG_TAG, error.getMessage());
                    mLoadingTextView.setText("We're having a hard time locating you!");
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = super.getHeaders();

                    if (headers == null
                            || headers.equals(Collections.emptyMap())) {
                        headers = new HashMap<String, String>();
                    }

                    headers.put("access_token", BaseActivity.accessToken);

                    return headers;
                }
            };

            AppController.getInstance().addToRequestQueue(jsonObjectRequest, "location");
        } else {
            mLoadingTextView.setText("We're having a hard time locating you!");
            mLocator.showSettingsAlert();
        }
    }

    public void getUserDetails() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", Integer.toString(userId));

        String url = BaseActivity.substituteString(getResources().getString(R.string.user), params);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
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
                final String SKOOT_POST_ID = "id";
                final String SKOOT_FAVORITE_COUNT = "favorites_count";
                final String SKOOT_USER_FAVORITED = "user_favorited";
                final String SKOOT_USER_COMMENTED = "user_commented";
                final String SKOOT_IF_USER_VOTED = "user_voted";
                final String SKOOT_USER_VOTE = "user_vote";
                final String SKOOT_IMAGE_URL = "zone_image";

                try {
                    int score = response.getInt(SKOOT_SCORE);
                    JSONArray jsonPosts = response.getJSONArray(SKOOT_POST);
                    JSONArray jsonComments = response.getJSONArray(SKOOT_COMMENT);

                    List<Post> posts = new ArrayList<Post>();
                    for (int i = 0; i < jsonPosts.length(); i++) {
                        JSONObject jsonPost = jsonPosts.getJSONObject(i);
                        int id = jsonPost.getInt(SKOOT_ID);
                        String post = jsonPost.getString(SKOOT_CONTENT);
                        String channel = jsonPost.getString(SKOOT_HANDLE);
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

                        Post postObject = new Post(id, channel, post, commentsCount, favoriteCount, upvotes, downvotes, skoot_if_user_voted, user_vote, true, user_favorited, user_commented, created_at, image_url);
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
                    mUser = new User(userId, score, posts, comments);
                    addUserLocation();
                    downloadZones();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, "Error processing Json Data");
                }
                Intent i = new Intent(LoadingActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(LOG_TAG, error.getMessage());
                mLoadingTextView.setText("Darn! Looks like we couldn't log you in. Hold on we'll keep trying!");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();

                if (headers == null
                        || headers.equals(Collections.emptyMap())) {
                    headers = new HashMap<String, String>();
                }

                headers.put("access_token", BaseActivity.accessToken);

                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(jsonObjectRequest, "login_user");
    }

    public void loginUser() {
        String androidId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String[] data = {"phone", androidId};

        LoginUser loginUser = new LoginUser("http://skooter.elasticbeanstalk.com/user", data);
        loginUser.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_loading, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class LoginUser extends PostUserLogin {

        public LoginUser(String mRawUrl, String[] postData) {
            super(mRawUrl, postData);
        }

        public void execute() {
            super.execute();
            LoginUserData loginUserData = new LoginUserData();
            loginUserData.execute();
        }

        public class LoginUserData extends PushJsonData {

            protected void onPostExecute(String webData) {
                super.onPostExecute(webData);
                BaseActivity.userId = getUserId();
                BaseActivity.accessToken = getAccessToken();
                if (BaseActivity.userId == 0) {
                    if (getmDownloadStatus() == DownloadStatus.FAILED_OR_EMPTY) {
                        mLoadingTextView.setText("Darn! Looks like we couldn't log you in. Hold on we'll keep trying!");
                    }
                } else {
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putInt("userId", userId);
                    editor.putString("access_token", accessToken);
                    editor.commit();
                    getUserDetails();
                }
            }
        }
    }
}
