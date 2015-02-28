package com.skooterapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.skooterapp.data.Post;

import java.util.ArrayList;
import java.util.List;

public class TrendingPostAdapter extends ArrayAdapter {

    private static final int TYPE_POST = 0;
    private static final int TYPE_TRENDING = 1;
    private static final int TYPE_SEPERATOR = 2;
    private static final int TYPE_MAX_COUNT = TYPE_SEPERATOR + 1;

    Context mContext;
    int mLayoutResourceId;
    List<Post> data = new ArrayList<Post>();
    List<String> mChannels = new ArrayList<String>();

    public TrendingPostAdapter(Context context, int resource, List<Post> objects, List<String> channels) {
        super(context, resource, objects);
        mContext = context;
        mLayoutResourceId = resource;
        this.data = objects;
        mChannels = channels;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);

        if (type == TYPE_TRENDING) {
            if (convertView == null) {
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                convertView = inflater.inflate(R.layout.list_view_trending_post_row, parent, false);
            }
            TextView trendingChannel = (TextView) convertView.findViewById(R.id.trending_channel);
            trendingChannel.setText(mChannels.get(position));

        } else if (type == TYPE_SEPERATOR) {
            if (convertView == null) {
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                convertView = inflater.inflate(R.layout.list_view_separator, parent, false);
            }
        } else if (type == TYPE_POST) {
            if (convertView == null) {
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                convertView = inflater.inflate(mLayoutResourceId, parent, false);
            }

            final Post post = data.get(position - mChannels.size() - 1);

            View is_user_post_view = convertView.findViewById(R.id.is_user_skoot);
            final TextView postContent = (TextView) convertView.findViewById(R.id.postText);
            final TextView handleContent = (TextView) convertView.findViewById(R.id.handleText);
            final TextView timestamp = (TextView) convertView.findViewById(R.id.timestamp);
            final TextView voteCount = (TextView) convertView.findViewById(R.id.voteCount);
            final TextView commentsCount = (TextView) convertView.findViewById(R.id.commentsCount);
            final TextView favoritesCount = (TextView) convertView.findViewById(R.id.favoritesCount);
            final Button flagButton = (Button) convertView.findViewById(R.id.flagButton);
            final Button favoriteBtn = (Button) convertView.findViewById(R.id.favorite);
            final Button upvoteBtn = (Button) convertView.findViewById(R.id.upvote);
            final Button downvoteBtn = (Button) convertView.findViewById(R.id.downvote);
            final ImageView commentImage = (ImageView) convertView.findViewById(R.id.commentImage);
            final NetworkImageView postImage = (NetworkImageView) convertView.findViewById(R.id.post_image);

            if (post.isUserSkoot()) {
                is_user_post_view.setAlpha(1.0f);
                is_user_post_view.setVisibility(View.VISIBLE);
            } else {
                is_user_post_view.setAlpha(0.0f);
                is_user_post_view.setVisibility(View.GONE);
            }
            postContent.setText(post.getContent());

            handleContent.setText(post.getChannel());
            handleContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CharSequence channel = handleContent.getText();
                    Intent intent = new Intent(mContext, ChannelActivity.class);
                    intent.putExtra("CHANNEL_NAME", channel.toString());
                    mContext.startActivity(intent);
                }
            });
            handleContent.setVisibility(View.VISIBLE);
            if (post.getChannel().equals("")) {
                handleContent.setVisibility(View.GONE);
            }

            timestamp.setText(post.getTimestamp());
            voteCount.setText(Integer.toString(post.getVoteCount()));
            commentsCount.setText(Integer.toString(post.getCommentsCount()));
            favoritesCount.setText(Integer.toString(post.getFavoriteCount()));
            flagButton.setVisibility(View.GONE);

            if (post.isImagePresent()) {
                postImage.setVisibility(View.VISIBLE);
                postImage.setImageUrl(post.getSmallImageUrl(), AppController.getInstance().getImageLoader());

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

            favoriteBtn.setTag(post);
            upvoteBtn.setTag(post);
            downvoteBtn.setTag(post);

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

            upvoteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RelativeLayout rl = (RelativeLayout) v.getParent();
                    Button upvoteBtn = (Button) rl.findViewById(R.id.upvote);
                    Button downvoteBtn = (Button) rl.findViewById(R.id.downvote);
                    Post post = (Post) upvoteBtn.getTag();

                    //Call the upvote method
                    post.upvotePost();
                    voteCount.setText(Integer.toString(post.getVoteCount()));

                    upvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_up_active));
                    downvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_down_inactive));
                }
            });

            downvoteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RelativeLayout rl = (RelativeLayout) v.getParent();
                    Button upvoteBtn = (Button) rl.findViewById(R.id.upvote);
                    Button downvoteBtn = (Button) rl.findViewById(R.id.downvote);

                    Post post = (Post) downvoteBtn.getTag();

                    //Call the downvote method
                    post.downvotePost();
                    voteCount.setText(Integer.toString(post.getVoteCount()));
                    upvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_up_inactive));
                    downvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_down_active));
                }
            });

            if (post.isIfUserVoted()) {
                if (post.isUserVote()) {
                    upvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_up_active));
                    downvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_down_inactive));
                } else {
                    upvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_up_inactive));
                    downvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_down_active));
                }
            } else {
                upvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_up_inactive));
                downvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_down_inactive));
            }

            favoriteBtn.setFocusable(false);
            upvoteBtn.setFocusable(false);
            downvoteBtn.setFocusable(false);
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return (null != data ? data.size() : 0);
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mChannels.size()) {
            return TYPE_TRENDING;
        } else if (position == mChannels.size()) {
            return TYPE_SEPERATOR;
        }
        return TYPE_POST;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}