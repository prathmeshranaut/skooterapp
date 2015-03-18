package com.skooterapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.skooterapp.ATextView;
import com.skooterapp.AppController;
import com.skooterapp.BaseActivity;
import com.skooterapp.R;
import com.skooterapp.SkooterJsonArrayRequest;
import com.skooterapp.SkooterJsonObjectRequest;
import com.skooterapp.TabsPagerAdapter;
import com.skooterapp.ViewPostActivity;
import com.skooterapp.data.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Notification extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    protected static final String LOG_TAG = Notification.class.getSimpleName();
    protected List<com.skooterapp.data.Notification> mNotificationArrayList
            = new ArrayList<com.skooterapp.data.Notification>();
    protected NotificationAdapter mNotificationAdapter;
    protected ListView mNotificationList;
    protected Context mContext;
    protected TextView mNoNotificationText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        mNotificationAdapter.notifyDataSetChanged();
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("Notifications");
        }
        BaseActivity.mUser.setHasNotifications(false);
        TabsPagerAdapter.imageResId[3] = R.drawable.notification;
        TabsPagerAdapter.activeImageResId[3] = R.drawable.notification_active;

        markNotificationsRead();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = container.getContext();

        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);
        mNotificationList = (ListView) rootView.findViewById(R.id.notifications_list);
        mNoNotificationText = (TextView) rootView.findViewById(R.id.notification_alert);

        getNotificationsForUser();

        mNotificationAdapter = new NotificationAdapter(mContext, R.layout.list_view_notification_row, mNotificationArrayList);
        mNotificationList.setAdapter(mNotificationAdapter);

        mNotificationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                com.skooterapp.data.Notification notification = mNotificationArrayList.get(position);
                if (notification.isPostRedirect()) {
                    Intent intent = new Intent(mContext, ViewPostActivity.class);
                    intent.putExtra(BaseActivity.SKOOTER_POST, notification.getPost());
                    startActivity(intent);
                }
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }


    @Override
    public void onRefresh() {
        getNotificationsForUser();
    }

    public void getNotificationsForUser() {
        //Get the notifications for the user
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", Integer.toString(BaseActivity.userId));

        String url = BaseActivity.substituteString(getResources().getString(R.string.user_notifications), params);

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

                try {
                    mNotificationArrayList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonNotification = response.getJSONObject(i);
                        int id = jsonNotification.getInt(NOTIFICATION_ID);
                        String text = jsonNotification.getString(NOTIFICATION_TEXT);
                        int post_id = jsonNotification.getInt(NOTIFICATION_POST_ID);
                        boolean post_redirect = jsonNotification.getBoolean(NOTIFICATION_POST_REDIRECT);
                        String icon_url = jsonNotification.getString(NOTIFICATION_ICON_URL);

                        com.skooterapp.data.Notification notification;

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

                            Post postObject = new Post(post_id, channel, post, commentsCount, favoriteCount, upvotes, downvotes, skoot_if_user_voted, user_vote, user_skoot, user_favorited, user_commented, created_at, image_url, isImagePresent, small_image_url, large_image_url);
                            notification = new com.skooterapp.data.Notification(id, post_id, text, icon_url, post_redirect, postObject);
                        } else {
                            notification = new com.skooterapp.data.Notification(id, post_id, text, icon_url, post_redirect);
                        }
                        mNotificationArrayList.add(notification);
                    }

                    if (response.length() < 1) {
                        //No notifications
                        BaseActivity.mUser.setHasNotifications(false);
                        TabsPagerAdapter.imageResId[3] = R.drawable.notification;
                        TabsPagerAdapter.activeImageResId[3] = R.drawable.notification_active;

                        mNoNotificationText.setVisibility(View.VISIBLE);
                    } else {
                        BaseActivity.mUser.setHasNotifications(true);
                        TabsPagerAdapter.imageResId[3] = R.drawable.notification_alert;
                        TabsPagerAdapter.activeImageResId[3] = R.drawable.notification_active_alert;

                        mNoNotificationText.setVisibility(View.GONE);
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
        });
        AppController.getInstance().addToRequestQueue(jsonArrayRequest, "notifications");
    }

    public void markNotificationsRead() {
        if(mNotificationArrayList.size() > 0) {
            Map<String, String> params = new HashMap<>();
            params.put("user_id", Integer.toString(BaseActivity.userId));

            String url = BaseActivity.substituteString(getString(R.string.notification_read), params);

            params = new HashMap<>();
            params.put("notification_id", Integer.toString(mNotificationArrayList.get(0).getId()));
            SkooterJsonObjectRequest skooterJsonObjectRequest = new SkooterJsonObjectRequest(Request.Method.PUT, url, new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(LOG_TAG, error.getMessage());
                }
            });

            AppController.getInstance().addToRequestQueue(skooterJsonObjectRequest, "notification_read");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    public class NotificationAdapter extends ArrayAdapter<com.skooterapp.data.Notification> {

        Context mContext;
        int mLayoutResourceId;
        List<com.skooterapp.data.Notification> data = new ArrayList<com.skooterapp.data.Notification>();

        public NotificationAdapter(Context context, int resource, List<com.skooterapp.data.Notification> objects) {
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

            final com.skooterapp.data.Notification notification = data.get(position);

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
                    com.skooterapp.data.Notification notification = mNotificationArrayList.get(position);

                    String url = BaseActivity.substituteString(getResources().getString(R.string.notification_delete), new HashMap<String, String>());

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

                            headers.put("user_id", Integer.toString(BaseActivity.userId));
                            headers.put("notification_id", Integer.toString(notification_id));
                            headers.put("access_token", BaseActivity.accessToken);

                            Log.d(LOG_TAG, headers.toString());

                            return headers;
                        }
                    };
                    mNotificationArrayList.remove(position);
                    mNotificationAdapter.notifyDataSetChanged();
                    if (mNotificationArrayList.size() < 1) {
                        BaseActivity.mUser.setHasNotifications(false);
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