package net.aayush.skooterapp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import net.aayush.skooterapp.data.Zone;
import net.aayush.skooterapp.data.ZoneDataHandler;

import java.util.List;


public class PeekActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peek);

        activateToolbarWithHomeEnabled();

        final ZoneDataHandler zoneDataHandler = new ZoneDataHandler(this);
        zoneDataHandler.getAllZones();

        final List<Zone> zones = zoneDataHandler.getAllZones();
        final ArrayAdapter<Zone> zonesArrayAdapter = new ArrayAdapter<Zone>(this, android.R.layout.simple_list_item_1, zones);

        ListView listZones = (ListView) findViewById(R.id.list_zones);
        listZones.setAdapter(zonesArrayAdapter);
        listZones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Zone zone = zones.get(position);
                if(zone.getIsFollowing()) {
                    zoneDataHandler.unFollowZoneById(zone.getZoneId());
                    zones.get(position).setIsFollowing(false);
                    zoneDataHandler.getAllZones();
                } else {
                    zoneDataHandler.followZoneById(zone.getZoneId());
                    zones.get(position).setIsFollowing(true);
                    zoneDataHandler.getAllZones();
                }
                zonesArrayAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_peek, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
