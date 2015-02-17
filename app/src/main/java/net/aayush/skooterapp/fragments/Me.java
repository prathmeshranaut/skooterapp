package net.aayush.skooterapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;

import net.aayush.skooterapp.AppController;
import net.aayush.skooterapp.BaseActivity;
import net.aayush.skooterapp.FavoritesActivity;
import net.aayush.skooterapp.MeCommentsActivity;
import net.aayush.skooterapp.MePostsActivity;
import net.aayush.skooterapp.R;
import net.aayush.skooterapp.data.Post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Me extends Fragment {
    protected List<Post> mPostsList = new ArrayList<Post>();
    protected ArrayAdapter<Post> mPostsAdapter;
    protected ListView mListPosts;
    protected Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = container.getContext();

        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.activity_me, container, false);
        int userId = BaseActivity.userId;

        ListView myList = (ListView) rootView.findViewById(R.id.myList);

        ArrayList<String> testData = new ArrayList<String>(3);
        testData.add("My Skoots");
        testData.add("My Replies");
        testData.add("My Favorites");

        myList.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, testData));

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = null;
                switch (position) {
                    case 0:
                        intent = new Intent(getActivity(), MePostsActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(getActivity(), MeCommentsActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(getActivity(), FavoritesActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });

        final ImageView map = (ImageView) rootView.findViewById(R.id.map);
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        Map<String, String> params = new HashMap<String, String>();
        params.put("latitude", Float.toString(0.0f));
        params.put("longitude", Float.toString(0.0f));
        params.put("width", Integer.toString(width));
        params.put("height", Integer.toString(300));

        String url = "http://maps.google.com/maps/api/staticmap?center=" + Double.toString(BaseActivity.mLocator.getLatitude()) + "," + Double.toString(BaseActivity.mLocator.getLongitude()) + "&zoom=15&" + "size=" + Integer.toString(width) + "x300&sensor=false";

        Log.v("Map", url);
        imageLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    map.setImageBitmap(response.getBitmap());
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("", error.getMessage());
            }
        });

        //setUpMapIfNeeded();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        inflater.inflate(R.menu.menu_me, menu);
        MenuItem menuItem = menu.findItem(R.id.score);
        menuItem.setTitle(Integer.toString(BaseActivity.mUser.getScore()));
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            ((ActionBarActivity)getActivity()).getSupportActionBar().setTitle("Me");
        }
    }
}
