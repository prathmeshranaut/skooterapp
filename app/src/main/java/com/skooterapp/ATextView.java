package com.skooterapp;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class ATextView extends TextView {

    public ATextView(Context context) {
        super(context);
        init();
    }

    public ATextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ATextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/museo_300.ttf");
        setTypeface(tf);
    }
}
