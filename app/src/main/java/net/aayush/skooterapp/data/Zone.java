package net.aayush.skooterapp.data;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import net.aayush.skooterapp.AppController;
import net.aayush.skooterapp.BaseActivity;
import net.aayush.skooterapp.R;

import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Zone {
    private static final String LOG_TAG = Zone.class.getSimpleName();

    private int mZoneId;
    private String mZoneName;
    private float mLatitudeMinimum;
    private float mLongitudeMinimum;
    private float mLatitudeMaximum;
    private float mLongitudeMaximum;
    private boolean mIsFollowing;

    public Zone(int zoneId, String zoneName, float latitudeMinimum, float longitudeMinimum, float latitudeMaximum, float longitudeMaximum, boolean isFollowing) {
        mZoneId = zoneId;
        mZoneName = zoneName;
        mLatitudeMinimum = latitudeMinimum;
        mLongitudeMinimum = longitudeMinimum;
        mLatitudeMaximum = latitudeMaximum;
        mLongitudeMaximum = longitudeMaximum;
        mIsFollowing = isFollowing;
    }

    public Zone() {
        mZoneId = 0;
        mZoneName = "";
    }

    public Zone(String zoneName, boolean isFollowing) {
        mZoneName = zoneName;
        mIsFollowing = isFollowing;
    }

    public int getZoneId() {
        return mZoneId;
    }

    public void setZoneId(int zoneId) {
        mZoneId = zoneId;
    }

    public String getZoneName() {
        return mZoneName;
    }

    public float getLatitudeMinimum() {
        return mLatitudeMinimum;
    }

    public void setLatitudeMinimum(float latitudeMinimum) {
        mLatitudeMinimum = latitudeMinimum;
    }

    public float getLongitudeMinimum() {
        return mLongitudeMinimum;
    }

    public void setLongitudeMinimum(float longitudeMinimum) {
        mLongitudeMinimum = longitudeMinimum;
    }

    public float getLatitudeMaximum() {
        return mLatitudeMaximum;
    }

    public void setLatitudeMaximum(float latitudeMaximum) {
        mLatitudeMaximum = latitudeMaximum;
    }

    public float getLongitudeMaximum() {
        return mLongitudeMaximum;
    }

    public void setLongitudeMaximum(float longitudeMaximum) {
        mLongitudeMaximum = longitudeMaximum;
    }

    public void setZoneName(String zoneName) {
        mZoneName = zoneName;
    }

    public boolean getIsFollowing() {
        return mIsFollowing;
    }

    public void setIsFollowing(boolean isFollowing) {
        mIsFollowing = isFollowing;
    }

    public void unfollow() {
        String url = BaseActivity.substituteString(AppController.getInstance().getResources().getString(R.string.zones_unfollow), new HashMap<String, String>());

        ZoneDataHandler zoneDataHandler = new ZoneDataHandler(AppController.getInstance().getBaseContext());
        zoneDataHandler.unFollowZoneById(getZoneId());
        setIsFollowing(false);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v(LOG_TAG, "UnFollow" + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(LOG_TAG, "Error: " + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();

                if (headers == null
                        || headers.equals(Collections.emptyMap())) {
                    headers = new HashMap<String, String>();
                }

                headers.put("user_id", Integer.toString(BaseActivity.userId));
                headers.put("zone_id", Integer.toString(getZoneId()));

                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(jsonObjectRequest, "unfollow_zone");
    }

    public void follow() {
        String url = BaseActivity.substituteString(AppController.getInstance().getResources().getString(R.string.zones_follow), new HashMap<String, String>());

        ZoneDataHandler zoneDataHandler = new ZoneDataHandler(AppController.getInstance().getBaseContext());
        zoneDataHandler.followZoneById(getZoneId());
        setIsFollowing(true);

        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", Integer.toString(BaseActivity.userId));
        params.put("zone_id", Integer.toString(getZoneId()));

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v(LOG_TAG, "Follow" + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(LOG_TAG, "Error: " + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();

                if (headers == null
                        || headers.equals(Collections.emptyMap())) {
                    headers = new HashMap<String, String>();
                }

                headers.put("user_id", Integer.toString(BaseActivity.userId));
                headers.put("access_token", BaseActivity.accessToken);

                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(jsonObjectRequest, "follow_zone");
    }

    @Override
    public String toString() {
        return mZoneName;
    }
}
