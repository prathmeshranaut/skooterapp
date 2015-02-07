package net.aayush.skooterapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import net.aayush.skooterapp.data.Notification;
import net.aayush.skooterapp.data.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class NotificationsActivity extends BaseActivity {

    protected final static String LOG_TAG = NotificationsActivity.class.getSimpleName();
    protected ArrayList<Notification> mNotificationArrayList = new ArrayList<Notification>();
    private NotificationAdapter mNotificationAdapter;
    private ListView mNotificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        activateToolbarWithHomeEnabled();

        mNotificationList = (ListView) findViewById(R.id.notifications_list);

        //Get the notifications for the user
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", Integer.toString(BaseActivity.userId));

        String url = BaseActivity.substituteString(getResources().getString(R.string.user_notifications), params);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                final String NOTIFICATION_TEXT = "text";
                final String NOTIFICATION_POST_REDIRECT = "post_redirect";
                final String NOTIFICATION_POST_ID = "post_id";
                final String NOTIFICATION_ID = "id";
                final String NOTIFICATION_ICON_URL = "icon_url";

                final String SKOOTS = "skoot";
                final String SKOOT_ID = "id";
                final String SKOOT_POST = "content";
                final String SKOOT_HANDLE = "channel";
                final String SKOOT_UPVOTES = "upvotes";
                final String SKOOT_DOWNVOTES = "downvotes";
                final String SKOOT_IF_USER_VOTED = "user_voted";
                final String SKOOT_USER_VOTE = "user_vote";
                final String SKOOT_USER_SCOOT = "user_skoot";
                final String SKOOT_CREATED_AT = "created_at";
                final String SKOOT_COMMENTS_COUNT = "comments_count";
                final String SKOOT_FAVORITE_COUNT = "favorites_count";
                final String SKOOT_USER_FAVORITED = "user_favorited";
                final String SKOOT_USER_COMMENTED = "user_commented";
                final String SKOOT_IMAGE_URL = "zone_image";

                try {
                    mNotificationArrayList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonNotification = response.getJSONObject(i);

                        int id = jsonNotification.getInt(NOTIFICATION_ID);
                        String text = jsonNotification.getString(NOTIFICATION_TEXT);
                        int post_id = jsonNotification.getInt(NOTIFICATION_POST_ID);
                        boolean post_redirect = jsonNotification.getBoolean(NOTIFICATION_POST_REDIRECT);
                        String icon_url = jsonNotification.getString(NOTIFICATION_ICON_URL);

                        Notification notification;

                        if(post_redirect) {
                            JSONObject jsonPost = jsonNotification.getJSONObject(SKOOTS);

                            String post = jsonPost.getString(SKOOT_POST);
                            String channel = "";
                            if (!jsonPost.isNull(SKOOT_HANDLE)) {
                                channel = "@" + jsonPost.getString(SKOOT_HANDLE);
                            }
                            int upvotes = jsonPost.getInt(SKOOT_UPVOTES);
                            int commentsCount = jsonPost.getInt(SKOOT_COMMENTS_COUNT);
                            int downvotes = jsonPost.getInt(SKOOT_DOWNVOTES);
                            boolean skoot_if_user_voted = jsonPost.getBoolean(SKOOT_IF_USER_VOTED);
                            boolean user_vote = jsonPost.getBoolean(SKOOT_USER_VOTE);
                            boolean user_skoot = jsonPost.getBoolean(SKOOT_USER_SCOOT);
                            boolean user_favorited = jsonPost.getBoolean(SKOOT_USER_FAVORITED);
                            boolean user_commented = jsonPost.getBoolean(SKOOT_USER_COMMENTED);
                            int favoriteCount = jsonPost.getInt(SKOOT_FAVORITE_COUNT);
                            String created_at = jsonPost.getString(SKOOT_CREATED_AT);
                            String image_url = jsonPost.getString(SKOOT_IMAGE_URL);

                            Post postObject = new Post(post_id, channel, post, commentsCount, favoriteCount, upvotes, downvotes, skoot_if_user_voted, user_vote, user_skoot, user_favorited, user_commented, created_at, image_url);

                            notification = new Notification(id, post_id, text, icon_url, post_redirect, postObject);
                        }
                        else {
                            notification = new Notification(id, post_id, text, icon_url, post_redirect);
                        }
                        mNotificationArrayList.add(notification);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, "Error processing Json Data");
                }
                mNotificationAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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

        AppController.getInstance().addToRequestQueue(jsonArrayRequest, "notifications");

        mNotificationAdapter =  new NotificationAdapter(this, R.layout.list_view_notification_row, mNotificationArrayList);
        mNotificationList.setAdapter(mNotificationAdapter);
        mNotificationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Notification notification = mNotificationArrayList.get(position);
                if(notification.isPostRedirect()) {
                    Intent intent = new Intent(NotificationsActivity.this, ViewPostActivity.class);
                    intent.putExtra(BaseActivity.SKOOTER_POST, notification.getPost());
                    startActivity(intent);

                    //TODO Delete notification
                    String url = BaseActivity.substituteString(getResources().getString(R.string.notification_delete), new HashMap<String, String>());

                    final int notification_id = notification.getId();
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            mNotificationArrayList.remove(position);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

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
                            headers.put("notification_id", Integer.toString(notification_id));
                            headers.put("access_token", BaseActivity.accessToken);

                            Log.d(LOG_TAG, headers.toString());

                            return headers;
                        }
                    };

                    AppController.getInstance().addToRequestQueue(jsonObjectRequest, "delete_notification");
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notifications, menu);
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
        } else if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
