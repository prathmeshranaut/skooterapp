package net.aayush.skooterapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import net.aayush.skooterapp.PeekActivity;
import net.aayush.skooterapp.R;
import net.aayush.skooterapp.data.Zone;
import net.aayush.skooterapp.data.ZoneDataHandler;

import java.util.ArrayList;
import java.util.List;

public class Peek extends Fragment {

    protected Context mContext;
    protected ArrayAdapter<Zone> zoneArrayAdapter;
    protected ArrayList<Zone> followingZones = new ArrayList<Zone>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = container.getContext();
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_peek, container, false);

        //Get all the zones
        final ZoneDataHandler dataHandler = new ZoneDataHandler(mContext);
        List<Zone> zones = dataHandler.getAllZones();

        findZonesFollowedByUser(zones);

        if (followingZones.size() > 0) {
            TextView addZonesTextView = (TextView) rootView.findViewById(R.id.addZonesText);
            addZonesTextView.setVisibility(View.GONE);

            ListView listView = (ListView) rootView.findViewById(R.id.list_zones);
            ArrayAdapter<Zone> zoneArrayAdapter = new ArrayAdapter<Zone>(mContext, android.R.layout.simple_list_item_1, followingZones);
            listView.setAdapter(zoneArrayAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dataHandler.unFollowZoneById(position + 1);
                }
            });
        }
        else {
            ListView listView = (ListView) rootView.findViewById(R.id.list_zones);
            final ArrayAdapter<Zone> zoneArrayAdapter = new ArrayAdapter<Zone>(mContext, android.R.layout.simple_list_item_1, zones);
            listView.setAdapter(zoneArrayAdapter);

            View header = getLayoutInflater(savedInstanceState).inflate(R.layout.list_header_text_view, null);
            listView.addHeaderView(header);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dataHandler.followZoneById(position + 1);
                }
            });
        }

        return rootView;
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
        if(zoneArrayAdapter != null) {
            zoneArrayAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        inflater.inflate(R.menu.menu_peek, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_add_zone) {
            Intent intent = new Intent(mContext, PeekActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}