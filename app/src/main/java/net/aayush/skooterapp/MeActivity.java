package net.aayush.skooterapp;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;

import net.aayush.skooterapp.data.Post;

import java.util.ArrayList;
import java.util.List;


public class MeActivity extends BaseActivity {

    protected List<Post> mPostsList = new ArrayList<Post>();
    protected ArrayAdapter<Post> mPostsAdapter;
    protected ListView mListPosts;
    protected Context mContext;
    private SupportMapFragment mSupportMapFragment;
    private GoogleMap mMap;
    protected GPSLocator mLocator;
    Circle circle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);

        activateToolbarWithHomeEnabled();

        int userId = BaseActivity.userId;

        ListView myList = (ListView) findViewById(R.id.myList);

        ArrayList<String> testData = new ArrayList<String>(3);
        testData.add("My Skoots");
        testData.add("My Replies");
        testData.add("My Favorites");
        testData.add("Settings");

        myList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, testData));

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = null;
                switch (position) {
                    case 0:
                        intent = new Intent(MeActivity.this, MePostsActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(MeActivity.this, MeCommentsActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(MeActivity.this, FavoritesActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        break;
                }
            }
        });

        FragmentManager fm = getSupportFragmentManager();
        mLocator = new GPSLocator(MeActivity.this);
        mSupportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (mSupportMapFragment == null) {
            mSupportMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, mSupportMapFragment).commit();
        }

        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Check if we were successful in obtaining the map.
        if (mMap != null) {
            mMap.getUiSettings().setAllGesturesEnabled(false);
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            if (mLocator.canGetLocation()) {
                CameraUpdate update = CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(new LatLng(mLocator.getLatitude(), mLocator.getLongitude()), 15.0f));
                if (update != null) {
                    mMap.moveCamera(update);
                }
                if(circle == null) {
                    //animateCircle();
                }
            }
        }
    }

    public void animateCircle() {
        ValueAnimator vAnimator = ValueAnimator.ofInt(70, 100);
        vAnimator.setRepeatCount(ValueAnimator.INFINITE);
        vAnimator.setRepeatMode(ValueAnimator.REVERSE);
        vAnimator.setEvaluator(new IntEvaluator());
        vAnimator.setDuration(500);

        vAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        vAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int animatedValue = (Integer) valueAnimator.getAnimatedValue();
                circle.setRadius(animatedValue);
            }
        });
        vAnimator.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMap == null) {
            mMap = mSupportMapFragment.getMap();
        }
        setUpMapIfNeeded();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_me, menu);
        MenuItem menuItem = menu.findItem(R.id.score);
        menuItem.setTitle(Integer.toString(BaseActivity.mUser.getScore()));
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
        }

        return super.onOptionsItemSelected(item);
    }
}
