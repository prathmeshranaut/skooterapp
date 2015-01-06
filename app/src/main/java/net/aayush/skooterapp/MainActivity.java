package net.aayush.skooterapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends BaseActivity {
    protected PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activateToolbar();

        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setBackgroundColor(getResources().getColor(R.color.skooterBackgroundColor));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.score);
        menuItem.setTitle(Integer.toString(mUser.getScore()));
        menuItem.setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_compose) {
            Intent intent = new Intent(MainActivity.this, ComposeActivity.class);
            startActivity(intent);

            return true;
        } else if(id == R.id.action_peek) {
            Intent intent = new Intent(MainActivity.this, PeekActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.action_me) {
            Intent intent = new Intent(MainActivity.this, MeActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
