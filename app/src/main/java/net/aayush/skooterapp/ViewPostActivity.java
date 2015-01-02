package net.aayush.skooterapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import net.aayush.skooterapp.data.Comment;
import net.aayush.skooterapp.data.CommentData;
import net.aayush.skooterapp.data.Post;

import java.util.ArrayList;
import java.util.List;


public class ViewPostActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        activateToolbarWithHomeEnabled();

        Intent intent = getIntent();
        Post post = (Post) intent.getSerializableExtra(SKOOTER_POST);
        List<Post> postList = new ArrayList<Post>();
        postList.add(post);

        ArrayAdapter<Post> postAdapter = new ArrayAdapter<Post>(this, android.R.layout.simple_list_item_1, postList);
        ListView listPosts = (ListView) findViewById(R.id.list_posts);
        listPosts.setAdapter(postAdapter);


        //Get the comments via JSON API
        List<Comment> comments = new CommentData().getComments();
        ArrayAdapter<Comment> commentAdapter = new ArrayAdapter<Comment>(this, android.R.layout.simple_list_item_1, comments);

        ListView listComments = (ListView) findViewById(R.id.list_comments);
        listComments.setAdapter(commentAdapter);

        //Setup the item click listeners
        listPosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ViewPostActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_post, menu);
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
}
