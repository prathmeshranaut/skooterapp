package net.aayush.skooterapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import net.aayush.skooterapp.fragments.Home;
import net.aayush.skooterapp.fragments.SneekPeek;
import net.aayush.skooterapp.fragments.Trending;

public class PagerAdapter extends FragmentPagerAdapter {

    private static int NUM_ITEMS = 3;

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Home();
            case 1:
                return new Trending();
            case 2:
                return SneekPeek.newInstance("Blah blah");
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence mTitle = null;
        switch (position) {
            case 0:
                return "Latest";
            case 1:
                return "Trending";
            case 2:
                return "Sneek Peek";
        }
        return mTitle;
    }
}
