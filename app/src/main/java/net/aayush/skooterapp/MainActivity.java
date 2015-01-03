package net.aayush.skooterapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import net.aayush.skooterapp.data.Post;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity {

    protected List<Post> mPostsList = new ArrayList<Post>();
    protected ArrayAdapter<Post> mPostsAdapter;
    protected ListView mListPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activateToolbar();

        //Check if the user has registered
        int userId = getUserId();
        Log.v("Main Activity", "User ID: "+userId);

        ProcessPosts processPosts = new ProcessPosts("https://skooter.herokuapp.com/latest/"+ userId +".json");
        processPosts.execute();

        mPostsAdapter = new PostAdapter(this, R.layout.list_view_post_row, mPostsList);
        mListPosts = (ListView) findViewById(R.id.list_posts);
        mListPosts.setAdapter(mPostsAdapter);

        mListPosts.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Context context = view.getContext();

                Intent intent = new Intent(MainActivity.this, ViewPostActivity.class);
                intent.putExtra(SKOOTER_POST, mPostsList.get(position));
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
        Log.v("Main Activity", mPostsList.toString());
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

    public class ProcessPosts extends GetSkootData {

        public ProcessPosts(String mRawUrl) {
            super(mRawUrl);
        }

        public void execute()
        {
            super.execute();
            ProcessData processData = new ProcessData();
            processData.execute();
        }

        public class ProcessData extends DownloadJsonData {
            protected void onPostExecute(String webData) {
                super.onPostExecute(webData);
                mPostsList = getPosts();
                mPostsAdapter.addAll(mPostsList);
                mPostsAdapter.notifyDataSetChanged();
            }
        }
    }
}
