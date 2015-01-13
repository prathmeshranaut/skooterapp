package net.aayush.skooterapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ComposeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        activateToolbarWithHomeEnabled();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compose, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send) {
            final TextView skootText = (TextView) findViewById(R.id.skootText);
            final TextView skootHandle = (TextView) findViewById(R.id.skootHandle);
            if (skootText.getText().length() > 0 && skootText.getText().length() <= 250) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost httppost = new HttpPost("https://skooter.herokuapp.com/skoot");

                        try {
                            // Add your data
                            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                            nameValuePairs.add(new BasicNameValuePair("user_id", Integer.toString(BaseActivity.userId)));
                            nameValuePairs.add(new BasicNameValuePair("handle", skootHandle.getText().toString()));
                            nameValuePairs.add(new BasicNameValuePair("content", skootText.getText().toString()));
                            nameValuePairs.add(new BasicNameValuePair("zone_id", "1"));
                            nameValuePairs.add(new BasicNameValuePair("location_id", "1"));
                            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                            // Execute HTTP Post Request
                            HttpResponse response = httpclient.execute(httppost);
                            Log.v("Posted Skoot", response.toString());
                        } catch (ClientProtocolException e) {
                            // TODO Auto-generated catch block
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                        }
                    }
                }).start();
                finish();
            } else if (skootText.getText().length() > 250) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("You cannot simply skoot with more than 250! For that you would have login through Facebook.");
                alertDialogBuilder.setPositiveButton("Ok!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("You cannot simply skoot with no content!");
                alertDialogBuilder.setPositiveButton("Ok!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }

            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
