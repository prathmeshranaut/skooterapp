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

public class PeekPageFragment extends Fragment {
    ImageView imageView;
    TextView introPeekText;
    ImageView introPeekLocation;

    Animation animation1;
    Animation animation2;
    Animation animation3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_intro_page_3, container, false);

        imageView = (ImageView) rootView.findViewById(R.id.peek_icon);
        introPeekText = (TextView) rootView.findViewById(R.id.intro_peek_text);
        introPeekLocation = (ImageView) rootView.findViewById(R.id.intro_peek_location);

        animation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.intro_screen_anonymous_icon);
        animation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.intro_screen_anonymous_icon);
        animation3 = AnimationUtils.loadAnimation(getActivity(), R.anim.intro_screen_anonymous_icon);

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            imageView.setAlpha(1.0f);
            introPeekText.setAlpha(1.0f);
            introPeekLocation.setAlpha(1.0f);

            imageView.startAnimation(animation1);
            introPeekText.startAnimation(animation2);
            animation2.setStartOffset(150);
            introPeekLocation.startAnimation(animation3);
            animation3.setStartOffset(300);
        } else {
            if(imageView != null && introPeekText != null && introPeekLocation != null) {
                imageView.setAlpha(0.0f);
                introPeekText.setAlpha(0.0f);
                introPeekLocation.setAlpha(0.0f);
            }
        }
    }
}