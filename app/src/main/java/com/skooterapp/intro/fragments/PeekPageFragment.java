package com.skooterapp.intro.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.skooterapp.R;

public class PeekPageFragment extends Fragment {
    ImageView imageView;
    TextView introPeekText;
    ImageView introPeekLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_intro_page_3, container, false);

        imageView = (ImageView) rootView.findViewById(R.id.peek_icon);
        introPeekText = (TextView) rootView.findViewById(R.id.intro_peek_text);
        introPeekLocation = (ImageView) rootView.findViewById(R.id.intro_peek_location);

        return rootView;
    }
}