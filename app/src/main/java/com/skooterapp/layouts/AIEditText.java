package com.skooterapp.layouts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class AIEditText extends EditText{

    public AIEditText(Context context) {
        super(context);
        init();
    }

    public AIEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AIEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/museo_300_italics.otf");
        setTypeface(tf);
    }
}
