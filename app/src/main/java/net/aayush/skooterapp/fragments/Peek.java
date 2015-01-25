package net.aayush.skooterapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.android.volley.toolbox.JsonObjectRequest;

import net.aayush.skooterapp.AppController;
import net.aayush.skooterapp.BaseActivity;
import net.aayush.skooterapp.PeekActivity;
import net.aayush.skooterapp.PostAdapter;
import net.aayush.skooterapp.R;
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

public class Peek extends Fragment {

    protected static final String LOG_TAG = Peek.class.getSimpleName();
    protected Context mContext;
    protected ListView mListView;
    protected ArrayAdapter<Zone> zoneArrayAdapter;
    protected ArrayList<Zone> followingZones = new ArrayList<Zone>();
    private PostAdapter mPostsAdapter;
    private ListView mListPosts;
    private ArrayList<Post> mPostsList = new ArrayList<Post>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = container.getContext();
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_peek, container, false);

        //Get all the zones
        final ZoneDataHandler dataHandler = new ZoneDataHandler(mContext);
        List<Zone> zones = dataHandler.getAllZones();

        findZonesFollowedByUser(zones);

        mListView = (ListView) rootView.findViewById(R.id.list_zones);

        if (followingZones.size() > 0) {
            //Fetch the peek posts for the person
            int userId = BaseActivity.userId;

            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", Integer.toString(userId));

            String url = BaseActivity.substituteString(getResources().getString(R.string.peek), params);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    final String SKOOTS = "skoots";
                    final String SKOOT_ID = "id";
                    final String SKOOT_POST = "content";
                    final String SKOOT_HANDLE = "handle";
                    final String SKOOT_UPVOTES = "upvotes";
                    final String SKOOT_DOWNVOTES = "downvotes";
                    final String SKOOT_IF_USER_VOTED = "if_user_voted";
                    final String SKOOT_USER_VOTE = "user_vote";
                    final String SKOOT_USER_SCOOT = "user_skoot";
                    final String SKOOT_CREATED_AT = "created_at";
                    final String SKOOT_COMMENTS_COUNT = "comments_count";

                    try {
                        JSONArray jsonArray = response.getJSONArray(SKOOTS);

                        Log.v(LOG_TAG, jsonArray.toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonPost = jsonArray.getJSONObject(i);
                            int id = jsonPost.getInt(SKOOT_ID);
                            String post = jsonPost.getString(SKOOT_POST);
                            String handle = jsonPost.getString(SKOOT_HANDLE);
                            int upvotes = jsonPost.getInt(SKOOT_UPVOTES);
                            int commentsCount = jsonPost.getInt(SKOOT_COMMENTS_COUNT);
                            int downvotes = jsonPost.getInt(SKOOT_DOWNVOTES);
                            boolean skoot_if_user_voted = jsonPost.getBoolean(SKOOT_IF_USER_VOTED);
                            boolean user_vote = jsonPost.getBoolean(SKOOT_USER_VOTE);
                            boolean user_skoot = jsonPost.getBoolean(SKOOT_USER_SCOOT);
                            String created_at = jsonPost.getString(SKOOT_CREATED_AT);

                            Post postObject = new Post(id, handle, post, commentsCount, upvotes, downvotes, skoot_if_user_voted, user_vote, user_skoot, created_at);
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
                    VolleyLog.d(LOG_TAG, "Error: " + error.getMessage());
                }
            });

            AppController.getInstance().addToRequestQueue(jsonObjectRequest, "home_page");

            TextView addZonesTextView = (TextView) rootView.findViewById(R.id.addZonesText);
            addZonesTextView.setVisibility(View.GONE);

            mPostsAdapter = new PostAdapter(mContext, R.layout.list_view_post_row, mPostsList);

            mListView.setAdapter(mPostsAdapter);

            mListView.setOnItemClickListener(new ListView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), ViewPostActivity.class);
                    intent.putExtra(BaseActivity.SKOOTER_POST, mPostsList.get(position));
                    startActivity(intent);
                }
            });
        }
        else {
            mListView = (ListView) rootView.findViewById(R.id.list_zones);
            final ArrayAdapter<Zone> zoneArrayAdapter = new ArrayAdapter<Zone>(mContext, android.R.layout.simple_list_item_1, zones);
            View header = getLayoutInflater(savedInstanceState).inflate(R.layout.list_header_text_view, null);
            mListView.addHeaderView(header);
            mListView.setAdapter(zoneArrayAdapter);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
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