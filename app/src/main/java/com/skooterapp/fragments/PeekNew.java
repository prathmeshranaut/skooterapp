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
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.skooterapp.AppController;
import com.skooterapp.BaseActivity;
import com.skooterapp.PeekActivity;
import com.skooterapp.PeekNewPostAdapter;
import com.skooterapp.PeekZoneActivity;
import com.skooterapp.R;
import com.skooterapp.SkooterJsonArrayRequest;
import com.skooterapp.data.ZoneNew;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PeekNew extends Fragment {

    protected static final String LOG_TAG = Peek.class.getSimpleName();
    protected Context mContext;
    protected ListView mListView;
    protected PeekNewPostAdapter zoneArrayAdapter;
    protected List<ZoneNew> mZones = new ArrayList<>();
    protected TextView mInfoTextView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = container.getContext();
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_peek_new, container, false);

        downloadZones();

        mListView = (ListView) rootView.findViewById(R.id.list_zones);
        zoneArrayAdapter = new PeekNewPostAdapter(getActivity(), R.layout.list_view_peek_new_row, mZones);
        mListView.setAdapter(zoneArrayAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), PeekZoneActivity.class);
                intent.putExtra("zone_id", mZones.get(position).getId());
                intent.putExtra("zone_name", mZones.get(position).getName());
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void downloadZones() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", Integer.toString(BaseActivity.userId));
        params.put("location_id", Integer.toString(BaseActivity.locationId));

        final String url = BaseActivity.substituteString(getResources().getString(R.string.v2_zones), params);

        SkooterJsonArrayRequest jsonArrayRequest = new SkooterJsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                final String ZONE_ID = "zone_id";
                final String NAME = "name";
                final String ACTIVE_ZONE = "active_zone";
                final String ZONE_IMAGE = "zone_image";
                final String ZONE_BACKGROUND = "zone_image_big";

                try {
                    mZones.clear();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        int zoneId = jsonObject.getInt(ZONE_ID);
                        String name = jsonObject.getString(NAME);
                        String zoneImage = jsonObject.getString(ZONE_IMAGE);
                        String zoneBackground = jsonObject.getString(ZONE_BACKGROUND);
                        boolean activeZone = jsonObject.getBoolean(ACTIVE_ZONE);

                        ZoneNew zoneNew = new ZoneNew(zoneId, name, activeZone, zoneImage, zoneBackground);

                        mZones.add(zoneNew);
                        Log.d(LOG_TAG, mZones.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                zoneArrayAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(LOG_TAG, error.networkResponse);
            }
        });

        AppController.getInstance().addToRequestQueue(jsonArrayRequest, "zones");
    }

    @Override
    public void onResume() {
        super.onResume();
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
//        inflater.inflate(R.menu.menu_peek, menu);
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