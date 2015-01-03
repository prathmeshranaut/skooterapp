package net.aayush.skooterapp;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

public class BaseActivity extends ActionBarActivity {
    private Toolbar mToolbar;
    public static int userId;

    protected static final String SKOOTER_POST = "SKOOTER_POST";
    protected static final String PREFS_NAME = "Skooter";

    protected Toolbar activateToolbar() {
        if (mToolbar == null) {
            mToolbar = (Toolbar) findViewById(R.id.app_bar);

            if (mToolbar != null) {
                setSupportActionBar(mToolbar);
            }
        }

        return mToolbar;
    }

    protected Toolbar activateToolbarWithHomeEnabled() {
        activateToolbar();
        if (mToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        return mToolbar;
    }

    protected int getUserId() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        int userId = settings.getInt("userId", 0);

        return userId;
    }
}
