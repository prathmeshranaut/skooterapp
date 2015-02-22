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
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

            final TextView email = (TextView) findViewById(R.id.email);
            final TextView feedback = (TextView) findViewById(R.id.feedback);
            final TextView name = (TextView) findViewById(R.id.name);

            Map<String, String> params = new HashMap<String, String>();
            params.put("fbzx", "-2669735696740216817");
            params.put("pageHistory", "0");
            params.put("entry.401753170", feedback.getText().toString());
            params.put("entry.365030282", email.getText().toString());
            params.put("entry.1648041446", name.getText().toString());

            Log.d(LOG_TAG, params.toString());
            final StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(LOG_TAG, response.substring(1700));
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    VolleyLog.d(LOG_TAG, error.getStackTrace());
                }
            }) {
                public byte[] getBody() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("fbzx", "-2669735696740216817");
                    params.put("pageHistory", "0");
                    params.put("entry.401753170", feedback.getText().toString());
                    params.put("entry.365030282", email.getText().toString());
                    params.put("entry.1648041446", name.getText().toString());

                    if (params != null && params.size() > 0) {
                        return encodeParameters(params, getParamsEncoding());
                    }
                    return null;
                }


                protected byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
                    StringBuilder encodedParams = new StringBuilder();
                    try {
                        for (Map.Entry<String, String> entry : params.entrySet()) {
                            encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                            encodedParams.append('=');
                            encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                            encodedParams.append('&');
                        }
                        return encodedParams.toString().getBytes(paramsEncoding);
                    } catch (UnsupportedEncodingException uee) {
                        throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
                    }
                }
            };
            AppController.getInstance().addToRequestQueue(jsonObjectRequest, "feedback");
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
