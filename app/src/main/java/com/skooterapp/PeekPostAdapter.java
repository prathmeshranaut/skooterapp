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
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.skooterapp.data.Post;

import java.util.ArrayList;
import java.util.List;

public class PeekPostAdapter extends ArrayAdapter<Post> {

    protected Context mContext;
    protected int mLayoutResourceId;
    protected List<Post> data = new ArrayList<Post>();

    public PeekPostAdapter(Context context, int resource, List<Post> objects) {
        super(context, resource, objects);
        mContext = context;
        mLayoutResourceId = resource;
        this.data = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(mLayoutResourceId, parent, false);
        }

        final Post post = data.get(position);

        View isUserPostView = convertView.findViewById(R.id.is_user_post);
        final TextView postContent = (TextView) convertView.findViewById(R.id.postText);
        final TextView handleContent = (TextView) convertView.findViewById(R.id.handleText);
        final NetworkImageView zoneImage = (NetworkImageView) convertView.findViewById(R.id.zone_icon);
        final NetworkImageView postImage = (NetworkImageView) convertView.findViewById(R.id.post_image);
        final TextView timestamp = (TextView) convertView.findViewById(R.id.timestamp);
        final TextView voteCount = (TextView) convertView.findViewById(R.id.voteCount);
        final TextView commentsCount = (TextView) convertView.findViewById(R.id.commentsCount);
        final TextView favoritesCount = (TextView) convertView.findViewById(R.id.favoritesCount);
        final Button favoriteBtn = (Button) convertView.findViewById(R.id.favorite);
        final ImageView commentImage = (ImageView) convertView.findViewById(R.id.commentImage);
        final Button upvoteBtn = (Button) convertView.findViewById(R.id.upvote);
        final Button downvoteBtn = (Button) convertView.findViewById(R.id.downvote);
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();

        // Show/Hide indicator depending if the post is made by user
        if (post.isUserSkoot()) {
            isUserPostView.setAlpha(1.0f);
        } else {
            isUserPostView.setAlpha(0.0f);
        }

        postContent.setText(post.getContent());
        if (post.getChannel().equals("")) {
            handleContent.setVisibility(View.GONE);
        }
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

//        String url = post.getImageUrl();
//
//        imageLoader.get(url, new ImageLoader.ImageListener() {
//            @Override
//            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
//                if (response.getBitmap() != null) {
//                    zoneImage.setImageBitmap(response.getBitmap());
//                }
//            }
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d(error.getMessage());
//            }
//        });

        zoneImage.setImageUrl(post.getImageUrl(), imageLoader);

        if (post.isImagePresent()) {
            postImage.setVisibility(View.VISIBLE);
            postImage.setImageUrl(post.getSmallImageUrl(), AppController.getInstance().getImageLoader());

//            String url2 = post.getSmallImageUrl();
//            postImage.setVisibility(View.VISIBLE);
//            postImage.setBackgroundColor(mContext.getResources().getColor(R.color.md_grey_200));
//            imageLoader2.get(url2, new ImageLoader.ImageListener() {
//                @Override
//                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
//                    if (response.getBitmap() != null) {
//                        postImage.setImageBitmap(response.getBitmap());
//                        postImage.setBackground(null);
//                    }
//                }
//
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    VolleyLog.d("Peek Post", "Error: " + error.getMessage());
//                }
//            });
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

        timestamp.setText(post.getTimestamp());

        voteCount.setText(Integer.toString(post.getVoteCount()));
        commentsCount.setText(Integer.toString(post.getCommentsCount()));
        favoritesCount.setText(Integer.toString(post.getFavoriteCount()));

        favoriteBtn.setTag(post);
        upvoteBtn.setTag(post);
        downvoteBtn.setTag(post);

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

        return convertView;
    }

    @Override
    public int getCount() {
        return (null != data ? data.size() : 0);
    }
}
