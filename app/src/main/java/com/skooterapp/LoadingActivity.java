package com.skooterapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.skooterapp.data.Comment;
import com.skooterapp.data.Post;
import com.skooterapp.data.User;
import com.skooterapp.data.Zone;
import com.skooterapp.data.ZoneDataHandler;

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
    protected final String LOG_TAG = LoadingActivity.class.getSimpleName();
    Handler mHandler = new Handler();
    Thread thread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        mLoadingTextView = (TextView) findViewById(R.id.loading_text);
        mSettings = getSharedPreferences(PREFS_NAME, 0);
        userId = mSettings.getInt("userId", 0);
        accessToken = mSettings.getString("access_token", "");
        mLocator = new GPSLocator(this);

        if (userId == 0) {
            loginUser();
        } else {
            getUserDetails();
        }
    }

    private void downloadZones() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", Integer.toString(userId));
        params.put("location_id", Integer.toString(locationId));

        final String url = substituteString(getResources().getString(R.string.zones), params);
        final ZoneDataHandler dataHandler = new ZoneDataHandler(this);

        SkooterJsonArrayRequest jsonArrayRequest = new SkooterJsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                final String ZONE_ID = "zone_id";
                final String NAME = "name";
                final String LATITUDE_MINIMUM = "lat_min";
                final String LATITUDE_MAXIMUM = "lat_max";
                final String LONGITUDE_MINIMUM = "long_min";
                final String LONGITUDE_MAXIMUM = "long_max";
                final String USER_FOLLOWS = "user_follows";
                final String ACTIVE_ZONE = "active_zone";
                final String ZONE_IMAGE = "zone_image";


                try {
                    mActiveZones.clear();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        int zoneId = jsonObject.getInt(ZONE_ID);
                        String name = jsonObject.getString(NAME);
                        float latitudeMinimum = (float) jsonObject.getDouble(LATITUDE_MINIMUM);
                        float latitudeMaximum = (float) jsonObject.getDouble(LATITUDE_MAXIMUM);
                        float longitudeMinimum = (float) jsonObject.getDouble(LONGITUDE_MINIMUM);
                        float longitudeMaximum = (float) jsonObject.getDouble(LONGITUDE_MAXIMUM);
                        boolean userFollows = jsonObject.getBoolean(USER_FOLLOWS);
                        boolean active_zone = jsonObject.getBoolean(ACTIVE_ZONE);
                        String zoneImage = jsonObject.getString(ZONE_IMAGE);
                        Log.d("LoadingZone", zoneImage);

                        Zone zone = new Zone(zoneId, name, latitudeMinimum, latitudeMaximum, longitudeMinimum, longitudeMaximum, userFollows, zoneImage);

                        List<Zone> zones = dataHandler.getAllZones();
                        boolean flag = false;
                        for (Zone z : zones) {
                            if (z.getZoneId() == zone.getZoneId()) {
                                flag = true;
                                break;
                            }
                        }
                        if (!flag) {
                            //Add
                            dataHandler.addZone(zone);
                        } else {
                            dataHandler.updateZone(zone);
                        }
                        if (active_zone) {
                            mActiveZones.add(zone);
                            Log.d("Active Zones: ", mActiveZones.toString());
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
        });

        AppController.getInstance().addToRequestQueue(jsonArrayRequest, "zones");
        Intent i = new Intent(LoadingActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void addUserLocation() {
        String url = substituteString(getResources().getString(R.string.user_location), new HashMap<String, String>());

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
                        locationId = response.getInt(LOCATION_ID);
                        Log.d("Location ID: ", Integer.toString(locationId));
                        Log.d("Access Token: ", accessToken);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    downloadZones();
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

                    headers.put("user_id", Integer.toString(userId));
                    headers.put("access_token", accessToken);

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

        String url = substituteString(getResources().getString(R.string.user), params);

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
                    mUser = new User(userId, score, posts, comments);
                    addUserLocation();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, "Error processing Json Data");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(LOG_TAG, error.getMessage());
                bootstrapCheckDeviceSettings();
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjectRequest, "login_user");
    }

    public void loginUser() {
        String androidId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        Map<String, String> params = new HashMap<>();
        params.put("phone", androidId);

        String url = substituteString(getResources().getString(R.string.user_new), new HashMap<String, String>());
        Log.d(LOG_TAG, params.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                final String SKOOT_USER_ID = "user_id";
                final String SKOOT_ACCESS_TOKEN = "access_token";

                Log.d(LOG_TAG, response.toString());
                try {
                    userId = response.getInt(SKOOT_USER_ID);
                    accessToken = response.getString(SKOOT_ACCESS_TOKEN);

                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putInt("userId", userId);
                    editor.putString("access_token", accessToken);
                    editor.apply();
                    getUserDetails();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, "Error processing Json Data");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                bootstrapCheckDeviceSettings();
                error.printStackTrace();
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjectRequest, "signup_user");
    }

    public void bootstrapCheckDeviceSettings() {
        if (!isOnline()) {
            mLoadingTextView.setText("We're unable to connect with the Skooter servers. \n We'll keep trying");
            mLoadingTextView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Check if the device is online
     *
     * @return
     */
    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
            return false;
        }

        return true;
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
}