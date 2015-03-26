package com.skooterapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.skooterapp.data.Notification;
import com.skooterapp.data.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NotificationsActivity extends BaseActivity {

    protected final static String LOG_TAG = NotificationsActivity.class.getSimpleName();
    protected ArrayList<Notification> mNotificationArrayList = new ArrayList<Notification>();
    private NotificationAdapter mNotificationAdapter;
    private ListView mNotificationList;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        activateToolbarWithHomeEnabled("Notifications");

        mNotificationList = (ListView) findViewById(R.id.notifications_list);
        mProgressBar = (ProgressBar) findViewById(R.id.loading_progress);

        //Get the notifications for the user
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", Integer.toString(userId));

        String url = substituteString(getResources().getString(R.string.user_notifications), params);

        SkooterJsonArrayRequest jsonArrayRequest = new SkooterJsonArrayRequest(url, new Response.Listener<JSONArray>() {
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
                final String SKOOT_IMAGE_PRESENT = "image_present";
                final String SKOOT_SMALL_IMAGE_URL = "small_image_url";
                final String SKOOT_LARGE_IMAGE_URL = "large_image_url";
                final String SKOOT_IMAGE_RESOLUTION = "image_resolution";
                final String SKOOT_IMAGE_WIDTH = "width";
                final String SKOOT_IMAGE_HEIGHT = "height";

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

                        if (post_redirect) {
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
                            boolean isImagePresent = jsonPost.getBoolean(SKOOT_IMAGE_PRESENT);
                            String small_image_url = jsonPost.getString(SKOOT_SMALL_IMAGE_URL);
                            String large_image_url = jsonPost.getString(SKOOT_LARGE_IMAGE_URL);
                            JSONObject image_resolution = jsonPost.getJSONObject(SKOOT_IMAGE_RESOLUTION);
                            int width = image_resolution.getInt(SKOOT_IMAGE_WIDTH);
                            int height = image_resolution.getInt(SKOOT_IMAGE_HEIGHT);

                            Post postObject = new Post(id, channel, post, upvotes, downvotes, commentsCount, favoriteCount,  skoot_if_user_voted, user_vote, user_favorited, user_commented, user_skoot,  image_url, isImagePresent, small_image_url, large_image_url, width, height, created_at);
                            notification = new Notification(id, post_id, text, icon_url, post_redirect, postObject);
                        } else {
                            notification = new Notification(id, post_id, text, icon_url, post_redirect);
                        }
                        mNotificationArrayList.add(notification);
                    }

                    if (response.length() < 1) {
                        //No notifications
                        TextView noNotifications = (TextView) findViewById(R.id.notification_alert);
                        noNotifications.setVisibility(View.VISIBLE);
                        mUser.setHasNotifications(false);
                    } else {
                        mUser.setHasNotifications(true);
                    }
                    mProgressBar.setVisibility(View.GONE);
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
        });

        AppController.getInstance().addToRequestQueue(jsonArrayRequest, "notifications");

        mNotificationAdapter = new NotificationAdapter(this, R.layout.list_view_notification_row, mNotificationArrayList);
        mNotificationList.setAdapter(mNotificationAdapter);
        mNotificationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Notification notification = mNotificationArrayList.get(position);
                if (notification.isPostRedirect()) {
                    Intent intent = new Intent(NotificationsActivity.this, ViewPostActivity.class);
                    intent.putExtra(SKOOTER_POST, notification.getPost());
                    startActivity(intent);
                }
//                String url = substituteString(getResources().getString(R.string.notification_delete), new HashMap<String, String>());
//
//                final int notification_id = notification.getId();
//                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                    }
//                }) {
//                    @Override
//                    public Map<String, String> getHeaders() throws AuthFailureError {
//                        Map<String, String> headers = super.getHeaders();
//
//                        if (headers == null
//                                || headers.equals(Collections.emptyMap())) {
//                            headers = new HashMap<String, String>();
//                        }
//
//                        headers.put("user_id", Integer.toString(userId));
//                        headers.put("notification_id", Integer.toString(notification_id));
//                        headers.put("access_token", accessToken);
//
//                        Log.d(LOG_TAG, headers.toString());
//
//                        return headers;
//                    }
//                };
//                mNotificationArrayList.remove(position);
//                mNotificationAdapter.notifyDataSetChanged();
//                if (mNotificationArrayList.size() < 1) {
//                    mUser.setHasNotifications(false);
//                }
//                AppController.getInstance().addToRequestQueue(jsonObjectRequest, "delete_notification");
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_notifications, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class NotificationAdapter extends ArrayAdapter<Notification> {

        Context mContext;
        int mLayoutResourceId;
        List<Notification> data = new ArrayList<Notification>();

        public NotificationAdapter(Context context, int resource, List<Notification> objects) {
            super(context, resource, objects);
            mContext = context;
            mLayoutResourceId = resource;
            this.data = objects;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                convertView = inflater.inflate(mLayoutResourceId, parent, false);
            }

            final Notification notification = data.get(position);

            final ImageView imageView = (ImageView) convertView.findViewById(R.id.notification_icon);
            ImageLoader imageLoader = AppController.getInstance().getImageLoader();

            String url = notification.getIconUrl();

            imageLoader.get(url, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if(response.getBitmap() != null) {
                        imageView.setImageBitmap(response.getBitmap());
                    }
                }
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(error.getMessage());
                }
            });

            TextView textView = (TextView) convertView.findViewById(R.id.notification_text);
            textView.setText(notification.getText());

            ATextView deleteButton = (ATextView) convertView.findViewById(R.id.delete_notification);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Notification notification = mNotificationArrayList.get(position);

                    String url = substituteString(getResources().getString(R.string.notification_delete), new HashMap<String, String>());

                    final int notification_id = notification.getId();
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

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

                            headers.put("user_id", Integer.toString(userId));
                            headers.put("notification_id", Integer.toString(notification_id));
                            headers.put("access_token", accessToken);

                            Log.d(LOG_TAG, headers.toString());

                            return headers;
                        }
                    };
                    mNotificationArrayList.remove(position);
                    mNotificationAdapter.notifyDataSetChanged();
                    if (mNotificationArrayList.size() < 1) {
                        mUser.setHasNotifications(false);
                    }
                    AppController.getInstance().addToRequestQueue(jsonObjectRequest, "delete_notification");
                }
            });

            return convertView;
        }

        @Override
        public int getCount() {
            return (null != data ? data.size() : 0);
        }
    }
}
