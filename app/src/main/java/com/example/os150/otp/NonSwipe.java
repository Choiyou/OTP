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
 * NonSwipe 자바 파일
 * 기능 : 좌우로 Page 이동을 막기 위한 클래스
 */


public class NonSwipe extends ViewPager {

    public NonSwipe(Context context) {
        super(context);
        MySwipe();
    }

    public NonSwipe(Context context, AttributeSet attrs) {
        super(context, attrs);
        MySwipe();
    }

    //하위 View로 이벤트 전달
    //Swipe로 인해 페이지 바뀌는 것 방지
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return false;
    }

    //Swipe로 인해 페이지 바뀌는 것 방지
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }


    private void MySwipe() {
        try {
            Class<?> VP = ViewPager.class;
            Field swipe = VP.getDeclaredField("SWIPE");//필드를 가져와
            swipe.setAccessible(true); //해당 필드의  접근을 허용
            swipe.set(this, new MySwipe(getContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //화면 전환 속도 조정
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
