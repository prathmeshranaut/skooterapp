package net.aayush.skooterapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import net.aayush.skooterapp.data.Comment;

import java.util.List;


public class MeCommentsActivity extends BaseActivity {

    List<Comment> commentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_comments);

        activateToolbarWithHomeEnabled("My Replies");

        ListView listView = (ListView) findViewById(R.id.list_posts);

        commentsList = BaseActivity.mUser.getComments();
        if (commentsList.isEmpty()) {
            Toast.makeText(getApplicationContext(), "You don't have any comments yet!", Toast.LENGTH_SHORT).show();
        } else {
            listView.setAdapter(new CommentsAdapter(this, R.layout.list_view_comment_post_row, commentsList, true));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MeCommentsActivity.this, ViewPostActivity.class);
                    Log.d("Comments", Integer.toString(commentsList.get(position).getPostId()));
                    intent.putExtra(SKOOTER_POST_ID, commentsList.get(position).getPostId());
                    startActivity(intent);
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_me_comments, menu);
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
