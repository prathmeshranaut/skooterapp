package net.aayush.skooterapp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import net.aayush.skooterapp.data.Zone;
import net.aayush.skooterapp.data.ZoneDataHandler;

import java.util.List;


public class PeekActivity extends BaseActivity {


    private static final String LOG_TAG = PeekActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peek);

        activateToolbarWithHomeEnabled("Zones");

        final ZoneDataHandler zoneDataHandler = new ZoneDataHandler(this);
        zoneDataHandler.getAllZones();

        final List<Zone> zones = zoneDataHandler.getAllZones();
        final ZoneFollowAdapter zonesArrayAdapter = new ZoneFollowAdapter(this, R.layout.list_view_zone_follow_row, zones);

        ListView listZones = (ListView) findViewById(R.id.list_zones);
        listZones.setAdapter(zonesArrayAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_peek, menu);
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