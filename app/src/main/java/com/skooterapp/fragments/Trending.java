package com.skooterapp.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.skooterapp.AppController;
import com.skooterapp.BaseActivity;
import com.skooterapp.ChannelActivity;
import com.skooterapp.SearchSuggestionsAdapter;
import com.skooterapp.R;
import com.skooterapp.SettingsActivity;
import com.skooterapp.SkooterJsonArrayRequest;
import com.skooterapp.SkooterJsonObjectRequest;
import com.skooterapp.TrendingPostAdapter;
import com.skooterapp.ViewPostActivity;
import com.skooterapp.data.Post;
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
    protected PullToRefreshListView mListPosts;
    protected Context mContext;
    private List<String> items = new ArrayList<String>();
    private Menu mMenu;
    private SearchView mSearchView;
    protected SearchSuggestionsAdapter mSuggestionAdapter;

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
            ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("Trending");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = container.getContext();

        View rootView = inflater.inflate(R.layout.fragment_trending, container, false);

        getTrendingSkoots();

        mPostsAdapter = new TrendingPostAdapter(mContext, R.layout.list_view_post_row, mPostsList, mChannelsList);
        mListPosts = (PullToRefreshListView) rootView.findViewById(R.id.list_posts);
        mListPosts.setAdapter(mPostsAdapter);
        mListPosts.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {

                getTrendingSkoots();
            }
        });

        mListPosts.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOG_TAG, Integer.toString(position));
                if (position < mChannelsList.size() + 1) {
                    Intent intent = new Intent(mContext, ChannelActivity.class);
                    Log.d(LOG_TAG, mPostsList.get(position - 1).toString());
                    intent.putExtra("CHANNEL_NAME", mChannelsList.get(position - 1));
                    mContext.startActivity(intent);
                } else if (position > mChannelsList.size() + 1) {
                    Log.d(LOG_TAG, mPostsList.get(position).toString());
                    Intent intent = new Intent(getActivity(), ViewPostActivity.class);
                    intent.putExtra(BaseActivity.SKOOTER_POST, (Post) mPostsList.get(position - mChannelsList.size() - 2));
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
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, "Error processing Json Data");
                }
                mPostsAdapter.notifyDataSetChanged();
                mListPosts.onRefreshComplete();
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        inflater.inflate(R.menu.menu_trending, menu);

        mMenu = menu;
        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        mSearchView.setIconified(true);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d(LOG_TAG, s);
                mSearchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                items.clear();
                if (query.length() >= 2) {
                    loadSuggestions(query);
                }
                return true;
            }
        });
        mSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int i) {
                Cursor cursor = (Cursor) mSearchView.getSuggestionsAdapter().getItem(i);
                String feedName = cursor.getString(2);
                mSearchView.setQuery(feedName, false);
                mSearchView.clearFocus();
                Intent intent = new Intent(getActivity(), ChannelActivity.class);
                Log.d(LOG_TAG, feedName);
                intent.putExtra("CHANNEL_NAME", feedName);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onSuggestionClick(int i) {
                Cursor cursor = (Cursor) mSearchView.getSuggestionsAdapter().getItem(i);
                String feedName = cursor.getString(1);
                mSearchView.setQuery(feedName, false);
                mSearchView.clearFocus();
                Intent intent = new Intent(getActivity(), ChannelActivity.class);
                intent.putExtra("CHANNEL_NAME", feedName);
                Log.d(LOG_TAG, feedName);
                startActivity(intent);
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void loadSuggestions(String query) {
        AppController.getInstance().cancelPendingRequests("suggestions");
        Map<String, String> params = new HashMap<>();
        params.put("user_id", Integer.toString(BaseActivity.userId));
        params.put("query", query);

        String url = BaseActivity.substituteString(getString(R.string.channel_suggestion), params);
        final String q = query;
        SkooterJsonArrayRequest skooterJsonArrayRequest = new SkooterJsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                items.clear();
                Log.d(LOG_TAG, response.toString());
                for (int i = 0; i < response.length(); i++) {
                    try {
                        if (!response.isNull(i)) {
                            items.add(response.getString(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                loadHistory(q, mMenu);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(LOG_TAG, error.getMessage());
            }
        });

        AppController.getInstance().addToRequestQueue(skooterJsonArrayRequest, "suggestions");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_search) {
            mSearchView.setIconified(true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadHistory(String query, Menu menu) {
        // Cursor
        String[] columns = new String[]{"_id", "text"};
        Object[] temp = new Object[]{0, "default"};

        MatrixCursor cursor = new MatrixCursor(columns);

        for (int i = 0; i < items.size(); i++) {

            temp[0] = i;
            temp[1] = items.get(i);

            cursor.addRow(temp);
        }

        if (mSuggestionAdapter == null) {
            mSuggestionAdapter = new SearchSuggestionsAdapter(getActivity().getBaseContext(), cursor, items);
            mSearchView.setSuggestionsAdapter(mSuggestionAdapter);
        }else {
            mSuggestionAdapter.changeCursor(cursor);
        }
        mSuggestionAdapter.notifyDataSetInvalidated();
    }
}