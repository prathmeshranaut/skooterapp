package com.skooterapp;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by aayushranaut on 2/28/15.
 * com.skooterapp
 */
public class ABTextView extends TextView {
    public ABTextView(Context context) {
        super(context);
        init();
    }

    public ABTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ABTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/museo_500.otf");
        setTypeface(tf);
    }
}
