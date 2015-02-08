package net.aayush.skooterapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

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
        if(convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(mLayoutResourceId, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.settings_icon);
        TextView textView = (TextView) convertView.findViewById(R.id.settings_text);

        Switch switchButton = (Switch) convertView.findViewById(R.id.switchButton);
        switchButton.setVisibility(View.GONE);

        if(mSwitchButtons[position]) {
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
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean(BaseActivity.SETTINGS_NOTIFICATION, isChecked);
                    editor.commit();

                    //TODO Push to the server

                }
            });
        }

        imageView.setImageResource(mIcons[position]);
        textView.setText(mSettings[position]);

        return convertView;
    }
}
