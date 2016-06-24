package com.skooterapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.localytics.android.LocalyticsActivityLifecycleCallbacks;
import com.localytics.android.LocalyticsAmpSession;
import com.skooterapp.common.view.SlidingTabLayout;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends BaseActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private LocalyticsAmpSession localyticsSession;
    SharedPreferences settings;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    Context context;
    String regid;

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "676255705065";

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

        context = getApplicationContext();

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
                    if (retrieveReferralParams.get("utm_medium") != null) {
                        params.put("entry.1790336789", retrieveReferralParams.get("utm_medium"));
                    }
                    if (retrieveReferralParams.get("utm_term") != null) {
                        params.put("entry.1187582545", retrieveReferralParams.get("utm_term"));
                    }
                    if (retrieveReferralParams.get("utm_content") != null) {
                        params.put("entry.991118102", retrieveReferralParams.get("utm_content"));
                    }
                    if (retrieveReferralParams.get("utm_campaign") != null) {
                        params.put("entry.1555135749", retrieveReferralParams.get("utm_campaign"));
                    }
                    params.put("entry.1526753725", Integer.toString(BaseActivity.userId));
                    if (params != null && !params.isEmpty()) {
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

        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i(LOG_TAG, "No valid Google Play Services APK found.");
        }
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(LOG_TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(LOG_TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void registerInBackground() {
        Log.d(LOG_TAG, "Yo");
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg;

                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the registration ID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                //TODO
            }
        }.execute(null, null, null);
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(LOG_TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private void sendRegistrationIdToBackend() {
        // Your implementation here.
        Log.d(LOG_TAG, "RegId: " + regid);
        Map<String, String> params = new HashMap<>();
        params.put("registration_id", regid);
        params.put("user_id", Integer.toString(BaseActivity.userId));

        String url = BaseActivity.substituteString(getResources().getString(R.string.registration_id), new HashMap<String, String>());

        SkooterJsonObjectRequest skooterJsonObjectRequest = new SkooterJsonObjectRequest(Request.Method.PUT, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //DONE
                Log.d(LOG_TAG, "Sent");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        AppController.getInstance().addToRequestQueue(skooterJsonObjectRequest, "register");
    }

    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the registration ID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
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
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.score) {
            return true;
        }  else if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(LOG_TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
