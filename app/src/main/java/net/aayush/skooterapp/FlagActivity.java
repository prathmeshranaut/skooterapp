package net.aayush.skooterapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import net.aayush.skooterapp.data.Post;

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


public class FlagActivity extends BaseActivity {

    protected Post mPost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flag);
        activateToolbarWithHomeEnabled();

        Intent intent = getIntent();
        mPost = (Post) intent.getSerializableExtra(BaseActivity.SKOOTER_POST);

        final Spinner spinner = (Spinner) findViewById(R.id.flagSpinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button btnFlagPost = (Button) findViewById(R.id.btnFlagPost);
        btnFlagPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost httppost = new HttpPost("https://skooter.herokuapp.com/flag/skoot");

                        try {
                            // Add your data
                            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                            nameValuePairs.add(new BasicNameValuePair("user_id", Integer.toString(BaseActivity.userId)));
                            nameValuePairs.add(new BasicNameValuePair("post_id", Integer.toString(mPost.getId())));
                            nameValuePairs.add(new BasicNameValuePair("type", Integer.toString(spinner.getSelectedItemPosition() + 1)));
                            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                            // Execute HTTP Post Request
                            HttpResponse response = httpclient.execute(httppost);
                            Log.v("Flag Activity", response.toString());
                        } catch (ClientProtocolException e) {
                            // TODO Auto-generated catch block
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                        }
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(), "Post has been successfully flagged", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }).start();
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_flag, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
