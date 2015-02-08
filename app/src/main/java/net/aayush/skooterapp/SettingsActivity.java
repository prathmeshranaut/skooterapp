package net.aayush.skooterapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        activateToolbarWithHomeEnabled();

        ListView settingsList = (ListView) findViewById(R.id.settings_list);

        int[] icons = {
                R.drawable.s_invite_icon,
                R.drawable.s_play_store_icon,
                R.drawable.s_notification,
                R.drawable.s_facebook,
                R.drawable.s_twitter,
                R.drawable.s_instagram,
                R.drawable.s_feedback_icon,
                R.drawable.s_about_icon
        };
        String[] settings = {
                "Invite Friends",
                "Rate us on Play Store",
                "Notifications",
                "Like us on Facebook",
                "Follow us on Twitter",
                "Follow us on Instagram",
                "Send feedback",
                "About Us"
        };
        boolean switchButtons[] = {
                false,
                false,
                true,
                false,
                false,
                false,
                false,
                false,
        };
        SettingsAdapter settingsAdapter = new SettingsAdapter(this, R.layout.list_view_settings, icons, settings, switchButtons);

        settingsList.setAdapter(settingsAdapter);
        settingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //Invite Friends
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.putExtra(Intent.EXTRA_TEXT, "Join me on Skooter!");
                        startActivity(Intent.createChooser(share, "Invite friends on Skooter"));
                        break;
                    case 1:
                        //Rate on play store
                        Uri uri = Uri.parse("market://details?id=" + getPackageName());
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        try {
                            startActivity(goToMarket);
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                        }
                        break;
                    case 2:
                        //Notification

                    case 3:
                        //Facebook
                        String url = "https://www.facebook.com/SkooterApp";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        break;
                    case 4:
                        //Twitter
                        url = "https://twitter.com/SkooterApp";
                        i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        break;
                    case 5:
                        //Instagram
                        url = "http://instagram.com/SkooterApp";
                        i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        break;
                    case 6:
                        //Feedback
                        Intent sendIntent = new Intent(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                        sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"skooter@gmail.com"});
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "PRESET TEXT");
                        sendIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sendIntent, "Send feedback"));

                        break;
                    case 7:
                        //About Us

                        break;
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_settings, menu);
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
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
