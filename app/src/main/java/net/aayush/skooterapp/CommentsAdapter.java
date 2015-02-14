package net.aayush.skooterapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.aayush.skooterapp.data.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentsAdapter extends ArrayAdapter<Comment> {
    protected Context mContext;
    protected int mLayoutResourceId;
    protected List<Comment> data = new ArrayList<Comment>();
    LinearLayout mDeleteView;
    LinearLayout mFlagView;
    TextView mTypeIdView;
    TextView mTypeView;
    boolean mFlaggable;
    boolean canPerformActivity;

    public CommentsAdapter(Context context, int resource, List<Comment> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mLayoutResourceId = resource;
        this.data = objects;
        mFlaggable = false;
    }

    public CommentsAdapter(Context context, int resource, List<Comment> objects, boolean flaggable, LinearLayout flagView, LinearLayout deleteView, TextView typeIdView, TextView typeView, boolean canPerformActivity) {
        super(context, resource, objects);
        this.mContext = context;
        this.mLayoutResourceId = resource;
        this.data = objects;
        this.mFlagView = flagView;
        this.mDeleteView = deleteView;
        this.mTypeIdView = typeIdView;
        this.mTypeView = typeView;
        this.mFlaggable = flaggable;
        this.canPerformActivity = canPerformActivity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(mLayoutResourceId, parent, false);
        }

        Comment comment = data.get(position);

        TextView commentContent = (TextView) convertView.findViewById(R.id.postText);
        commentContent.setText(comment.getContent());

        TextView timestamp = (TextView) convertView.findViewById(R.id.timestamp);
        timestamp.setText(comment.getTimestamp());

        final TextView voteCount = (TextView) convertView.findViewById(R.id.voteCount);
        voteCount.setText(Integer.toString(comment.getVoteCount()));

        Button upvoteBtn = (Button) convertView.findViewById(R.id.upvote);
        Button downvoteBtn = (Button) convertView.findViewById(R.id.downvote);

        upvoteBtn.setTag(comment);
        downvoteBtn.setTag(comment);

        upvoteBtn.setEnabled(true);
        downvoteBtn.setEnabled(true);
        upvoteBtn.setAlpha(1.0f);
        downvoteBtn.setAlpha(1.0f);

        View is_user_comment = convertView.findViewById(R.id.is_user_comment);
        final Button flagButton = (Button) convertView.findViewById(R.id.flagButton);


        if (comment.isUserComment()) {
            is_user_comment.setVisibility(View.VISIBLE);
            is_user_comment.setAlpha(1.0f);

            flagButton.setBackground(mContext.getResources().getDrawable(R.drawable.delete));
        } else {
            is_user_comment.setVisibility(View.GONE);
            is_user_comment.setAlpha(0.0f);

            flagButton.setBackground(mContext.getResources().getDrawable(R.drawable.flag_inactive));
        }

        if (mFlaggable) {
            flagButton.setTag(comment);

            flagButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Comment comment = (Comment) flagButton.getTag();

                    if (comment.isUserComment()) {
                        //Deletable post
                        mDeleteView.setVisibility(View.VISIBLE);
                        mTypeIdView.setText(Integer.toString(comment.getId()));
                        mTypeView.setText("comment");
                    } else {
                        //Flaggeble post
                        mFlagView.setVisibility(View.VISIBLE);
                        mTypeIdView.setText(Integer.toString(comment.getId()));
                        mTypeView.setText("comment");
                    }
                }
            });
        } else {
            flagButton.setVisibility(View.GONE);
        }
        if (comment.isIfUserVoted()) {
            upvoteBtn.setEnabled(false);
            downvoteBtn.setEnabled(false);
            if (comment.isUserVote()) {
                upvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_up_active));
                downvoteBtn.setAlpha(0.3f);
            } else {
                downvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_down_active));
                upvoteBtn.setAlpha(0.3f);
            }
        } else {
            if(canPerformActivity) {
                upvoteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RelativeLayout rl = (RelativeLayout) v.getParent();
                        Button upvoteBtn = (Button) rl.findViewById(R.id.upvote);
                        Button downvoteBtn = (Button) rl.findViewById(R.id.downvote);
                        Comment comment = (Comment) upvoteBtn.getTag();

                        //Call the upvote method
                        comment.upvoteComment();
                        voteCount.setText(Integer.toString(comment.getVoteCount() + 1));
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

                        Comment comment = (Comment) downvoteBtn.getTag();

                        //Call the upvote method
                        comment.downvoteComment();
                        voteCount.setText(Integer.toString(comment.getVoteCount() - 1));
                        upvoteBtn.setEnabled(false);
                        downvoteBtn.setEnabled(false);
                        downvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_down_active));
                        upvoteBtn.setAlpha(0.3f);
                    }
                });
            }
        }
        //upvoteBtn.setFocusable(false);
        //downvoteBtn.setFocusable(false);

        return convertView;
    }
}
