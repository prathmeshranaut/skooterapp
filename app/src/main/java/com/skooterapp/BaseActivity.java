package com.skooterapp;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.skooterapp.data.Post;
import com.skooterapp.data.User;
import com.skooterapp.data.Zone;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class BaseActivity extends ActionBarActivity {
    private Toolbar mToolbar;
    public static int userId;
    public static String accessToken;
    public static int locationId;
    public static User mUser;
    public static List<Zone> mActiveZones = new ArrayList<Zone>();
    public static List<Post> mHomePosts = new ArrayList<Post>();

    public static final String SKOOTER_POST = "SKOOTER_POST";
    public static final String SKOOTER_POST_ID = "SKOOTER_POST_ID";
    public static final String PREFS_NAME = "Skooter";
    public static final String SETTINGS_NOTIFICATION = "notificationSettings";

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    private static final int SECONDS = 1;
    private static final int MINUTES = 60 * SECONDS;
    private static final int HOURS = 60 * MINUTES;
    private static final int DAYS = 24 * HOURS;
    private static final int MONTHS = 30 * DAYS;
    private static final int YEARS = 365 * DAYS;

    public static GPSLocator mLocator;

    public static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    public static String substituteString(String template, Map<String, String> substitutions) {
        String result = AppController.getInstance().getResources().getString(R.string.main_url) + template;
        for (Map.Entry<String, String> substitution : substitutions.entrySet()) {
            String pattern = "{" + substitution.getKey() + "}";
            result = result.replace(pattern, substitution.getValue());
        }
        return result;
    }

    public static String getTimeAgo(String time) {
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        long t = 0;
        try {
            Date date = formatter.parse(time.substring(0, 24));
            t = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (t < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            t *= 1000;
        }

        Date curDate = currentDate();
        long now = curDate.getTime();

        if (t > now || t <= 0) {
            return null;
        }

        long diff = now - t;
        diff /= SECOND_MILLIS;
        if (diff < MINUTES) {
            return "1min";
        } else if (diff < HOURS) {
            return diff / MINUTES + "min";
        } else if (diff < DAYS) {
            return diff / HOURS + "h";
        } else if (diff < MONTHS) {
            return diff / DAYS + "d";
        } else if (diff < YEARS) {
            return diff / MONTHS + "m";
        } else {
            return diff / YEARS + "y";
        }
    }

    protected Toolbar activateToolbar() {
        if (mToolbar == null) {
            mToolbar = (Toolbar) findViewById(R.id.app_bar);

            if (mToolbar != null) {
                setSupportActionBar(mToolbar);
                mToolbar.setLogo(R.drawable.ic_launcher_with_padding);
                getSupportActionBar().setTitle("Skooter");
                getSupportActionBar().setDisplayShowTitleEnabled(true);
            }
        }

        return mToolbar;
    }

    protected Toolbar activateToolbarWithHomeEnabled() {
        activateToolbar();
        if (mToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Skooter");
        }
        return mToolbar;
    }

    protected Toolbar activateToolbarWithHomeEnabled(String title) {
        activateToolbar();
        if (mToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title);
        }
        return mToolbar;
    }

    protected int getUserId() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        int userId = settings.getInt("userId", 0);

        return userId;
    }
}
