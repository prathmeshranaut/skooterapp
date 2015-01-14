package net.aayush.skooterapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.aayush.skooterapp.R;
import net.aayush.skooterapp.data.Zone;
import net.aayush.skooterapp.data.ZoneDataHandler;

import java.util.ArrayList;
import java.util.List;

public class Peek extends Fragment {

    protected Context mContext;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = container.getContext();
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_peek, container, false);

        //Get all the zones
        ZoneDataHandler dataHandler = new ZoneDataHandler(mContext);
        List<Zone> zones = dataHandler.getAllZones();

        ArrayList<Zone> followingZones = new ArrayList<Zone>();

        for (Zone zone : zones) {
            if (zone.getIsFollowing()) {
                followingZones.add(zone);
            }
        }

        if (followingZones.size() > 0) {
            TextView addZonesTextView = (TextView) rootView.findViewById(R.id.addZonesText);
            addZonesTextView.setVisibility(View.GONE);
        }
        ListView listView = (ListView) rootView.findViewById(R.id.list_zones);
        listView.setAdapter(new ArrayAdapter<Zone>(mContext, android.R.layout.simple_list_item_1, followingZones));

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        inflater.inflate(R.menu.menu_peek, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
}