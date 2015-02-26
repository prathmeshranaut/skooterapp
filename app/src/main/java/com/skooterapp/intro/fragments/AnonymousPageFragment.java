package com.skooterapp.intro.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.skooterapp.R;

public class AnonymousPageFragment extends Fragment {
    ImageView imageView;
    TextView introShareText;
    ImageView introShareIcon;
    TextView anonymousText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_intro_page_1, container, false);

        imageView = (ImageView) rootView.findViewById(R.id.anonymous_icon);
        introShareText = (TextView) rootView.findViewById(R.id.intro_share_text);
        introShareIcon = (ImageView) rootView.findViewById(R.id.intro_share_icon);
        anonymousText = (TextView) rootView.findViewById(R.id.intro_anonymously_text);

        return rootView;
    }
}