package com.skooterapp;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.skooterapp.data.Post;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ChannelActivity extends BaseActivity {

    protected final static String LOG_TAG = ChannelActivity.class.getSimpleName();
    private ArrayList<Post> mPostsList = new ArrayList<Post>();
    private PostAdapter mPostsAdapter;
    private ListView mListView;
    String mChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        activateToolbarWithHomeEnabled("");

        handleIntent(getIntent());

        mListView = (ListView) findViewById(R.id.list_channels);

        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", Integer.toString(userId));
        params.put("location_id", Integer.toString(locationId));
        if(mChannel.charAt(0) == '@') {
            params.put("channel", mChannel.substring(1));
            activateToolbarWithHomeEnabled();
            getSupportActionBar().setTitle(mChannel.substring(1));
        } else {
            params.put("channel", mChannel);
            getSupportActionBar().setTitle(mChannel);

        }

        String url = substituteString(getResources().getString(R.string.channel_view), params);

        SkooterJsonArrayRequest jsonArrayRequest = new SkooterJsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    mPostsList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        Post postObject = Post.parsePostFromJSONObject(response.getJSONObject(i));

                        mPostsList.add(postObject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, "Error processing Json Data");
                }
                mPostsAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(LOG_TAG, error.getMessage());
            }
        });

        AppController.getInstance().addToRequestQueue(jsonArrayRequest, "channel_list");

        mPostsAdapter = new PostAdapter(this, R.layout.list_view_post_row, mPostsList);

        mListView.setAdapter(mPostsAdapter);
        mListView.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ChannelActivity.this, ViewPostActivity.class);
                intent.putExtra(SKOOTER_POST, mPostsList.get(position));
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_channel, menu);
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
            Intent parentActivityIntent = new Intent(this, MainActivity.class);
            parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(parentActivityIntent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mChannel = query;

        }
        else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            doView(intent);
        }
        else {
            mChannel = intent.getStringExtra("CHANNEL_NAME");
        }
        Log.d(LOG_TAG + " Search", intent.getExtras().toString());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }
    private void doView(final Intent queryIntent) {
        Uri uri = queryIntent.getData();
        String action = queryIntent.getAction();
        Intent i = new Intent(action);
        i.setData(uri);
        startActivity(i);
        this.finish();
    }
}