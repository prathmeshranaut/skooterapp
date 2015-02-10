package net.aayush.skooterapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import net.aayush.skooterapp.AppController;
import net.aayush.skooterapp.BaseActivity;
import net.aayush.skooterapp.ComposeActivity;
import net.aayush.skooterapp.PostAdapter;
import net.aayush.skooterapp.R;
import net.aayush.skooterapp.ViewPostActivity;
import net.aayush.skooterapp.data.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String LOG_TAG = Home.class.getSimpleName();
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
    protected View mSkootHolder;
    private Menu mMenu;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mHeader = inflater.inflate(R.layout.header, null);
//        mQuickReturnView = (TextView) view.findViewById(R.id.sticky);
//        mPlaceHolder = mHeader.findViewById(R.id.placeholder);
    }

    @Override
    public void onRefresh() {
        getLatestSkoots();
    }

    @Override
    public void onStart() {
        super.onStart();
        mLinearLayout.requestFocus();
    }

    @Override
    public void onResume() {
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

        getLatestSkoots();

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

        mSkootHolder = rootView.findViewById(R.id.post_skoot_holder);

//        mListPosts.getViewTreeObserver().addOnGlobalLayoutListener(
//                new ViewTreeObserver.OnGlobalLayoutListener() {
//                    @Override
//                    public void onGlobalLayout() {
//                        mQuickReturnHeight = mQuickReturnView.getHeight();
//                        mListView.computeScrollY();
//                        mCachedVerticalScrollRange = mListPosts.getListHeight();
//                    }
//                }
//        );
        mListPosts.setOnScrollListener(
                new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                        AnimationSet animation = new AnimationSet(true);
                        Animation anim;
                        switch (scrollState) {
                            case 2: // SCROLL_STATE_FLING
                                //hide button here
                                anim = new TranslateAnimation(0, 0, 0, 1000);
                                anim.setDuration(1000);
                                animation.addAnimation(anim);

                                anim = new AlphaAnimation(1.0f, 0.0f);
                                anim.setDuration(1000);
                                animation.addAnimation(anim);

                                mSkootHolder.startAnimation(animation);
                                animation.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        mSkootHolder.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                break;

                            case 1: // SCROLL_STATE_TOUCH_SCROLL
                                //hide button here
                                anim = new TranslateAnimation(0, 0, 0, 1000);
                                anim.setDuration(1000);
                                animation.addAnimation(anim);

                                anim = new AlphaAnimation(1.0f, 0.0f);
                                anim.setDuration(1000);
                                animation.addAnimation(anim);

                                mSkootHolder.startAnimation(animation);
                                animation.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        mSkootHolder.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                break;

                            case 0: // SCROLL_STATE_IDLE
                                //show button here
                                anim = new TranslateAnimation(0, 0, 1000, 0);
                                anim.setDuration(1000);
                                animation.addAnimation(anim);

                                anim = new AlphaAnimation(0.0f, 1.0f);
                                anim.setDuration(1000);
                                animation.addAnimation(anim);

                                mSkootHolder.startAnimation(animation);
                                animation.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        mSkootHolder.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                break;

                            default:
                                break;
                        }
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                         int totalItemCount) {
                        mScrollY = 0;
                        int translationY = 0;

                    }
                }
        );

        final EditText postSkoot = (EditText) rootView.findViewById(R.id.skootText);
        mLinearLayout = (LinearLayout) rootView.findViewById(R.id.focusLayout);
        mLinearLayout.requestFocus();

        postSkoot.setOnFocusChangeListener(
                new View.OnFocusChangeListener()

                {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            Intent intent = new Intent(getActivity(), ComposeActivity.class);
                            startActivity(intent);
                        }
                    }
                }

        );

        Button postSkootButton = (Button) rootView.findViewById(R.id.commentSkoot);
        postSkootButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ComposeActivity.class);
                startActivity(intent);
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    public void getLatestSkoots() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", Integer.toString(BaseActivity.userId));
        params.put("location_id", Integer.toString(BaseActivity.locationId));

        String url = BaseActivity.substituteString(getResources().getString(R.string.home), params);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                final String SKOOTS = "skoots";
                final String SKOOT_ID = "id";
                final String SKOOT_POST = "content";
                final String SKOOT_HANDLE = "channel";
                final String SKOOT_UPVOTES = "upvotes";
                final String SKOOT_DOWNVOTES = "downvotes";
                final String SKOOT_IF_USER_VOTED = "user_voted";
                final String SKOOT_USER_VOTE = "user_vote";
                final String SKOOT_USER_SCOOT = "user_skoot";
                final String SKOOT_CREATED_AT = "created_at";
                final String SKOOT_COMMENTS_COUNT = "comments_count";
                final String SKOOT_FAVORITE_COUNT = "favorites_count";
                final String SKOOT_USER_FAVORITED = "user_favorited";
                final String SKOOT_USER_COMMENTED = "user_commented";
                final String SKOOT_IMAGE_URL = "zone_image";

                try {
                    JSONArray jsonArray = response.getJSONArray(SKOOTS);

                    mPostsList.clear();
                    Log.v(LOG_TAG, jsonArray.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonPost = jsonArray.getJSONObject(i);
                        int id = jsonPost.getInt(SKOOT_ID);
                        String post = jsonPost.getString(SKOOT_POST);

                        String channel = "";
                        if (!jsonPost.isNull(SKOOT_HANDLE)) {
                            channel = "@" + jsonPost.getString(SKOOT_HANDLE);
                        }

                        int upvotes = jsonPost.getInt(SKOOT_UPVOTES);
                        int commentsCount = jsonPost.getInt(SKOOT_COMMENTS_COUNT);
                        int downvotes = jsonPost.getInt(SKOOT_DOWNVOTES);
                        boolean skoot_if_user_voted = jsonPost.getBoolean(SKOOT_IF_USER_VOTED);
                        boolean user_vote = jsonPost.getBoolean(SKOOT_USER_VOTE);
                        boolean user_skoot = jsonPost.getBoolean(SKOOT_USER_SCOOT);
                        boolean user_favorited = jsonPost.getBoolean(SKOOT_USER_FAVORITED);
                        boolean user_commented = jsonPost.getBoolean(SKOOT_USER_COMMENTED);
                        int favoriteCount = jsonPost.getInt(SKOOT_FAVORITE_COUNT);
                        String created_at = jsonPost.getString(SKOOT_CREATED_AT);
                        String image_url = jsonPost.getString(SKOOT_IMAGE_URL);

                        Post postObject = new Post(id, channel, post, commentsCount, favoriteCount, upvotes, downvotes, skoot_if_user_voted, user_vote, user_skoot, user_favorited, user_commented, created_at, image_url);
                        mPostsList.add(postObject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, "Error processing Json Data");
                }
                mPostsAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(LOG_TAG, "Error: " + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();

                if (headers == null
                        || headers.equals(Collections.emptyMap())) {
                    headers = new HashMap<String, String>();
                }
                headers.put("user_id", Integer.toString(BaseActivity.userId));
                headers.put("access_token", BaseActivity.accessToken);

                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(jsonObjectRequest, "home_page");
    }

    public void checkNotifications() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", Integer.toString(BaseActivity.userId));

        String url = BaseActivity.substituteString(getResources().getString(R.string.user_notifications), params);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length() > 0) {
                    MenuItem menuItem = mMenu.findItem(R.id.action_alerts);
                    menuItem.setIcon(R.drawable.notification_icon_active);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();

                if (headers == null
                        || headers.equals(Collections.emptyMap())) {
                    headers = new HashMap<String, String>();
                }

                headers.put("user_id", Integer.toString(BaseActivity.userId));
                headers.put("access_token", BaseActivity.accessToken);

                return headers;
            }
        };
        MenuItem menuItem = mMenu.findItem(R.id.action_alerts);
        menuItem.setIcon(R.drawable.notification_icon_active);
        AppController.getInstance().addToRequestQueue(jsonArrayRequest, "notifications");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
        mMenu = menu;
        checkNotifications();
        super.onCreateOptionsMenu(menu, inflater);
    }
}