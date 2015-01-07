package net.aayush.skooterapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import net.aayush.skooterapp.data.Comment;
import net.aayush.skooterapp.data.CommentData;
import net.aayush.skooterapp.data.Post;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
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
        ProcessComments processComments = new ProcessComments("https://skooter.herokuapp.com/skoot/" + getUserId() + "/" + post.getId() + ".json", post.getId());
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

        Button commentBtn = (Button) findViewById(R.id.commentSkoot);
        final TextView commentText = (TextView) findViewById(R.id.commentText);
        final TextView commentHandle = (TextView) findViewById(R.id.commentHandle);
        final int postId = post.getId();

        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commentText.getText().length() > 0 && commentText.getText().length() < 250) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HttpClient httpclient = new DefaultHttpClient();
                            HttpPost httppost = new HttpPost("https://skooter.herokuapp.com/comment");

                            try {
                                // Add your data
                                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
                                nameValuePairs.add(new BasicNameValuePair("user_id", Integer.toString(BaseActivity.userId)));
                                nameValuePairs.add(new BasicNameValuePair("handle", commentHandle.getText().toString()));
                                nameValuePairs.add(new BasicNameValuePair("content", commentText.getText().toString()));
                                nameValuePairs.add(new BasicNameValuePair("location_id", "1"));
                                nameValuePairs.add(new BasicNameValuePair("post_id", Integer.toString(postId)));
                                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                                // Execute HTTP Post Request
                                HttpResponse response = httpclient.execute(httppost);
                                Log.v("Posted Skoot Comment", response.toString());
                            } catch (ClientProtocolException e) {
                                // TODO Auto-generated catch block
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                            }
                        }
                    }).start();
                } else if (commentText.getText().length() > 250) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ViewPostActivity.this);
                    alertDialogBuilder.setMessage("You cannot simply skoot with more than 250! For that you would have login through Facebook.");
                    alertDialogBuilder.setPositiveButton("Ok!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ViewPostActivity.this);
                    alertDialogBuilder.setMessage("You cannot simply skoot with no content!");
                    alertDialogBuilder.setPositiveButton("Ok!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
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

        public ProcessComments(String mRawUrl, int mPostId) {
            super(mRawUrl, mPostId);
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
