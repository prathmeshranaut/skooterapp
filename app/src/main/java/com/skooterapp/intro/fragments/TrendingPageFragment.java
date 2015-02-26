package com.skooterapp.intro.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.skooterapp.R;

public class TrendingPageFragment extends Fragment {
    ImageView imageView;
    TextView introStayInformed;
    TextView introFindOutLatest;
    TextView introChannels;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_intro_page_2, container, false);

        imageView = (ImageView) rootView.findViewById(R.id.trending_icon);
        introStayInformed = (TextView) rootView.findViewById(R.id.stay_informed);
        introFindOutLatest = (TextView) rootView.findViewById(R.id.find_out_latest);
        introChannels = (TextView) rootView.findViewById(R.id.intro_channels);

        return rootView;
    }

}