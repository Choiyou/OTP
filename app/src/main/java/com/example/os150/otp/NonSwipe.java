package com.example.os150.otp;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * Created by os150 on 2020-07-16.
 */

public class NonSwipe extends ViewPager {

    public NonSwipe(Context context) {
        super(context);
        setMySwipe();
    }

    public NonSwipe(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMySwipe();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }


    private void setMySwipe() {
        try {
            Class<?> viewpager = ViewPager.class;
            Field swipe = viewpager.getDeclaredField("SWIPE");
            swipe.setAccessible(true);
            swipe.set(this, new MySwipe(getContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class MySwipe extends Scroller {
        public MySwipe(Context context) {
            super(context, new DecelerateInterpolator());
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, 350);
        }
    }
}
