package net.aayush.skooterapp;

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

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;

import net.aayush.skooterapp.data.Post;

import java.util.ArrayList;
import java.util.List;

public class PeekPostAdapter extends ArrayAdapter<Post> {

    Context mContext;
    int mLayoutResourceId;
    List<Post> data = new ArrayList<Post>();

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

        View is_user_post_view = convertView.findViewById(R.id.is_user_post);
        if (post.isUserSkoot()) {
            is_user_post_view.setAlpha(1.0f);
        } else {
            is_user_post_view.setAlpha(0.0f);
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

        final ImageView zoneImage = (ImageView) convertView.findViewById(R.id.zone_icon);
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();

        String url = post.getImageUrl();

        imageLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    zoneImage.setImageBitmap(response.getBitmap());
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(error.getMessage());
            }
        });

        final ImageView postImage = (ImageView) convertView.findViewById(R.id.post_image);
        if (post.isImagePresent()) {

            ImageLoader imageLoader2 = AppController.getInstance().getImageLoader();

            String url2 = post.getSmallImageUrl();
            postImage.setVisibility(View.VISIBLE);

            imageLoader2.get(url2, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        postImage.setImageBitmap(response.getBitmap());
                        postImage.setBackground(null);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Peek Post", "Error: " + error.getMessage());
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

        TextView timestamp = (TextView) convertView.findViewById(R.id.timestamp);
        timestamp.setText(post.getTimestamp());

        final TextView voteCount = (TextView) convertView.findViewById(R.id.voteCount);
        voteCount.setText(Integer.toString(post.getVoteCount()));

        TextView commentsCount = (TextView) convertView.findViewById(R.id.commentsCount);
        commentsCount.setText(Integer.toString(post.getCommentsCount()));

        final TextView favoritesCount = (TextView) convertView.findViewById(R.id.favoritesCount);
        favoritesCount.setText(Integer.toString(post.getFavoriteCount()));

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
