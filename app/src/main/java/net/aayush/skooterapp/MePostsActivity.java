package net.aayush.skooterapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import net.aayush.skooterapp.data.Post;

import java.util.List;


public class MePostsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_posts);

        activateToolbarWithHomeEnabled();

        ListView listView = (ListView) findViewById(R.id.list_posts);

        final List<Post> postList = BaseActivity.mUser.getSkoots();
        if(postList.isEmpty()) {
            Toast.makeText(getApplicationContext(), "You don't have any skoots yet!", Toast.LENGTH_SHORT).show();
        } else {
            listView.setAdapter(new PostAdapter(this, R.layout.list_view_post_row, postList, true));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MePostsActivity.this, ViewPostActivity.class);
                    intent.putExtra(BaseActivity.SKOOTER_POST, postList.get(position));
                    startActivity(intent);
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_me_posts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
