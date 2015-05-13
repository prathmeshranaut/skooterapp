package com.skooterapp.layouts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class ABITextView extends TextView {

    public ABITextView(Context context) {
        super(context);
        init();
    }

    public ABITextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ABITextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/museo_500_italics.otf");
        setTypeface(tf);
    }
}