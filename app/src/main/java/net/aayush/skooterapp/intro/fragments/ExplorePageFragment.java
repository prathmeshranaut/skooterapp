package net.aayush.skooterapp.intro.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import net.aayush.skooterapp.R;

public class ExplorePageFragment extends Fragment {
    ImageView imageView;
    TextView introExplore;
    TextView introAroundYou;
    ImageView introChannels;
    Button introFinish;

    Animation animation1;
    Animation animation2;
    Animation animation3;
    Animation animation4;
    Animation animation5;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_intro_page_4, container, false);

        imageView = (ImageView) rootView.findViewById(R.id.intro_compass);
        introExplore = (TextView) rootView.findViewById(R.id.intro_explore);
        introAroundYou = (TextView) rootView.findViewById(R.id.intro_around_you);
        introChannels = (ImageView) rootView.findViewById(R.id.intro_channels);
        introFinish = (Button) rootView.findViewById(R.id.intro_finish_button);

        introFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        animation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.intro_screen_anonymous_icon);
        animation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.intro_screen_anonymous_icon);
        animation3 = AnimationUtils.loadAnimation(getActivity(), R.anim.intro_screen_anonymous_icon);
        animation4 = AnimationUtils.loadAnimation(getActivity(), R.anim.intro_screen_anonymous_icon);
        animation5 = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            imageView.setAlpha(1.0f);
            introExplore.setAlpha(1.0f);
            introAroundYou.setAlpha(1.0f);
            introChannels.setAlpha(1.0f);
            introFinish.setAlpha(1.0f);

            imageView.startAnimation(animation1);
            introExplore.startAnimation(animation2);
            animation2.setStartOffset(150);
            introAroundYou.startAnimation(animation3);
            animation3.setStartOffset(300);
            introChannels.startAnimation(animation4);
            animation4.setStartOffset(450);
            introFinish.startAnimation(animation5);
            animation5.setStartOffset(1450);
        } else {
            if(imageView != null && introExplore != null && introAroundYou != null && introChannels != null) {
                imageView.setAlpha(0.0f);
                introExplore.setAlpha(0.0f);
                introAroundYou.setAlpha(0.0f);
                introChannels.setAlpha(0.0f);
                introFinish.setAlpha(0.0f);
            }
        }
    }
}