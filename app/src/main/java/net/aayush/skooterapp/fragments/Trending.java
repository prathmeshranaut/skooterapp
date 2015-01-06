package net.aayush.skooterapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import net.aayush.skooterapp.BaseActivity;
import net.aayush.skooterapp.GetSkootData;
import net.aayush.skooterapp.PostAdapter;
import net.aayush.skooterapp.R;
import net.aayush.skooterapp.ViewPostActivity;
import net.aayush.skooterapp.data.Post;

import java.util.ArrayList;
import java.util.List;

public class Trending extends Fragment {

    protected List<Post> mPostsList = new ArrayList<Post>();
    protected ArrayAdapter<Post> mPostsAdapter;
    protected ListView mListPosts;
    protected Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = container.getContext();

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        int userId = BaseActivity.userId;

        ProcessPosts processPosts = new ProcessPosts("https://skooter.herokuapp.com/hot/" + userId + ".json");
        processPosts.execute();

        mPostsAdapter = new PostAdapter(mContext, R.layout.list_view_post_row, mPostsList);
        mListPosts = (ListView) rootView.findViewById(R.id.list_posts);
        mListPosts.setAdapter(mPostsAdapter);

        mListPosts.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Context context = view.getContext();

                Intent intent = new Intent(getActivity(), ViewPostActivity.class);
                intent.putExtra(BaseActivity.SKOOTER_POST, mPostsList.get(position));
                startActivity(intent);
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    public class ProcessPosts extends GetSkootData {

        public ProcessPosts(String mRawUrl) {
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
                mPostsList = getPosts();
                mPostsAdapter.addAll(mPostsList);
                mPostsAdapter.notifyDataSetChanged();
            }
        }
    }
}
