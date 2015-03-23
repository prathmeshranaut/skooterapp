package com.skooterapp.fragments;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.skooterapp.AppController;
import com.skooterapp.BaseActivity;
import com.skooterapp.ChannelActivity;
import com.skooterapp.ComposeActivity;
import com.skooterapp.PostAdapter;
import com.skooterapp.R;
import com.skooterapp.SearchSuggestionsAdapter;
import com.skooterapp.SkooterJsonArrayRequest;
import com.skooterapp.ViewPostActivity;
import com.skooterapp.data.Post;

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
    protected PullToRefreshListView mListPosts;
    protected Context mContext;
    protected LinearLayout mLinearLayout;

    private static final int ACTIVITY_POST_SKOOT = 100;

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
    private List<String> items = new ArrayList<String>();
    private Menu mMenu;
    private SearchView mSearchView;
    protected SearchSuggestionsAdapter mSuggestionAdapter;

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

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("Home");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mPostsAdapter.notifyDataSetChanged();
        mLinearLayout.requestFocus();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = container.getContext();

        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        getLatestSkoots();

        mPostsAdapter = new PostAdapter(mContext, R.layout.list_view_post_row, BaseActivity.mHomePosts);
        mListPosts = (PullToRefreshListView) rootView.findViewById(R.id.list_posts);
        mListPosts.setAdapter(mPostsAdapter);
        mListPosts.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                // Do work to refresh the list here.
                getLatestSkoots();
            }
        });

        mListPosts.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ViewPostActivity.class);
                intent.putExtra(BaseActivity.SKOOTER_POST, BaseActivity.mHomePosts.get(position - 1));
                startActivity(intent);
            }
        });

        mSkootHolder = rootView.findViewById(R.id.post_skoot_holder);

//        mNotificationList.getViewTreeObserver().addOnGlobalLayoutListener(
//                new ViewTreeObserver.OnGlobalLayoutListener() {
//                    @Override
//                    public void onGlobalLayout() {
//                        mQuickReturnHeight = mQuickReturnView.getHeight();
//                        mListView.computeScrollY();
//                        mCachedVerticalScrollRange = mNotificationList.getListHeight();
//                    }
//                }
//        );
//        mNotificationList.setOnScrollListener(
//                new AbsListView.OnScrollListener() {
//                    @Override
//                    public void onScrollStateChanged(AbsListView view, int scrollState) {
//                        AnimationSet animation = new AnimationSet(true);
//                        Animation anim;
//                        switch (scrollState) {
//                            case 2: // SCROLL_STATE_FLING
//                                //hide button here
//                                anim = new TranslateAnimation(0, 0, 0, 1000);
//                                anim.setDuration(1000);
//                                animation.addAnimation(anim);
//
//                                anim = new AlphaAnimation(1.0f, 0.0f);
//                                anim.setDuration(1000);
//                                animation.addAnimation(anim);
//
//                                mSkootHolder.startAnimation(animation);
//                                animation.setAnimationListener(new Animation.AnimationListener() {
//                                    @Override
//                                    public void onAnimationStart(Animation animation) {
//
//                                    }
//
//                                    @Override
//                                    public void onAnimationEnd(Animation animation) {
//                                        mSkootHolder.setVisibility(View.GONE);
//                                    }
//
//                                    @Override
//                                    public void onAnimationRepeat(Animation animation) {
//
//                                    }
//                                });
//                                break;
//
//                            case 1: // SCROLL_STATE_TOUCH_SCROLL
//                                //hide button here
//                                anim = new TranslateAnimation(0, 0, 0, 1000);
//                                anim.setDuration(1000);
//                                animation.addAnimation(anim);
//
//                                anim = new AlphaAnimation(1.0f, 0.0f);
//                                anim.setDuration(1000);
//                                animation.addAnimation(anim);
//
//                                mSkootHolder.startAnimation(animation);
//                                animation.setAnimationListener(new Animation.AnimationListener() {
//                                    @Override
//                                    public void onAnimationStart(Animation animation) {
//
//                                    }
//
//                                    @Override
//                                    public void onAnimationEnd(Animation animation) {
//                                        mSkootHolder.setVisibility(View.GONE);
//                                    }
//
//                                    @Override
//                                    public void onAnimationRepeat(Animation animation) {
//
//                                    }
//                                });
//                                break;
//
//                            case 0: // SCROLL_STATE_IDLE
//                                //show button here
//                                anim = new TranslateAnimation(0, 0, 1000, 0);
//                                anim.setDuration(1000);
//                                animation.addAnimation(anim);
//
//                                anim = new AlphaAnimation(0.0f, 1.0f);
//                                anim.setDuration(1000);
//                                animation.addAnimation(anim);
//
//                                mSkootHolder.startAnimation(animation);
//                                animation.setAnimationListener(new Animation.AnimationListener() {
//                                    @Override
//                                    public void onAnimationStart(Animation animation) {
//
//                                    }
//
//                                    @Override
//                                    public void onAnimationEnd(Animation animation) {
//                                        mSkootHolder.setVisibility(View.VISIBLE);
//                                    }
//
//                                    @Override
//                                    public void onAnimationRepeat(Animation animation) {
//
//                                    }
//                                });
//                                break;
//
//                            default:
//                                break;
//                        }
//                    }
//
//                    @Override
//                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
//                                         int totalItemCount) {
//                        mScrollY = 0;
//                        int translationY = 0;
//
//                    }
//                }
//        );

        mLinearLayout = (LinearLayout) rootView.findViewById(R.id.focusLayout);
        mLinearLayout.requestFocus();

        Button postSkootButton = (Button) rootView.findViewById(R.id.commentSkoot);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ComposeActivity.class);
                startActivityForResult(intent, ACTIVITY_POST_SKOOT);
            }
        };

        postSkootButton.setOnClickListener(listener);
        mSkootHolder.setOnClickListener(listener);

//        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        lps.addRule(RelativeLayout.CENTER_VERTICAL);
//        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
//        lps.setMargins(margin, margin, margin, margin);
//
//        ShowcaseView sv = new ShowcaseView.Builder(getActivity())
//                .setTarget(new ViewTarget(rootView.findViewById(R.id.post_skoot_holder)))
//                .setContentTitle("Home")
//                .setContentText("Get all the updates happening around you in 3 km.")
//                .setStyle(R.style.CustomShowcaseTheme)
//                .hideOnTouchOutside()
//                .build();
//
//        Log.v(LOG_TAG, Integer.toString(container.getChildCount()));
//
//        sv.setButtonPosition(lps);
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

                try {
                    JSONArray jsonArray = response.getJSONArray(SKOOTS);

                    BaseActivity.mHomePosts.clear();
                    Log.v(LOG_TAG, jsonArray.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Post postObject = Post.parsePostFromJSONObject(jsonArray.getJSONObject(i));

                        BaseActivity.mHomePosts.add(postObject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, "Error processing Json Data");
                }
                mPostsAdapter.notifyDataSetChanged();
                mListPosts.onRefreshComplete();
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ACTIVITY_POST_SKOOT) {
                mPostsAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
        mMenu = menu;

        mMenu = menu;
        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        mSearchView.setIconified(true);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d(LOG_TAG, s);
                mSearchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                items.clear();
                if (query.length() >= 3) {
                    loadSuggestions(query);
                }
                return true;
            }
        });
        mSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int i) {
                Cursor cursor = (Cursor) mSearchView.getSuggestionsAdapter().getItem(i);
                String feedName = cursor.getString(2);
                mSearchView.setQuery(feedName, false);
                mSearchView.clearFocus();
                Intent intent = new Intent(getActivity(), ChannelActivity.class);
                Log.d(LOG_TAG, feedName);
                intent.putExtra("CHANNEL_NAME", feedName);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onSuggestionClick(int i) {
                Cursor cursor = (Cursor) mSearchView.getSuggestionsAdapter().getItem(i);
                String feedName = cursor.getString(1);
                mSearchView.setQuery(feedName, false);
                mSearchView.clearFocus();
                Intent intent = new Intent(getActivity(), ChannelActivity.class);
                intent.putExtra("CHANNEL_NAME", feedName);
                Log.d(LOG_TAG, feedName);
                startActivity(intent);
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            mSearchView.setIconified(true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void loadSuggestions(String query) {
        AppController.getInstance().cancelPendingRequests("suggestions");
        Map<String, String> params = new HashMap<>();
        params.put("user_id", Integer.toString(BaseActivity.userId));
        params.put("query", query);

        String url = BaseActivity.substituteString(getString(R.string.channel_suggestion), params);
        final String q = query;
        SkooterJsonArrayRequest skooterJsonArrayRequest = new SkooterJsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                items.clear();
                Log.d(LOG_TAG, response.toString());
                for (int i = 0; i < response.length(); i++) {
                    try {
                        if (!response.isNull(i)) {
                            items.add(response.getString(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                loadHistory(q, mMenu);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(LOG_TAG, error.getMessage());
            }
        });

        AppController.getInstance().addToRequestQueue(skooterJsonArrayRequest, "suggestions");
    }

    private void loadHistory(String query, Menu menu) {
        // Cursor
        String[] columns = new String[]{"_id", "text"};
        Object[] temp = new Object[]{0, "default"};

        MatrixCursor cursor = new MatrixCursor(columns);

        for (int i = 0; i < items.size(); i++) {

            temp[0] = i;
            temp[1] = items.get(i);

            cursor.addRow(temp);
        }

        if (mSuggestionAdapter == null) {
            mSuggestionAdapter = new SearchSuggestionsAdapter(getActivity().getBaseContext(), cursor, items);
            mSearchView.setSuggestionsAdapter(mSuggestionAdapter);
        }else {
            mSuggestionAdapter.changeCursor(cursor);
        }
        mSuggestionAdapter.notifyDataSetInvalidated();
    }
}