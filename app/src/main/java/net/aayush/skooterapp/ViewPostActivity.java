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

    protected List<Comment> mCommentsList = new ArrayList<Comment>();
    protected ArrayAdapter<Comment> mCommentsAdapter;
    protected ListView mListComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        activateToolbarWithHomeEnabled();

        Intent intent = getIntent();
        Post post = (Post) intent.getSerializableExtra(SKOOTER_POST);
        List<Post> postList = new ArrayList<Post>();
        postList.add(post);

        ArrayAdapter<Post> postAdapter = new PostAdapter(this, R.layout.list_view_post_row, postList);
        ListView listPosts = (ListView) findViewById(R.id.list_posts);
        listPosts.setAdapter(postAdapter);

        //Get the comments via JSON API
        ProcessComments processComments = new ProcessComments("https://skooter.herokuapp.com/skoot/2/" + post.getId() + ".json");
        processComments.execute();

        List<Comment> comments = new CommentData().getComments();
        mCommentsAdapter = new CommentsAdapter(this, R.layout.list_view_post_row, mCommentsList);
        mListComments = (ListView) findViewById(R.id.list_comments);
        mListComments.setAdapter(mCommentsAdapter);

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

    public class ProcessComments extends GetSkootComments {

        public ProcessComments(String mRawUrl) {
            super(mRawUrl);
        }

        public void execute() {
            super.execute();
            ProcessData processData = new ProcessData();
            processData.execute();
        }

        public class ProcessData extends DownloadJsonData {
            protected void onPostExecute(String webData) {
                super.onPostExecute(webData);
                mCommentsList = getComments();
                mCommentsAdapter.addAll(mCommentsList);
                mCommentsAdapter.notifyDataSetChanged();
            }
        }
    }
}
