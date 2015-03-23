package com.skooterapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aayushranaut on 3/1/15.
 * com.skooterapp
 */
public class InstallReferrerReceiver extends BroadcastReceiver {

    private final static String[] EXPECTED_PARAMETERS = {
            "utm_source",
            "utm_medium",
            "utm_term",
            "utm_content",
            "utm_campaign"
    };

    @Override
    public void onReceive(Context context, Intent intent) {

        //TODO Send data to the server
        Bundle extras = intent.getExtras();
        String referrer = extras.getString("referrer");
        Map<String, String> referralParams = new HashMap<String, String>();

        if (referrer == null || referrer.length() == 0) {
            return;
        }

        try {
            referrer = URLDecoder.decode(referrer, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String[] params = referrer.split("&");
        for (String param : params) {
            String[] pair = param.split("=");
            referralParams.put(pair[0], pair[1]);
        }

        Log.d("referrer", referralParams.toString());
        InstallReferrerReceiver.storeReferralParams(context, referralParams);
    }

    private static void storeReferralParams(Context context, Map<String, String> params)
    {
        SharedPreferences storage = context.getSharedPreferences(BaseActivity.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = storage.edit();

        for(String key : InstallReferrerReceiver.EXPECTED_PARAMETERS)
        {
            String value = params.get(key);
            if(value != null)
            {
                editor.putString(key, value);
            }
        }
        editor.putInt("install_referral", 1);

        editor.commit();
    }

    public static Map<String, String> retrieveReferralParams(Context context)
    {
        HashMap<String, String> params = new HashMap<String, String>();
        SharedPreferences storage = context.getSharedPreferences(BaseActivity.PREFS_NAME, Context.MODE_PRIVATE);

        for(String key : InstallReferrerReceiver.EXPECTED_PARAMETERS)
        {
            String value = storage.getString(key, null);
            if(value != null)
            {
                params.put(key, value);
            }
        }
        return params;
    }

    public static void cleanReferralParams(Context context){

    }

}
