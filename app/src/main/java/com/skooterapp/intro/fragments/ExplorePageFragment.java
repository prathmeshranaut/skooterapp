package com.skooterapp.intro.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.skooterapp.BaseActivity;
import com.skooterapp.R;

public class ExplorePageFragment extends Fragment {
    ImageView imageView;
    TextView introExplore;
    TextView introAroundYou;
    ImageView introChannels;
    Button introFinish;

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
                SharedPreferences settings = getActivity().getSharedPreferences(BaseActivity.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("intro_screen", 1);
                editor.apply();
                getActivity().finish();
            }
        });

        return rootView;
    }
}