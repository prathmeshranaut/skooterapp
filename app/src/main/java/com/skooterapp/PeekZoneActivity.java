package com.skooterapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.skooterapp.data.Post;
import com.skooterapp.data.Zone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class PeekZoneActivity extends BaseActivity {

    protected static final String LOG_TAG = PeekZoneActivity.class.getSimpleName();
    protected Context mContext;
    protected ListView mListView;
    protected ArrayAdapter<Zone> zoneArrayAdapter;
    protected ArrayList<Zone> followingZones = new ArrayList<Zone>();
    protected TextView mInfoTextView;
    private PeekPostAdapter mPostsAdapter;
    private ArrayList<Post> mPostsList = new ArrayList<Post>();
    int zoneId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peek_zone);

        activateToolbarWithHomeEnabled("");

        Intent intent = getIntent();
        zoneId = intent.getIntExtra("zone_id", 1);
        String zoneName = intent.getStringExtra("zone_name");

        getSupportActionBar().setTitle(zoneName);
        downloadPeekData();

        mListView = (ListView) findViewById(R.id.list_posts);
        mInfoTextView = (TextView) findViewById(R.id.peek_info_text);

        mPostsAdapter = new PeekPostAdapter(this, R.layout.list_view_peek_row, mPostsList);

        mListView.setAdapter(mPostsAdapter);

        mListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PeekZoneActivity.this, ViewPostActivity.class);
                intent.putExtra(BaseActivity.SKOOTER_POST, mPostsList.get(position));
                intent.putExtra("can_perform_activity", false);
                startActivity(intent);
            }
        });
    }

    protected void downloadPeekData() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", Integer.toString(BaseActivity.userId));
        params.put("zone_id", Integer.toString(zoneId));

        String url = BaseActivity.substituteString(getResources().getString(R.string.v2_peek), params);

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
                    Log.d(LOG_TAG, response.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, "Error processing Json Data");
                }
                if(mPostsAdapter != null) {
                    mPostsAdapter.notifyDataSetChanged();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(LOG_TAG, "Error: " + error.getMessage());
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjectRequest, "home_page");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_peek_zone, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
