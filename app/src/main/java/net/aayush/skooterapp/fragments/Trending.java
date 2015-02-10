package net.aayush.skooterapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import net.aayush.skooterapp.AppController;
import net.aayush.skooterapp.BaseActivity;
import net.aayush.skooterapp.ChannelActivity;
import net.aayush.skooterapp.R;
import net.aayush.skooterapp.TrendingPostAdapter;
import net.aayush.skooterapp.ViewPostActivity;
import net.aayush.skooterapp.data.Post;
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

public class Trending extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    protected static final String LOG_TAG = Trending.class.getSimpleName();
    protected List mPostsList = new ArrayList();
    protected TrendingPostAdapter mPostsAdapter;
    protected ListView mListPosts;
    protected Context mContext;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    private List<String> items = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

        mPostsAdapter = new TrendingPostAdapter(mContext, R.layout.list_view_post_row, mPostsList);
        mListPosts = (ListView) rootView.findViewById(R.id.list_posts);
        mListPosts.setAdapter(mPostsAdapter);

        mListPosts.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOG_TAG, Integer.toString(position));
                if (position < mPostsAdapter.getChannelsCount()) {
                    Intent intent = new Intent(mContext, ChannelActivity.class);
                    Log.d(LOG_TAG, mPostsList.get(position).toString());
                    intent.putExtra("CHANNEL_NAME", mPostsList.get(position).toString());
                    mContext.startActivity(intent);
                } else {
                    Log.d(LOG_TAG, mPostsList.get(position).toString());
                    Intent intent = new Intent(getActivity(), ViewPostActivity.class);
                    intent.putExtra(BaseActivity.SKOOTER_POST, (Post) mPostsList.get(position));
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

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                final String SKOOTS = "skoots";
                final String SKOOT_CHANNELS = "channels";
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
                    mPostsList.clear();
                    JSONArray channels = response.getJSONArray(SKOOT_CHANNELS);
                    JSONArray jsonArray = response.getJSONArray(SKOOTS);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonPost = jsonArray.getJSONObject(i);
                        int id = jsonPost.getInt(SKOOT_ID);
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

                        Post postObject = new Post(id, channel, post, commentsCount, favoriteCount, upvotes, downvotes, skoot_if_user_voted, user_vote, user_skoot, user_favorited, user_commented, created_at, image_url);
                        mPostsList.add(postObject);
                    }

                    for (int i = 0; i < min(channels.length(), 3); i++) {
                        mPostsList.add(i, channels.getString(i));
                    }
                    mPostsAdapter.setChannelsCount(min(channels.length(), 3));
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
        final Menu m = menu;
        inflater.inflate(R.menu.menu_trending, menu);

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