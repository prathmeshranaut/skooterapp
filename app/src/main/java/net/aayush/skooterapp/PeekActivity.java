package net.aayush.skooterapp;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import net.aayush.skooterapp.data.Zone;
import net.aayush.skooterapp.data.ZoneDataHandler;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PeekActivity extends BaseActivity {


    private static final String LOG_TAG = PeekActivity.class.getSimpleName();

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
                    unfollowZone(zone);
                    zoneDataHandler.unFollowZoneById(zone.getZoneId());
                    zones.get(position).setIsFollowing(false);
                    zoneDataHandler.getAllZones();
                } else {
                    followZone(zone);
                    zoneDataHandler.followZoneById(zone.getZoneId());
                    zones.get(position).setIsFollowing(true);
                    zoneDataHandler.getAllZones();
                }
                zonesArrayAdapter.notifyDataSetChanged();
            }
        });
    }

    protected void unfollowZone(Zone zone) {
        String url = BaseActivity.substituteString(getResources().getString(R.string.zones_unfollow), new HashMap<String, String>());

        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", Integer.toString(BaseActivity.userId));
        params.put("zone_id", Integer.toString(zone.getZoneId()));

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v(LOG_TAG, response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(LOG_TAG, "Error: " + error.getMessage());
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjectRequest, "unfollow_zone");
    }

    protected void followZone(Zone zone) {
        String url = BaseActivity.substituteString(getResources().getString(R.string.zones_follow), new HashMap<String, String>());

        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", Integer.toString(BaseActivity.userId));
        params.put("zone_id", Integer.toString(zone.getZoneId()));

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v(LOG_TAG, response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(LOG_TAG, "Error: " + error.getMessage());
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjectRequest, "unfollow_zone");
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
