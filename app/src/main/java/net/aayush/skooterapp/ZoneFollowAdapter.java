package net.aayush.skooterapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;

import net.aayush.skooterapp.data.Zone;

import java.util.ArrayList;
import java.util.List;

public class ZoneFollowAdapter extends ArrayAdapter<Zone>{

    Context mContext;
    int mLayoutResourceId;
    List<Zone> data = new ArrayList<Zone>();

    public ZoneFollowAdapter(Context context, int resource, List<Zone> objects) {
        super(context, resource, objects);
        mContext = context;
        mLayoutResourceId = resource;
        this.data = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(mLayoutResourceId, parent, false);
        }

        Zone zone = data.get(position);

        TextView zoneName = (TextView) convertView.findViewById(R.id.zone_name);
        zoneName.setText(zone.getZoneName());

        final ImageView zoneImage = (ImageView) convertView.findViewById(R.id.zone_icon);
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();

        String url = zone.getZoneImage();

        imageLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if(response.getBitmap() != null) {
                    zoneImage.setImageBitmap(response.getBitmap());
                }
            }
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(error.getMessage());
            }
        });

        final Button button = (Button) convertView.findViewById(R.id.follow_zone_button);
        button.setTag(zone);
        if(zone.getIsFollowing()) {
            button.setText("Following");
            button.setBackground(mContext.getResources().getDrawable(R.drawable.custom_following_button));
            button.setTextColor(mContext.getResources().getColor(R.color.md_white_1000));
        } else {
            button.setText("Follow");
            button.setBackground(mContext.getResources().getDrawable(R.drawable.custom_follow_button));
            button.setTextColor(mContext.getResources().getColor(R.color.md_grey_600));
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Zone zone = (Zone) v.getTag();
                Log.d("ZoneAdapter", zone.toStringCustom());
                if(zone.getIsFollowing()) {
                    //Zone is being followed
                    zone.unfollow();
                    button.setText("Follow");
                    button.setBackground(mContext.getResources().getDrawable(R.drawable.custom_follow_button));
                    button.setTextColor(mContext.getResources().getColor(R.color.md_grey_600));
                } else {
                    zone.follow();
                    button.setText("Following");
                    button.setBackground(mContext.getResources().getDrawable(R.drawable.custom_following_button));
                    button.setTextColor(mContext.getResources().getColor(R.color.md_white_1000));
                }
            }
        });

        return convertView;
    }
}
