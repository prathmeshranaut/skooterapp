package com.skooterapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.skooterapp.data.ZoneNew;

import java.util.ArrayList;
import java.util.List;

public class PeekNewPostAdapter extends ArrayAdapter<ZoneNew> {

    protected Context mContext;
    protected int mLayoutResourceId;
    protected List<ZoneNew> data = new ArrayList<ZoneNew>();

    public PeekNewPostAdapter(Context context, int resource, List<ZoneNew> objects) {
        super(context, resource, objects);
        mContext = context;
        mLayoutResourceId = resource;
        this.data = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(mLayoutResourceId, parent, false);
        }

        final ZoneNew zone = data.get(position);

        final NetworkImageView zoneBackgroundImage =
                (NetworkImageView) convertView.findViewById(R.id.zone_header_image);
        final NetworkImageView zoneLogo =
                (NetworkImageView) convertView.findViewById(R.id.zone_logo);
        final TextView zoneName = (TextView) convertView.findViewById(R.id.zone_name);

        zoneBackgroundImage.setImageUrl(zone.getZoneBackground(), AppController.getInstance().getImageLoader());
        zoneLogo.setImageUrl(zone.getZoneImage(), AppController.getInstance().getImageLoader());
        zoneName.setText(zone.getName());

        return convertView;
    }

    @Override
    public int getCount() {
        return (null != data ? data.size() : 0);
    }
}
