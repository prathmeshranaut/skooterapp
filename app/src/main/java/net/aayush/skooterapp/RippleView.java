package net.aayush.skooterapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;

public class RippleView extends View {

    protected Paint mPaint;
    protected AnimationSet mAnimation;

    public RippleView(Context context) {
        super(context);
        mPaint = new Paint();
    }

    public RippleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
    }

    public RippleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(5);
        mPaint.setAntiAlias(true);
        mPaint.setColor(getResources().getColor(R.color.md_green_100));

        if(mAnimation == null) {
            createAnimation(canvas);
        }
        canvas.drawCircle(getWidth()/2,getHeight()/2,150, mPaint);
    }

    private void createAnimation(Canvas canvas) {
        Animation animationScaleUp = AnimationUtils.loadAnimation(getContext(), R.anim.scale_up);
        Animation animationFadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        mAnimation = new AnimationSet(true);
        mAnimation.addAnimation(animationScaleUp);
        mAnimation.addAnimation(animationFadeOut);
        mAnimation.setRepeatMode(Animation.RESTART);
        mAnimation.setRepeatCount(Animation.INFINITE);
//        this.setAnimation(mAnimation);
        this.startAnimation(mAnimation);



//        mAnimation = new ScaleAnimation(0.2f, 1.0f, 0.2f, 1.0f,
//                Animation.RELATIVE_TO_SELF, 0.5f,
//                Animation.RELATIVE_TO_SELF, 0.5f);
//        mAnimation = new AlphaAnimation(1.0f, 0.2f);
//        mAnimation.setInterpolator(new LinearInterpolator());
//        mAnimation.setRepeatMode(Animation.RESTART);
//        mAnimation.setRepeatCount(Animation.INFINITE);
//        mAnimation.setDuration(4000L);
//        startAnimation(mAnimation);
    }
}
