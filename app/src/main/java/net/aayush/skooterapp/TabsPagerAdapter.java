package net.aayush.skooterapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import net.aayush.skooterapp.fragments.Home;
import net.aayush.skooterapp.fragments.Me;
import net.aayush.skooterapp.fragments.Trending;

public class TabsPagerAdapter extends FragmentPagerAdapter {
    String tabs[] = {"Home", "Hot", "Peek", "Me"};

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return tabs.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Home();
            case 1:
                return new Trending();
            case 2:
                return new Trending();
            case 3:
                return new Me();
            default:
                return null;
        }
    }
}