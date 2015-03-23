package com.skooterapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.skooterapp.AppController;
import com.skooterapp.BaseActivity;
import com.skooterapp.PeekPostAdapter;
import com.skooterapp.R;
import com.skooterapp.SkooterJsonArrayRequest;
import com.skooterapp.SkooterJsonObjectRequest;
import com.skooterapp.ViewPostActivity;
import com.skooterapp.data.Post;
import com.skooterapp.data.Zone;
import com.skooterapp.data.ZoneDataHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Peek extends Fragment {

    protected static final String LOG_TAG = Peek.class.getSimpleName();
    protected Context mContext;
    protected PullToRefreshListView mListView;
    protected ArrayAdapter<Zone> zoneArrayAdapter;
    protected ArrayList<Zone> followingZones = new ArrayList<Zone>();
    protected TextView mInfoTextView;
    private PeekPostAdapter mPostsAdapter;
    private ArrayList<Post> mPostsList = new ArrayList<Post>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = container.getContext();
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_peek, container, false);

        downloadZones();

        //Get all the zones
        final ZoneDataHandler dataHandler = new ZoneDataHandler(mContext);
        List<Zone> zones = dataHandler.getAllZones();

        findZonesFollowedByUser(zones);

        mInfoTextView = (TextView) rootView.findViewById(R.id.peek_info_text);
        mListView = (PullToRefreshListView) rootView.findViewById(R.id.list_posts);

        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                getPeekData();
            }
        });

        if (followingZones.size() > 0) {
            //Fetch the peek posts for the person
            mInfoTextView.setText("Peeking posts...");
            mInfoTextView.setVisibility(View.VISIBLE);
            getPeekData();

            mPostsAdapter = new PeekPostAdapter(mContext, R.layout.list_view_peek_row, mPostsList);

            mListView.setAdapter(mPostsAdapter);

            mListView.setOnItemClickListener(new ListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), ViewPostActivity.class);
                    intent.putExtra(BaseActivity.SKOOTER_POST, mPostsList.get(position - 1));
                    intent.putExtra("can_perform_activity", false);
                    startActivity(intent);
                }
            });
        } else {
            //TODO
            mInfoTextView.setText("Follow feeds from college campuses & companies by clicking on +");
            mInfoTextView.setVisibility(View.VISIBLE);
            final ArrayAdapter<Zone> zoneArrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, new ArrayList<Zone>());
            mListView.setAdapter(zoneArrayAdapter);
            mListView.setEnabled(false);
        }

        return rootView;
    }

    public void getPeekData() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", Integer.toString(BaseActivity.userId));

        String url = BaseActivity.substituteString(getResources().getString(R.string.peek), params);

        SkooterJsonObjectRequest jsonObjectRequest = new SkooterJsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                final String SKOOTS = "skoots";

                try {
                    JSONArray jsonArray = response.getJSONArray(SKOOTS);

                    mPostsList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Post postObject = Post.parsePostFromJSONObject(jsonArray.getJSONObject(i));
                        mPostsList.add(postObject);
                    }
                    if(jsonArray.length() > 0) {
                        mInfoTextView.setVisibility(View.GONE);
                    }
                    else {
                        mInfoTextView.setVisibility(View.VISIBLE);
                        mInfoTextView.setText("Follow feeds from college campuses & companies by clicking on +");
                    }
                    mListView.setEnabled(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, "Error processing Json Data");
                }
                if(mPostsAdapter != null) {
                    mPostsAdapter.notifyDataSetChanged();
                }
                mListView.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(LOG_TAG, "Error: " + error.getMessage());
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjectRequest, "home_page");
    }
    private void downloadZones() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", Integer.toString(BaseActivity.userId));
        params.put("location_id", Integer.toString(BaseActivity.locationId));

        final String url = BaseActivity.substituteString(getResources().getString(R.string.zones), params);
        final ZoneDataHandler dataHandler = new ZoneDataHandler(mContext);

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
                final String ZONE_IMAGE = "zone_image";

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
                        String zoneImage = jsonObject.getString(ZONE_IMAGE);

                        Zone zone = new Zone(zoneId, name, latitudeMinimum, latitudeMaximum, longitudeMinimum, longitudeMaximum, userFollows, zoneImage);

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
                        }else {
                            dataHandler.updateZone(zone);
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
    }

    private void findZonesFollowedByUser(List<Zone> zones) {
        followingZones.clear();
        for (Zone zone : zones) {
            if (zone.getIsFollowing()) {
                followingZones.add(zone);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ZoneDataHandler dataHandler = new ZoneDataHandler(mContext);
        List<Zone> zones = dataHandler.getAllZones();
        findZonesFollowedByUser(zones);
        if (zoneArrayAdapter != null) {
            zoneArrayAdapter.notifyDataSetChanged();
        }
    }

    public void onStart() {
        super.onStart();
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            ((ActionBarActivity)getActivity()).getSupportActionBar().setTitle("Peek");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        //inflater.inflate(R.menu.menu_peek, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//
//        if (id == R.id.action_add_zone) {
//            Intent intent = new Intent(mContext, PeekActivity.class);
//            startActivity(intent);
//        }

        return super.onOptionsItemSelected(item);
    }
}