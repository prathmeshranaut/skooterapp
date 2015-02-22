package net.aayush.skooterapp;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aayushranaut on 2/22/15.
 * net.aayush.skooterapp
 */
public class SkooterJsonArrayRequest extends JsonArrayRequest {
    /**
     * Creates a new request.
     *
     * @param url           URL to fetch the JSON from
     * @param listener      Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public SkooterJsonArrayRequest(String url, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();

        if (headers == null || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }

        headers.put("user_id", Integer.toString(BaseActivity.userId));
        headers.put("access_token", BaseActivity.accessToken);

        return headers;
    }
}
