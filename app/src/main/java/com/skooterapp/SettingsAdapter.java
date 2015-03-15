package com.skooterapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingsAdapter extends ArrayAdapter<String> {

    private final Context mContext;
    private final int mLayoutResourceId;
    private final int[] mIcons;
    private final String[] mSettings;
    private final boolean[] mSwitchButtons;

    public SettingsAdapter(Context context, int resource, int[] icons, String[] settings, boolean[] switchButtons) {
        super(context, resource, settings);
        mContext = context;
        mLayoutResourceId = resource;
        mIcons = icons;
        mSettings = settings;
        mSwitchButtons = switchButtons;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(mLayoutResourceId, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.settings_icon);
        TextView textView = (TextView) convertView.findViewById(R.id.settings_text);

        Switch switchButton = (Switch) convertView.findViewById(R.id.switchButton);
        switchButton.setVisibility(View.GONE);

        if (mSwitchButtons[position]) {
            //Add the slider
            switchButton = (Switch) convertView.findViewById(R.id.switchButton);
            switchButton.setVisibility(View.VISIBLE);
            final SharedPreferences settings = mContext.getSharedPreferences(BaseActivity.PREFS_NAME, 0);
            boolean notificationSwitch = settings.getBoolean(BaseActivity.SETTINGS_NOTIFICATION, true);
            switchButton.setChecked(notificationSwitch);

            switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //Save the settings locally
                    final boolean isCheckedClone = isChecked;

                    //TODO Push to the server
                    String url = BaseActivity.substituteString(mContext.getResources().getString(R.string.notification_preferences), new HashMap<String, String>());
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_id", Integer.toString(BaseActivity.userId));
                    params.put("notify", Boolean.toString(isChecked));

                    SkooterJsonObjectRequest skooterJsonObjectRequest =
                            new SkooterJsonObjectRequest(Request.Method.PUT,
                                    url, new JSONObject(params),
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            SharedPreferences.Editor editor = settings.edit();
                                            editor.putBoolean(BaseActivity.SETTINGS_NOTIFICATION, isCheckedClone);
                                            editor.commit();
                                            Log.d("SettingsAdapter", response.toString());
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    VolleyLog.d("SettingAdapter", "Error: " + error.getMessage());
                                }
                            });

                    AppController.getInstance().addToRequestQueue(skooterJsonObjectRequest, "notification_preferences");
                }
            });
        }

        imageView.setImageResource(mIcons[position]);
        textView.setText(mSettings[position]);

        return convertView;
    }
}
