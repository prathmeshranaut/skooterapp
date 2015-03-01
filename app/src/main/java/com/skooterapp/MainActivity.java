package com.skooterapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.localytics.android.LocalyticsActivityLifecycleCallbacks;
import com.localytics.android.LocalyticsAmpSession;
import com.skooterapp.common.view.SlidingTabLayout;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private LocalyticsAmpSession localyticsSession;
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check if the user is opening the app the first time
        settings = getSharedPreferences(PREFS_NAME, 0);
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

        // Send the install info
        int install_referral = settings.getInt("install_referral", 0);
        if (install_referral == 1) {
            final Map<String, String> retrieveReferralParams = InstallReferrerReceiver.retrieveReferralParams(this);

            String url = getResources().getString(R.string.install_referral);

            Log.d("MainActivity", retrieveReferralParams.toString());


            StringRequest skooterJsonObjectRequest = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Clean the existing data
                            InstallReferrerReceiver.cleanReferralParams(MainActivity.this);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putInt("install_referral", 0);
                            editor.apply();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {
                public byte[] getBody() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("fbzx", "-335929672963535987");
                    params.put("pageHistory", "0");
                    params.put("entry.628117443", retrieveReferralParams.get("utm_source"));
                    params.put("entry.1790336789", retrieveReferralParams.get("utm_medium"));
                    params.put("entry.1187582545", retrieveReferralParams.get("utm_term"));
                    params.put("entry.991118102", retrieveReferralParams.get("utm_content"));
                    params.put("entry.1555135749", retrieveReferralParams.get("utm_campaign"));
                    params.put("entry.1526753725", Integer.toString(BaseActivity.userId));

                    if (params != null && params.size() > 0) {
                        return encodeParameters(params, getParamsEncoding());
                    }
                    return null;
                }


                protected byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
                    StringBuilder encodedParams = new StringBuilder();
                    try {
                        for (Map.Entry<String, String> entry : params.entrySet()) {
                            encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                            encodedParams.append('=');
                            encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                            encodedParams.append('&');
                        }
                        return encodedParams.toString().getBytes(paramsEncoding);
                    } catch (UnsupportedEncodingException uee) {
                        throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
                    }
                }
            };
            ;

            AppController.getInstance().addToRequestQueue(skooterJsonObjectRequest, "install_referral");
        }
    }

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
