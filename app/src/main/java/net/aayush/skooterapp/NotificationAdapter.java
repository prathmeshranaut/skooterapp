package net.aayush.skooterapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;

import net.aayush.skooterapp.data.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends ArrayAdapter<Notification> {

    Context mContext;
    int mLayoutResourceId;
    List<Notification> data = new ArrayList<Notification>();

    public NotificationAdapter(Context context, int resource, List<Notification> objects) {
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

        Notification notification = data.get(position);

        final ImageView imageView = (ImageView) convertView.findViewById(R.id.notification_icon);
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();

        String url = notification.getIconUrl();

        imageLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if(response.getBitmap() != null) {
                    imageView.setImageBitmap(response.getBitmap());
                }
            }
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(error.getMessage());
            }
        });

        TextView textView = (TextView) convertView.findViewById(R.id.notification_text);
        textView.setText(notification.getText());

        return convertView;
    }

    @Override
    public int getCount() {
        return (null != data ? data.size() : 0);
    }
}
