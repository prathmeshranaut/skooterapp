package net.aayush.skooterapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.aayush.skooterapp.R;

public class SneekPeek extends Fragment {

    // Store instance variables
    private String title;



    public static SneekPeek newInstance(String title) {
        SneekPeek fragmentsneekpeek = new SneekPeek();
        Bundle args = new Bundle();
        args.putString("Sneek Peek", title);
        fragmentsneekpeek.setArguments(args);
        return fragmentsneekpeek;
    }

    public SneekPeek() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sneek_peek, container, false);
    }
}
