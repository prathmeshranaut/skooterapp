package net.aayush.skooterapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingsAdapter extends ArrayAdapter<String> {

    private final Context mContext;
    private final int mLayoutResourceId;
    private final int[] mIcons;
    private final String[] mSettings;

    public SettingsAdapter(Context context, int resource, int[] icons, String[] settings) {
        super(context, resource, settings);
        mContext = context;
        mLayoutResourceId = resource;
        mIcons = icons;
        mSettings = settings;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(mLayoutResourceId, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.settings_icon);
        TextView textView = (TextView) convertView.findViewById(R.id.settings_text);

        imageView.setImageResource(mIcons[position]);
        textView.setText(mSettings[position]);

        return convertView;
    }
}
