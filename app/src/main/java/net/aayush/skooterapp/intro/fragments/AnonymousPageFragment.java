package net.aayush.skooterapp.intro.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import net.aayush.skooterapp.R;

public class AnonymousPageFragment extends Fragment {
    ImageView imageView;
    TextView introShareText;
    ImageView introShareIcon;
    TextView anonymousText;

    Animation animation1;
    Animation animation2;
    Animation animation3;
    Animation animation4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_intro_page_1, container, false);

        imageView = (ImageView) rootView.findViewById(R.id.anonymous_icon);
        introShareText = (TextView) rootView.findViewById(R.id.intro_share_text);
        introShareIcon = (ImageView) rootView.findViewById(R.id.intro_share_icon);
        anonymousText = (TextView) rootView.findViewById(R.id.intro_anonymously_text);

        animation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.intro_screen_anonymous_icon);
        animation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.intro_screen_anonymous_icon);
        animation3 = AnimationUtils.loadAnimation(getActivity(), R.anim.intro_screen_anonymous_icon);
        animation4 = AnimationUtils.loadAnimation(getActivity(), R.anim.intro_screen_anonymous_icon);

        imageView.startAnimation(animation1);
        introShareText.startAnimation(animation2);
        animation2.setStartOffset(150);
        introShareIcon.startAnimation(animation3);
        animation3.setStartOffset(300);
        anonymousText.startAnimation(animation4);
        animation4.setStartOffset(450);

        return rootView;
    }
}