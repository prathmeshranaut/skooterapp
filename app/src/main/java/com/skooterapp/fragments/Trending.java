package com.skooterapp.fragments;

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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.skooterapp.TrendingPostAdapter;
import com.skooterapp.data.Post;

import com.skooterapp.AppController;
import com.skooterapp.BaseActivity;
import com.skooterapp.ChannelActivity;
import com.skooterapp.R;
import com.skooterapp.SkooterJsonObjectRequest;
import com.skooterapp.ViewPostActivity;
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

public class Trending extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    protected static final String LOG_TAG = Trending.class.getSimpleName();
    protected List<Post> mPostsList = new ArrayList<Post>();
    protected List<String> mChannelsList = new ArrayList<String>();
    protected TrendingPostAdapter mPostsAdapter;
    protected ListView mListPosts;
    protected Context mContext;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    private List<String> items = new ArrayList<String>();
    protected ProgressBar mProgressBar;
    protected LinearLayout mProgressBarLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        mPostsAdapter.notifyDataSetChanged();
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            ((ActionBarActivity)getActivity()).getSupportActionBar().setTitle("Trending");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = container.getContext();

        View rootView = inflater.inflate(R.layout.fragment_trending, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        getTrendingSkoots();

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        mProgressBarLayout = (LinearLayout) rootView.findViewById(R.id.progress_bar_layout);

        mPostsAdapter = new TrendingPostAdapter(mContext, R.layout.list_view_post_row, mPostsList, mChannelsList);
        mListPosts = (ListView) rootView.findViewById(R.id.list_posts);
        mListPosts.setAdapter(mPostsAdapter);

        mListPosts.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOG_TAG, Integer.toString(position));
                if (position < mChannelsList.size()) {
                    Intent intent = new Intent(mContext, ChannelActivity.class);
                    Log.d(LOG_TAG, mPostsList.get(position).toString());
                    intent.putExtra("CHANNEL_NAME", mChannelsList.get(position));
                    mContext.startActivity(intent);
                } else {
                    Log.d(LOG_TAG, mPostsList.get(position).toString());
                    Intent intent = new Intent(getActivity(), ViewPostActivity.class);
                    intent.putExtra(BaseActivity.SKOOTER_POST, (Post) mPostsList.get(position - mChannelsList.size() - 1));
                    startActivity(intent);
                }
            }
        });

        ZoneDataHandler zoneDataHandler = new ZoneDataHandler(getActivity());
        List<Zone> zones = zoneDataHandler.getAllZones();
        for (Zone zone : zones) {
            items.add(zone.getZoneName());
        }

        // Inflate the layout for this fragment
        return rootView;
    }


    @Override
    public void onRefresh() {
        getTrendingSkoots();
    }

    public void getTrendingSkoots() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", Integer.toString(BaseActivity.userId));
        params.put("location_id", Integer.toString(BaseActivity.locationId));

        String url = BaseActivity.substituteString(getResources().getString(R.string.hot), params);

        SkooterJsonObjectRequest jsonObjectRequest = new SkooterJsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(LOG_TAG, response.toString());
                final String SKOOTS = "skoots";
                final String SKOOT_CHANNELS = "channels";

                try {
                    mPostsList.clear();
                    mChannelsList.clear();

                    JSONArray channels = response.getJSONArray(SKOOT_CHANNELS);
                    JSONArray jsonArray = response.getJSONArray(SKOOTS);

                    for (int i = 0; i < min(channels.length(), 5); i++) {
                        mChannelsList.add(i, channels.getString(i));
                    }

                    for (int i = 0; i < jsonArray.length(); i++) {
                        Post postObject = Post.parsePostFromJSONObject(jsonArray.getJSONObject(i));
                        mPostsList.add(postObject);
                    }
                    mProgressBar.setVisibility(View.GONE);
                    mProgressBarLayout.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, "Error processing Json Data");
                }
                mPostsAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(LOG_TAG, "Error: " + error.getMessage());
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

        AppController.getInstance().addToRequestQueue(jsonObjectRequest, "trending");
    }

    private int min(int num1, int num2) {
        if (num1 < num2) {
            return num1;
        } else {
            return num2;
        }
    }

//    public void getTrendingChannels() {
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("user_id", Integer.toString(BaseActivity.userId));
//
//        String url = BaseActivity.substituteString(getResources().getString(R.string.channel_trending), params);
//
//        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                try {
//                    mPostsList.clear();
//                    for (int i = 0; i < 3; i++) {
//                        mPostsList.add(i, response.getString(i));
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Log.e(LOG_TAG, "Error processing Json Data");
//                }
//                mPostsAdapter.notifyDataSetChanged();
//                mSwipeRefreshLayout.setRefreshing(false);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d(LOG_TAG, "Error: " + error.getMessage());
//            }
//        });
//        Log.d(LOG_TAG, mPostsList.toString());
//        AppController.getInstance().addToRequestQueue(jsonArrayRequest, "trending_channels");
//    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        inflater.inflate(R.menu.menu_trending, menu);

        if (BaseActivity.mUser.isHasNotifications()) {
            MenuItem menuItem = menu.findItem(R.id.action_alerts);
            menuItem.setIcon(R.drawable.notification_icon_active);
        }

//        MenuItem searchItem = menu.findItem(R.id.action_search);
//
//        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
//
//        SearchView search = (SearchView) MenuItemCompat.getActionView(searchItem);
//
//        search.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
//
//        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String query) {
//                loadHistory(query, m);
//
//                return true;
//            }
//        });

        super.onCreateOptionsMenu(menu, inflater);
    }

//    private void loadHistory(String query, Menu menu) {
//        // Cursor
//        String[] columns = new String[]{"_id", "text"};
//        Object[] temp = new Object[]{0, "default"};
//
//        MatrixCursor cursor = new MatrixCursor(columns);
//
//        for (int i = 0; i < items.size(); i++) {
//
//            temp[0] = i;
//            temp[1] = items.get(i);
//
//            cursor.addRow(temp);
//        }
//
//        // SearchView
//        MenuItem searchItem = menu.findItem(R.id.action_search);
//
//        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
//
//        SearchView search = (SearchView) MenuItemCompat.getActionView(searchItem);
//
//        search.setSuggestionsAdapter(new ExampleAdapter(getActivity().getBaseContext(), cursor, items));
//    }
}