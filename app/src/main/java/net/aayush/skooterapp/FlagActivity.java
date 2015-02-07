package net.aayush.skooterapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import net.aayush.skooterapp.data.Post;

import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class FlagActivity extends BaseActivity {

    private static final String LOG_TAG = FlagActivity.class.getSimpleName();
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
                String url = "http://skooter.elasticbeanstalk.com/flag/skoot";

                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", Integer.toString(BaseActivity.userId));
                params.put("post_id", Integer.toString(mPost.getId()));
                params.put("type", Integer.toString(spinner.getSelectedItemPosition()));

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(FlagActivity.this, "Post has been successfully flagged", Toast.LENGTH_SHORT).show();
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

                        headers.put("access_token", BaseActivity.accessToken);

                        return headers;
                    }
                };

                AppController.getInstance().addToRequestQueue(jsonObjectRequest, "flag_post");
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
