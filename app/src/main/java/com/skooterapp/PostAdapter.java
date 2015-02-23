package com.skooterapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.skooterapp.data.Post;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends ArrayAdapter<Post> {

    Context mContext;
    int mLayoutResourceId;
    List<Post> data = new ArrayList<Post>();
    boolean mFlaggable;
    LinearLayout mDeleteView;
    LinearLayout mFlagView;
    TextView mTypeIdView;
    TextView mTypeView;
    boolean canPerformActivity = true;

    public boolean isFlaggable() {
        return mFlaggable;
    }

    public void setFlaggable(boolean flaggable) {
        mFlaggable = flaggable;
    }

    public PostAdapter(Context context, int resource, List<Post> objects) {
        super(context, resource, objects);
        mContext = context;
        mLayoutResourceId = resource;
        this.data = objects;
        this.mFlaggable = false;
    }

    public PostAdapter(Context context, int resource, List<Post> objects, boolean canPerformActivity) {
        super(context, resource, objects);
        mContext = context;
        mLayoutResourceId = resource;
        this.data = objects;
        this.canPerformActivity = canPerformActivity;
    }

    public PostAdapter(Context context, int resource, List<Post> objects, boolean flaggable, LinearLayout flagView, LinearLayout deleteView, TextView typeIdView, TextView typeView, boolean canPerformActivity) {
        super(context, resource, objects);
        mContext = context;
        mLayoutResourceId = resource;
        this.data = objects;
        this.mFlaggable = flaggable;
        this.mFlagView = flagView;
        this.mDeleteView = deleteView;
        this.mTypeIdView = typeIdView;
        this.mTypeView = typeView;
        this.canPerformActivity = canPerformActivity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(mLayoutResourceId, parent, false);
        }

        final Post post = data.get(position);

        View is_user_post_view = convertView.findViewById(R.id.is_user_skoot);
        if (post.isUserSkoot()) {
            is_user_post_view.setAlpha(1.0f);
            is_user_post_view.setVisibility(View.VISIBLE);
        } else {
            is_user_post_view.setAlpha(0.0f);
            is_user_post_view.setVisibility(View.GONE);
        }
        TextView postContent = (TextView) convertView.findViewById(R.id.postText);
        postContent.setText(post.getContent());

        final TextView handleContent = (TextView) convertView.findViewById(R.id.handleText);
        handleContent.setText(post.getChannel());
        if (mContext.getClass() != ChannelActivity.class) {
            handleContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CharSequence channel = handleContent.getText();
                    Intent intent = new Intent(mContext, ChannelActivity.class);
                    intent.putExtra("CHANNEL_NAME", channel.toString());
                    mContext.startActivity(intent);
                }
            });
        }
        handleContent.setVisibility(View.VISIBLE);
        if (post.getChannel().equals("")) {
            handleContent.setVisibility(View.GONE);
        }

        TextView timestamp = (TextView) convertView.findViewById(R.id.timestamp);
        timestamp.setText(post.getTimestamp());

        final TextView voteCount = (TextView) convertView.findViewById(R.id.voteCount);
        voteCount.setText(Integer.toString(post.getVoteCount()));

        TextView commentsCount = (TextView) convertView.findViewById(R.id.commentsCount);
        commentsCount.setText(Integer.toString(post.getCommentsCount()));

        final TextView favoritesCount = (TextView) convertView.findViewById(R.id.favoritesCount);
        favoritesCount.setText(Integer.toString(post.getFavoriteCount()));

        final Button flagButton = (Button) convertView.findViewById(R.id.flagButton);

        if (mFlaggable) {
            flagButton.setVisibility(View.VISIBLE);
            if (post.isUserSkoot()) {
                flagButton.setBackground(mContext.getResources().getDrawable(R.drawable.delete));
            } else {
                flagButton.setBackground(mContext.getResources().getDrawable(R.drawable.flag_inactive));
            }
            flagButton.setTag(post);

            flagButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Post post = (Post) flagButton.getTag();

                    if (post.isUserSkoot()) {
                        //Deletable post
                        mDeleteView.setVisibility(View.VISIBLE);
                        mTypeIdView.setText(Integer.toString(post.getId()));
                        mTypeView.setText("post");
                    } else {
                        //Flaggeble post
                        mFlagView.setVisibility(View.VISIBLE);
                        mTypeIdView.setText(Integer.toString(post.getId()));
                        mTypeView.setText("post");
                    }
                }
            });
        } else {
            flagButton.setVisibility(View.GONE);
        }
        final ImageView postImage = (ImageView) convertView.findViewById(R.id.post_image);
        if (post.isImagePresent()) {

            ImageLoader imageLoader = AppController.getInstance().getImageLoader();

            String url = post.getSmallImageUrl();
            postImage.setVisibility(View.VISIBLE);

            imageLoader.get(url, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        postImage.setImageBitmap(response.getBitmap());
                        postImage.setMaxHeight(150);
                        postImage.setMaxWidth(150);
                        postImage.setMinimumHeight(150);
                        postImage.setMinimumWidth(150);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Post", error.getMessage());
                }
            });

            postImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Open an activity with the full image
                    Intent intent = new Intent(mContext, ViewImage.class);
                    intent.putExtra("IMAGE_URL", post.getLargeImageUrl());
                    mContext.startActivity(intent);
                }
            });
        } else {
            postImage.setVisibility(View.GONE);
        }
        ImageView commentImage = (ImageView) convertView.findViewById(R.id.commentImage);

        final Button favoriteBtn = (Button) convertView.findViewById(R.id.favorite);
        Button upvoteBtn = (Button) convertView.findViewById(R.id.upvote);
        Button downvoteBtn = (Button) convertView.findViewById(R.id.downvote);

        favoriteBtn.setTag(post);
        upvoteBtn.setTag(post);
        downvoteBtn.setTag(post);

        upvoteBtn.setEnabled(true);
        downvoteBtn.setEnabled(true);
        favoriteBtn.setEnabled(true);
        upvoteBtn.setAlpha(1.0f);
        downvoteBtn.setAlpha(1.0f);

        //Favorited
        if (post.isUserFavorited()) {
            favoriteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.favorite_icon_active));
        } else {
            favoriteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.favorite_icon_inactive));
        }

        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Post post = (Post) favoriteBtn.getTag();

                if (!post.isUserFavorited()) {
                    favoriteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.favorite_icon_active));
                    post.favoritePost();
                } else {
                    favoriteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.favorite_icon_inactive));
                    post.unFavoritePost();
                }
                favoritesCount.setText(Integer.toString(post.getFavoriteCount()));
            }
        });

        //Commented
        if (post.isUserCommented()) {
            commentImage.setImageResource(R.drawable.comment_active);
        } else {
            commentImage.setImageResource(R.drawable.comment_inactive);
        }

        if (post.isIfUserVoted()) {
            upvoteBtn.setEnabled(false);
            downvoteBtn.setEnabled(false);
            if (post.isUserVote()) {
                upvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_up_active));
                downvoteBtn.setAlpha(0.3f);
            } else {
                downvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_down_active));
                downvoteBtn.setAlpha(0.7f);
                upvoteBtn.setAlpha(0.3f);
            }
        } else {
            upvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_up_inactive));
            downvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_down_inactive));
            if (canPerformActivity) {
                upvoteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RelativeLayout rl = (RelativeLayout) v.getParent();
                        Button upvoteBtn = (Button) rl.findViewById(R.id.upvote);
                        Button downvoteBtn = (Button) rl.findViewById(R.id.downvote);
                        Post post = (Post) upvoteBtn.getTag();

                        //Call the upvote method
                        post.upvotePost();
                        voteCount.setText(Integer.toString(post.getVoteCount() + 1));
                        upvoteBtn.setEnabled(false);
                        downvoteBtn.setEnabled(false);
                        upvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_up_active));
                        downvoteBtn.setAlpha(0.3f);
                    }
                });

                downvoteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RelativeLayout rl = (RelativeLayout) v.getParent();
                        Button upvoteBtn = (Button) rl.findViewById(R.id.upvote);
                        Button downvoteBtn = (Button) rl.findViewById(R.id.downvote);

                        Post post = (Post) downvoteBtn.getTag();

                        //Call the upvote method
                        post.downvotePost();
                        voteCount.setText(Integer.toString(post.getVoteCount() - 1));
                        upvoteBtn.setEnabled(false);
                        downvoteBtn.setEnabled(false);
                        downvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_down_active));
                        upvoteBtn.setAlpha(0.3f);
                    }
                });
            } else {
                upvoteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage("You can't do any activity outside 3 kms");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                });

                downvoteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage("You can't do any activity outside 3 kms");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                });
            }
        }
        favoriteBtn.setFocusable(false);
        upvoteBtn.setFocusable(false);
        downvoteBtn.setFocusable(false);

        return convertView;
    }

    @Override
    public int getCount() {
        return (null != data ? data.size() : 0);
    }
}
