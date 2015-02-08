package net.aayush.skooterapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import net.aayush.skooterapp.common.view.SlidingTabLayout;

import static net.aayush.skooterapp.common.view.SlidingTabLayout.TabColorizer;

public class SlidingFragment extends Fragment {

    private SlidingTabLayout mSlidingTabLayout;
    private LinearLayout mTabsLinearLayout;
    private ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sample, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setUpPager(view);
    }
    void setUpPager(View view){
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        TabsPagerAdapter tabsAdapter = new TabsPagerAdapter(getActivity().getBaseContext(), getFragmentManager());
        mViewPager.setAdapter(tabsAdapter);
        mViewPager.setBackgroundColor(getResources().getColor(R.color.skooterBackgroundColor));
        mViewPager.setPageMargin(20);
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setCustomTabView(R.layout.image_slider_tab, 0);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setBackgroundColor(getResources().getColor(R.color.skooterBackgroundColor));
        mSlidingTabLayout.setBackground(getResources().getDrawable(R.drawable.drop_shadow));
        mSlidingTabLayout.setCustomTabColorizer(new TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return SlidingFragment.this.getResources().getColor(R.color.skooterNPrimaryTextColor);
            }
        });
    }

}