package net.aayush.skooterapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.localytics.android.LocalyticsActivityLifecycleCallbacks;
import com.localytics.android.LocalyticsAmpSession;

import net.aayush.skooterapp.common.view.SlidingTabLayout;

public class MainActivity extends BaseActivity {
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private LocalyticsAmpSession localyticsSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check if the user is opening the app the first time
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        int introScreen = settings.getInt("intro_screen", 0);

        if (introScreen == 0) {
            Intent intent = new Intent(this, IntroductoryActivity.class);
            startActivity(intent);
        }

        this.localyticsSession = new LocalyticsAmpSession(
                this.getApplicationContext());
        getApplication().registerActivityLifecycleCallbacks(
                new LocalyticsActivityLifecycleCallbacks(this.localyticsSession));

        activateToolbar();
        if (savedInstanceState == null) {

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            SlidingFragment fragment = new SlidingFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }
    }
//
//    public static void addShortcut(Context context)
//    {
//        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
//
//        ApplicationInfo appInfo = context.getApplicationInfo();
//
//        // Shortcut name
//        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, appInfo.name);
//        shortcut.putExtra("duplicate", false); // Just create once
//
//        // Setup activity shoud be shortcut object
//        ComponentName component = new ComponentName(appInfo.packageName, appInfo.className);
//        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_MAIN).setComponent(component));
//
//        // Set shortcut icon
//        Intent.ShortcutIconResource iconResource = Intent.ShortcutIconResource.fromContext(context, appInfo.icon);
//        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);
//
//        context.sendBroadcast(shortcut);
//    }
//
//    public static void deleteShortcut(Context context)
//    {
//        Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
//
//        ApplicationInfo appInfo = context.getApplicationInfo();
//
//        // Shortcut name
//        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, appInfo.name);
//
//        ComponentName comp = new ComponentName(appInfo.packageName, appInfo.className);
//        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_MAIN).setComponent(comp));
//
//        context.sendBroadcast(shortcut);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_alerts) {
            Intent intent = new Intent(MainActivity.this, NotificationsActivity.class);
            startActivity(intent);

            return true;
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.score) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
