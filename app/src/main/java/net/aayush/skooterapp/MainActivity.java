package net.aayush.skooterapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import net.aayush.skooterapp.data.Post;
import net.aayush.skooterapp.data.PostData;

import java.util.List;


public class MainActivity extends BaseActivity {

    private List<Post> mPosts = new PostData().getPosts();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activateToolbar();

        ArrayAdapter<Post> mPostsAdapter = new ArrayAdapter<Post>(this, android.R.layout.simple_list_item_1, mPosts);
        ListView listPosts = (ListView) findViewById(R.id.list_posts);
        listPosts.setAdapter(mPostsAdapter);

        listPosts.setOnItemClickListener(new ListView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ViewPostActivity.class);
                intent.putExtra(SKOOTER_POST, mPosts.get(position));
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_compose) {
            Intent intent = new Intent(MainActivity.this, ComposeActivity.class);
            startActivity(intent);

            return true;
        } else if(id == R.id.action_peek) {
            Intent intent = new Intent(MainActivity.this, PeekActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
