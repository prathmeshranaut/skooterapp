package net.aayush.skooterapp;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class FeedbackActivity extends BaseActivity {
    private static final String LOG_TAG = FeedbackActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        activateToolbarWithHomeEnabled("Feedback");


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feedback, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_feedback) {
            //Upload the data to google forms
            String url = "https://docs.google.com/forms/d/1zz9BQvIuvTd4ZXmtKXyeiAZaXDi3MWkKAUMG8GamoGs/formResponse";

            TextView email = (TextView) findViewById(R.id.email);
            TextView feedback = (TextView) findViewById(R.id.feedback);
            TextView name = (TextView) findViewById(R.id.name);

            Map<String, String> params = new HashMap<String, String>();
            params.put("fbzx", "-2669735696740216817");
            params.put("pageHistory", "0");
            params.put("entry.401753170", feedback.getText().toString());
            params.put("entry.365030282", email.getText().toString());
            params.put("entry.1648041446", name.getText().toString());

            Log.d(LOG_TAG, params.toString());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(LOG_TAG, response.toString());
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(LOG_TAG, error.networkResponse);
                }
            });

            AppController.getInstance().addToRequestQueue(jsonObjectRequest, "feedback");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
