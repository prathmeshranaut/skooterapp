package com.skooterapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.skooterapp.data.Post;

import java.util.ArrayList;
import java.util.List;


public class MeActivity extends BaseActivity {

    protected List<Post> mPostsList = new ArrayList<Post>();
    protected ArrayAdapter<Post> mPostsAdapter;
    protected ListView mListPosts;
    protected Context mContext;
    protected GPSLocator mLocator;

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

        myList.setAdapter(new ArrayAdapter<String>(this, R.layout.simple_list_view, testData));

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
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

        mLocator = new GPSLocator(MeActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_me, menu);
        MenuItem menuItem = menu.findItem(R.id.score);
        menuItem.setTitle(Integer.toString(mUser.getScore()));
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
