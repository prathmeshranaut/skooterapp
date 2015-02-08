package net.aayush.skooterapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import net.aayush.skooterapp.fragments.Home;
import net.aayush.skooterapp.fragments.Me;
import net.aayush.skooterapp.fragments.Peek;
import net.aayush.skooterapp.fragments.Trending;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    public Context mContext;
    private int[] imageResId = {
            R.drawable.home_inactive,
            R.drawable.trending_inactive,
            R.drawable.peek_inactive,
    };

    private int[] activeImageResId = {
            R.drawable.home_active,
            R.drawable.trending_active,
            R.drawable.peek_active,
    };

    public TabsPagerAdapter(Context mContext, FragmentManager fm) {
        super(fm);
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return imageResId.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Drawable image = mContext.getResources().getDrawable(imageResId[position]);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        SpannableString sb = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    public CharSequence getActivePageTitle(int position) {
        Drawable image = mContext.getResources().getDrawable(activeImageResId[position]);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        SpannableString sb = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Home();
            case 1:
                return new Trending();
            case 2:
                return new Peek();
            case 3:
                return new Me();
            default:
                return null;
        }
    }
}