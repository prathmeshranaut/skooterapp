package net.aayush.skooterapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import net.aayush.skooterapp.data.Post;
import net.aayush.skooterapp.data.Zone;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class ComposeActivity extends BaseActivity {

    protected static final String LOG_TAG = ComposeActivity.class.getSimpleName();
    private Menu mMenu;
    private final static int MAX_CHARACTERS = 200;
    protected Zone mCurrentZone;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_BROWSE_IMAGE_REQUEST_CODE = 200;
    private static final int CAMERA_BROWSE_IMAGE_REQUEST_CODE_KITKAT = 201;

    private static final int MEDIA_TYPE_IMAGE = 1;

    private static final String IMAGE_DIRECTORY_NAME = "Skooter";

    private static ImageView imagePreview;

    private Uri fileUri;
    private File mFile;

    TextView skootText;
    TextView skootHandle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        activateToolbarWithHomeEnabled("");

        EditText editText = (EditText) findViewById(R.id.skootText);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_CHARACTERS)});

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                MenuItem menuItem = mMenu.findItem(R.id.text_counter);
                //TODO Redmi crash
                menuItem.setTitle(Integer.toString(MAX_CHARACTERS - s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        final ImageView imageView = (ImageView) findViewById(R.id.location_icon);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String locationId = (String) imageView.getTag();
//                if (locationId.equals("1")) {
//                    imageView.setImageResource(R.drawable.location_icon);
//                    imageView.setTag("0");
//                } else {
//                    imageView.setImageResource(R.drawable.location_icon_filled);
//                    imageView.setTag("1");
//                }
//
//            }
//        });

        ImageView imageSelectIcon = (ImageView) findViewById(R.id.camera_icon);
        imageSelectIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the camera app
                captureImage();
            }
        });
        if(!isDeviceSupportCamera()) {
            imageSelectIcon.setVisibility(View.GONE);
        }

        ImageView imageBrowse = (ImageView) findViewById(R.id.image_icon);
        imageBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browseImages();
            }
        });

        imagePreview = (ImageView) findViewById(R.id.image_preview);

        calculateActiveZone();
    }

    public void browseImages() {
        if (Build.VERSION.SDK_INT < 19) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(
                    Intent.createChooser(intent, "Select Picture"),
                    CAMERA_BROWSE_IMAGE_REQUEST_CODE);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, CAMERA_BROWSE_IMAGE_REQUEST_CODE_KITKAT);

        }
    }

    private boolean isDeviceSupportCamera() {
        return getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            //Successfully captured the image
            previewCapturedImage();
        } else if (requestCode == CAMERA_BROWSE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            fileUri = data.getData();

            String realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());
            mFile = new File(realPath);
            Uri uriFromPath = Uri.fromFile(mFile);
            fileUri = uriFromPath;
            imagePreview.setVisibility(View.VISIBLE);
            imagePreview.setImageURI(uriFromPath);
        } else if (requestCode == CAMERA_BROWSE_IMAGE_REQUEST_CODE_KITKAT && resultCode == RESULT_OK) {
            fileUri = data.getData();

            String realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());
            mFile = new File(realPath);
            Uri uriFromPath = Uri.fromFile(mFile);
            fileUri = uriFromPath;
            imagePreview.setVisibility(View.VISIBLE);
            imagePreview.setImageURI(uriFromPath);
        } else if (resultCode == RESULT_CANCELED) {

        } else {
            //Failed to capture image
            Toast.makeText(getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
        }
    }

    private void previewCapturedImage() {
        try {
            imagePreview.setVisibility(View.VISIBLE);

            BitmapFactory.Options options = new BitmapFactory.Options();

            //Downsize the image
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
            imagePreview.setImageBitmap(bitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public Uri getOutputMediaFileUri(int type) {
        mFile = getOutputMediaFile(type);
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Failed to create " + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        File mediaFile;

        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    private void calculateActiveZone() {
        TextView activeZone = (TextView) findViewById(R.id.zone);
        String text = "Active Zone: ";
        if (BaseActivity.mActiveZones.size() > 0) {
            for (Zone zone : BaseActivity.mActiveZones) {
                text += zone.getZoneName() + ", ";
            }
            activeZone.setText(text.substring(0, text.length() - 2));
        } else {
            activeZone.setText("Active Zone: None");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.mMenu = menu;
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
            skootText = (TextView) findViewById(R.id.skootText);
            skootHandle = (TextView) findViewById(R.id.skootHandle);

            if (skootText.getText().length() > 0 && skootText.getText().length() <= 250) {
                String url = BaseActivity.substituteString(getResources().getString(R.string.skoot_new), new HashMap<String, String>());

                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", Integer.toString(BaseActivity.userId));
                params.put("channel", skootHandle.getText().toString());
                params.put("content", skootText.getText().toString());
                params.put("location_id", Integer.toString(BaseActivity.locationId));
                if (mActiveZones.size() > 0) {
                    params.put("zone_id", Integer.toString(mActiveZones.get(0).getZoneId()));
                } else {
                    params.put("zone_id", "null'");
                }

                if (fileUri != null) {
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                Looper.prepare();
                                new PostFile().main();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    Toast.makeText(ComposeActivity.this, "Posting skoot...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ComposeActivity.this, "Posting skoot...", Toast.LENGTH_SHORT).show();
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(LOG_TAG, response.toString());

                            parsePost(response);

                            skootText.setText("");
                            skootHandle.setText("");

                            Toast.makeText(ComposeActivity.this, "Woot! Skoot posted!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            finish();
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

                            headers.put("user_id", Integer.toString(BaseActivity.userId));
                            headers.put("access_token", BaseActivity.accessToken);

                            return headers;
                        }
                    };
                    AppController.getInstance().addToRequestQueue(jsonObjectRequest, "compose_skoot");
                }
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
        } else if (id == android.R.id.home)

        {
            finish();
            return true;
        }

        return super.

                onOptionsItemSelected(item);
    }

    public class PostFile {
        public void main() throws Exception {
            HttpParams params = new BasicHttpParams();
            params.setParameter("user_id", Integer.toString(BaseActivity.userId));
            params.setParameter("channel", skootHandle.getText().toString());
            params.setParameter("content", skootText.getText().toString());
            params.setParameter("location_id", Integer.toString(BaseActivity.locationId));
            if (mActiveZones.size() > 0) {
                params.setParameter("zone_id", Integer.toString(mActiveZones.get(0).getZoneId()));
            } else {
                params.setParameter("zone_id", "null'");
            }

            Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream); //compress to which format you want.
            byte[] byte_arr = stream.toByteArray();
            String image_str = Base64.encodeToString(byte_arr, Base64.DEFAULT);
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            nameValuePairs.add(new BasicNameValuePair("image", image_str));
            nameValuePairs.add(new BasicNameValuePair("user_id", Integer.toString(BaseActivity.userId)));
            nameValuePairs.add(new BasicNameValuePair("channel", skootHandle.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("content", skootText.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("location_id", Integer.toString(BaseActivity.locationId)));
            if (mActiveZones.size() > 0) {
                nameValuePairs.add(new BasicNameValuePair("zone_id", Integer.toString(mActiveZones.get(0).getZoneId())));
            } else {
                nameValuePairs.add(new BasicNameValuePair("zone_id", "null"));
            }

            try {

                HttpClient httpclient = new DefaultHttpClient();
                httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

                String url = BaseActivity.substituteString(getResources().getString(R.string.skoot_new), new HashMap<String, String>());
                HttpPost httppost = new HttpPost(url);

                httppost.setHeader("user_id", Integer.toString(BaseActivity.userId));
                httppost.setHeader("access_token", BaseActivity.accessToken);

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);

                parsePost(new JSONObject(EntityUtils.toString(response.getEntity())));

                Log.d("Done", Integer.toString(response.getStatusLine().getStatusCode()));
                Toast.makeText(ComposeActivity.this, "Woot! Skoot posted!", Toast.LENGTH_SHORT).show();
                finish();
            } catch (Exception e) {
                Log.d("Error:", e.getMessage());
                System.out.println("Error in http connection " + e.toString());
            }
        }
    }

    private void parsePost(JSONObject response) {
        final String SKOOTS = "skoot";

        try {
            Post postObject = Post.parsePostFromJSONObject(response.getJSONObject(SKOOTS));
            BaseActivity.mHomePosts.add(0, postObject);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Error processing Json Data");
        }
    }
}