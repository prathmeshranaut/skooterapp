package net.aayush.skooterapp;

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

import net.aayush.skooterapp.data.Post;

import java.util.ArrayList;
import java.util.List;

public class TrendingPostAdapter extends ArrayAdapter {

    private static final int TYPE_POST = 0;
    private static final int TYPE_TRENDING = 1;
    private static final int TYPE_MAX_COUNT = TYPE_TRENDING + 1;
    Context mContext;
    int mLayoutResourceId;
    List data = new ArrayList();

    public TrendingPostAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        mContext = context;
        mLayoutResourceId = resource;
        this.data = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);

        if(type == TYPE_TRENDING) {
            if (convertView == null) {
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                convertView = inflater.inflate(R.layout.list_view_trending_post_row, parent, false);

                TextView trendingChannel = (TextView) convertView.findViewById(R.id.trending_channel);
                trendingChannel.setText(data.get(position).toString());
            }
        } else {
            if (convertView == null) {
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                convertView = inflater.inflate(mLayoutResourceId, parent, false);
            }
            Post post = (Post) data.get(position);

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

            TextView timestamp = (TextView) convertView.findViewById(R.id.timestamp);
            timestamp.setText(post.getTimestamp());

            final TextView voteCount = (TextView) convertView.findViewById(R.id.voteCount);
            voteCount.setText(Integer.toString(post.getVoteCount()));

            TextView commentsCount = (TextView) convertView.findViewById(R.id.commentsCount);
            commentsCount.setText(Integer.toString(post.getCommentsCount()));

            TextView favoritesCount = (TextView) convertView.findViewById(R.id.favoritesCount);
            favoritesCount.setText(Integer.toString(post.getFavoriteCount()));

            final Button flagButton = (Button) convertView.findViewById(R.id.flagButton);

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
        if(position < 3) {
            return TYPE_TRENDING;
        }
        return TYPE_POST;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
