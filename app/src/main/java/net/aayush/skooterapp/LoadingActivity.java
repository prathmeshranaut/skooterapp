package net.aayush.skooterapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;


public class LoadingActivity extends BaseActivity {

    protected SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        mSettings = getSharedPreferences(PREFS_NAME, 0);
        userId = mSettings.getInt("userId", 0);

        if (userId == 0) {
            String androidId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            String[] data = {"login", androidId};

            LoginUser loginUser = new LoginUser("https://skooter.herokuapp.com/user", data);
            loginUser.execute();
        } else {
            getUserDetails();
        }
    }

    public void getUserDetails()
    {
        UserDetails userDetails = new UserDetails("https://skooter.herokuapp.com/user/"+userId+".json", userId);
        userDetails.execute();
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
                userId = getUserId();

                SharedPreferences.Editor editor = mSettings.edit();
                editor.putInt("userId", userId);
                editor.commit();
                getUserDetails();
            }
        }
    }

    public class UserDetails extends GetUserDetails {
        public UserDetails(String mRawUrl, int mUserId) {
            super(mRawUrl, mUserId);
        }

        public void execute()
        {
            super.execute();
            ProcessData processData = new ProcessData();
            processData.execute();
        }

        public class ProcessData extends GetUserDetails.DownloadJsonData {
            protected void onPostExecute(String webData) {
                super.onPostExecute(webData);
                mUser = getUser();
                Intent i = new Intent(LoadingActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }
    }
}
