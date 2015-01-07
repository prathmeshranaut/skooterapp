package net.aayush.skooterapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class LoadingActivity extends BaseActivity {

    public static final int MAX_COOLING_PERIOD = 300000;
    protected SharedPreferences mSettings;
    protected TextView mLoadingTextView;
    protected boolean mStatus = false;
    protected static int mCollingPeriod = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        mLoadingTextView = (TextView) findViewById(R.id.loadingText);
        mSettings = getSharedPreferences(PREFS_NAME, 0);
        userId = mSettings.getInt("userId", 0);

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null && !networkInfo.isConnected()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoadingActivity.this);
            alertDialogBuilder.setMessage("Looks like you aren't connected to the internet! Would you please mind doing so?");
            alertDialogBuilder.setPositiveButton("Ok!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {

                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        if (userId == 0) {
            loginUser();
        } else {
            getUserDetails();
        }
    }

    public void getUserDetails() {
        UserDetails userDetails = new UserDetails("https://skooter.herokuapp.com/user/" + userId + ".json", userId);
        userDetails.execute();
    }

    public void loginUser()
    {
        String androidId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String[] data = {"phone", androidId};

        LoginUser loginUser = new LoginUser("https://skooter.herokuapp.com/user", data);
        loginUser.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_loading, menu);
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

    public class LoginUser extends PostUserLogin {

        public LoginUser(String mRawUrl, String[] postData) {
            super(mRawUrl, postData);
        }

        public void execute() {
            super.execute();
            LoginUserData loginUserData = new LoginUserData();
            loginUserData.execute();
        }

        public class LoginUserData extends PushJsonData {

            protected void onPostExecute(String webData) {
                super.onPostExecute(webData);
                BaseActivity.userId = getUserId();
                if (BaseActivity.userId == 0) {
                    if (getmDownloadStatus() == DownloadStatus.FAILED_OR_EMPTY) {
                        mLoadingTextView.setText("Darn! Looks like we couldn't log you in. Hold on we'll keep trying!");
                    }
                } else {
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putInt("userId", userId);
                    editor.commit();
                    getUserDetails();
                }
            }
        }
    }

    public class UserDetails extends GetUserDetails {
        public UserDetails(String mRawUrl, int mUserId) {
            super(mRawUrl, mUserId);
        }

        public void execute() {
            super.execute();
            ProcessData processData = new ProcessData();
            processData.execute();
        }

        public class ProcessData extends DownloadJsonData {
            protected void onPostExecute(String webData) {
                super.onPostExecute(webData);
                if (getmDownloadStatus() != DownloadStatus.OK) {
                    if (getmDownloadStatus() == DownloadStatus.FAILED_OR_EMPTY) {
                        mLoadingTextView.setText("Darn! Looks like we couldn't log you in. Hold on we'll keep trying!");
                    }
                } else {
                    mUser = getUser();
                    mStatus = true;
                    Intent i = new Intent(LoadingActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }
    }
}
