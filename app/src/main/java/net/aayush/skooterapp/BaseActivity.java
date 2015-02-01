package net.aayush.skooterapp;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import net.aayush.skooterapp.data.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

public class BaseActivity extends ActionBarActivity {
    private Toolbar mToolbar;
    public static int userId;
    public static User mUser;

    public static final String SKOOTER_POST = "SKOOTER_POST";
    public static final String PREFS_NAME = "Skooter";

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

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

        final long diff = now - t;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Yesterday";
        } else {
            return diff / DAY_MILLIS + " days";
        }
    }

    protected Toolbar activateToolbar() {
        if (mToolbar == null) {
            mToolbar = (Toolbar) findViewById(R.id.app_bar);

            if (mToolbar != null) {
                setSupportActionBar(mToolbar);
                mToolbar.setLogo(R.drawable.ic_launcher);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
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
