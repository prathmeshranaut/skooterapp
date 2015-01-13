package net.aayush.skooterapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import net.aayush.skooterapp.common.view.SlidingTabLayout;

public class MainActivity extends BaseActivity {
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activateToolbar();

//        mCustomPagerAdapter = new CustomPagerAdapter(getSupportFragmentManager());
//
//        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
//        viewPager.setAdapter(mCustomPagerAdapter);
//        viewPager.setBackgroundColor(getResources().getColor(R.color.skooterBackgroundColor));

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            SlidingFragment fragment = new SlidingFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }

//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        SlidingFragment fragment = new SlidingFragment();
//        transaction.replace(R.id.sample_content_fragment, fragment);
//        transaction.commit();
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
