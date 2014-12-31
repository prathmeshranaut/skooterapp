package net.aayush.skooterapp;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

public class BaseActivity extends ActionBarActivity {

    private Toolbar mToolbar;

    protected static final String SKOOTER_POST = "SKOOTER_POST";

    protected Toolbar activateToolbar()
    {
        if(mToolbar == null)
        {
            mToolbar = (Toolbar) findViewById(R.id.app_bar);

            if(mToolbar != null) {
                setSupportActionBar(mToolbar);
            }
        }

        return mToolbar;
    }

    protected Toolbar activateToolbarWithHomeEnabled()
    {
        activateToolbar();
        if(mToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        return mToolbar;
    }
}
