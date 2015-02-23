package com.skooterapp.intro.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.skooterapp.R;

public class TrendingPageFragment extends Fragment {
    ImageView imageView;
    TextView introStayInformed;
    TextView introFindOutLatest;
    TextView introChannels;

    Animation animation1;
    Animation animation2;
    Animation animation3;
    Animation animation4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_intro_page_2, container, false);

        imageView = (ImageView) rootView.findViewById(R.id.trending_icon);
        introStayInformed = (TextView) rootView.findViewById(R.id.stay_informed);
        introFindOutLatest = (TextView) rootView.findViewById(R.id.find_out_latest);
        introChannels = (TextView) rootView.findViewById(R.id.intro_channels);

        animation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.intro_screen_anonymous_icon);
        animation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.intro_screen_anonymous_icon);
        animation3 = AnimationUtils.loadAnimation(getActivity(), R.anim.intro_screen_anonymous_icon);
        animation4 = AnimationUtils.loadAnimation(getActivity(), R.anim.intro_screen_anonymous_icon);

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            imageView.setAlpha(1.0f);
            introStayInformed.setAlpha(1.0f);
            introChannels.setAlpha(1.0f);
            introFindOutLatest.setAlpha(1.0f);

            imageView.startAnimation(animation1);
            introStayInformed.startAnimation(animation2);
            animation2.setStartOffset(150);
            introFindOutLatest.startAnimation(animation3);
            animation3.setStartOffset(300);
            introChannels.startAnimation(animation4);
            animation4.setStartOffset(450);
        } else {
            if(imageView != null && introStayInformed != null && introFindOutLatest != null && introChannels != null) {
                imageView.setAlpha(0.0f);
                introStayInformed.setAlpha(0.0f);
                introChannels.setAlpha(0.0f);
                introFindOutLatest.setAlpha(0.0f);
            }
        }
    }
}