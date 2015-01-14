package net.aayush.skooterapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import net.aayush.skooterapp.BaseActivity;
import net.aayush.skooterapp.ComposeActivity;
import net.aayush.skooterapp.GetSkootData;
import net.aayush.skooterapp.PostAdapter;
import net.aayush.skooterapp.R;
import net.aayush.skooterapp.ViewPostActivity;
import net.aayush.skooterapp.data.Post;

import java.util.ArrayList;
import java.util.List;

public class Home extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    protected List<Post> mPostsList = new ArrayList<Post>();
    protected ArrayAdapter<Post> mPostsAdapter;
    protected ListView mListPosts;
    protected Context mContext;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected LinearLayout mLinearLayout;

    private static final int STATE_ONSCREEN = 0;
    private static final int STATE_OFFSCREEN = 1;
    private static final int STATE_RETURNING = 2;
    private int mState = STATE_ONSCREEN;
    private int mScrollY;
    private int mMinRawY = 0;

    private View mHeader;
    private TextView mQuickReturnView;
    private View mPlaceHolder;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mHeader = inflater.inflate(R.layout.header, null);
//        mQuickReturnView = (TextView) view.findViewById(R.id.sticky);
//        mPlaceHolder = mHeader.findViewById(R.id.placeholder);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                Log.v("Main Activity", "Refreshed");
            }
        }, 5000);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mLinearLayout.requestFocus();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mLinearLayout.requestFocus();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = container.getContext();

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        int userId = BaseActivity.userId;

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        ProcessPosts processPosts = new ProcessPosts("https://skooter.herokuapp.com/latest/" + userId + ".json");
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

        mListPosts.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
//                        mQuickReturnHeight = mQuickReturnView.getHeight();
//                        mListView.computeScrollY();
//                        mCachedVerticalScrollRange = mListPosts.getListHeight();
                    }
                }
        );
        mListPosts.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                mScrollY = 0;
                int translationY = 0;

            }
        });

        final EditText postSkoot = (EditText) rootView.findViewById(R.id.skootText);
        mLinearLayout = (LinearLayout) rootView.findViewById(R.id.focusLayout);
        mLinearLayout.requestFocus();

        postSkoot.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    Intent intent = new Intent(getActivity(), ComposeActivity.class);
                    startActivity(intent);
                }
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.score);
        menuItem.setTitle(Integer.toString(BaseActivity.mUser.getScore() + 2));
        super.onCreateOptionsMenu(menu,inflater);
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