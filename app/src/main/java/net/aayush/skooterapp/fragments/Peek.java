package net.aayush.skooterapp.fragments;

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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import net.aayush.skooterapp.AppController;
import net.aayush.skooterapp.BaseActivity;
import net.aayush.skooterapp.PeekActivity;
import net.aayush.skooterapp.PeekPostAdapter;
import net.aayush.skooterapp.R;
import net.aayush.skooterapp.SkooterJsonArrayRequest;
import net.aayush.skooterapp.SkooterJsonObjectRequest;
import net.aayush.skooterapp.ViewPostActivity;
import net.aayush.skooterapp.data.Post;
import net.aayush.skooterapp.data.Zone;
import net.aayush.skooterapp.data.ZoneDataHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Peek extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    protected static final String LOG_TAG = Peek.class.getSimpleName();
    protected Context mContext;
    protected ListView mListView;
    protected ArrayAdapter<Zone> zoneArrayAdapter;
    protected ArrayList<Zone> followingZones = new ArrayList<Zone>();
    private PeekPostAdapter mPostsAdapter;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<Post> mPostsList = new ArrayList<Post>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = container.getContext();
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_peek, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        downloadZones();

        //Get all the zones
        final ZoneDataHandler dataHandler = new ZoneDataHandler(mContext);
        List<Zone> zones = dataHandler.getAllZones();

        findZonesFollowedByUser(zones);

        mListView = (ListView) rootView.findViewById(R.id.list_zones);

        if (followingZones.size() > 0) {
            //Fetch the peek posts for the person
            getPeekData();

            mPostsAdapter = new PeekPostAdapter(mContext, R.layout.list_view_peek_row, mPostsList);

            mListView.setAdapter(mPostsAdapter);

            mListView.setOnItemClickListener(new ListView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), ViewPostActivity.class);
                    intent.putExtra(BaseActivity.SKOOTER_POST, mPostsList.get(position));
                    intent.putExtra("can_perform_activity", false);
                    startActivity(intent);
                }
            });
        } else {
            final ArrayAdapter<Zone> zoneArrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, new ArrayList<Zone>());
            View header = getLayoutInflater(savedInstanceState).inflate(R.layout.list_header_text_view, null);
            mListView.addHeaderView(header);
            mListView.setAdapter(zoneArrayAdapter);
            mListView.setEnabled(false);
        }

        return rootView;
    }

    @Override
    public void onRefresh() {
        getPeekData();
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
                    mListView.setEnabled(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, "Error processing Json Data");
                }
                if(mPostsAdapter != null) {
                    mPostsAdapter.notifyDataSetChanged();
                }
                mSwipeRefreshLayout.setRefreshing(false);
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
        inflater.inflate(R.menu.menu_peek, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_add_zone) {
            Intent intent = new Intent(mContext, PeekActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}