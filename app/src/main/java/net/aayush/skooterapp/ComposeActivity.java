package net.aayush.skooterapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class ComposeActivity extends BaseActivity {

    protected static final String LOG_TAG = ComposeActivity.class.getSimpleName();

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
                String url = BaseActivity.substituteString(getResources().getString(R.string.skoot_new), new HashMap<String, String>());

                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", Integer.toString(BaseActivity.userId));
                params.put("channel", skootHandle.getText().toString());
                params.put("content", skootText.getText().toString());
                params.put("location_id", Integer.toString(BaseActivity.locationId));
                params.put("zone_id", "1");

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LOG_TAG, response.toString());
                        skootText.setText("");
                        skootHandle.setText("");
                        Toast.makeText(ComposeActivity.this, "Woot! Skoot posted!", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(LOG_TAG, "Error: " + error.getMessage());
                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = super.getHeaders();

                        if (headers == null
                                || headers.equals(Collections.emptyMap())) {
                            headers = new HashMap<String, String>();
                        }

                        headers.put("user_id", Integer.toString(BaseActivity.userId));
                        headers.put("access_token", BaseActivity.accessToken);

                        return headers;
                    }
                };

                AppController.getInstance().addToRequestQueue(jsonObjectRequest, "compose_skoot");
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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return super.onKeyDown(keyCode, event);
    }
}
