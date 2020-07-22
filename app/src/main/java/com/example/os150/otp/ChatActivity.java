package com.example.os150.otp;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

/**
 * Created by os150 on 2020-07-06.
 */

public class ChatActivity extends AppCompatActivity {
    ViewPager viewPage;
    Button allfriend;
    Button myfriend;
    Button messagepage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        viewPage = (ViewPager) findViewById(R.id.viewPage);
        allfriend = (Button) findViewById(R.id.allfriend);
        myfriend = (Button) findViewById(R.id.myfriend);
        messagepage = (Button) findViewById(R.id.messagepage);


        viewPage.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        viewPage.setCurrentItem(0);
        viewPage.getAdapter().notifyDataSetChanged();

        viewPage.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                return false;
            }
        });

        String color = "#F00332";


        allfriend.setTag(0);

        allfriend.setOnClickListener(movePageListener);
        myfriend.setTag(1);
        myfriend.setOnClickListener(movePageListener);
        messagepage.setTag(2);
        messagepage.setOnClickListener(movePageListener);

        ActionBar ab = getSupportActionBar();
        ab.hide();
    }

    View.OnClickListener movePageListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int tag = (int) view.getTag();
            viewPage.setCurrentItem(tag);
            viewPage.getAdapter().notifyDataSetChanged();
            if (tag == 0) {
                allfriend.setTextColor(Color.BLUE);
                myfriend.setTextColor(Color.BLACK);
                messagepage.setTextColor(Color.BLACK);
            } else if (tag == 1) {
                allfriend.setTextColor(Color.BLACK);
                myfriend.setTextColor(Color.BLUE);
                messagepage.setTextColor(Color.BLACK);
            } else if (tag == 2) {
                allfriend.setTextColor(Color.BLACK);
                myfriend.setTextColor(Color.BLACK);
                messagepage.setTextColor(Color.BLUE);
            }

        }
    };

    private class pagerAdapter extends FragmentStatePagerAdapter {
        public pagerAdapter(FragmentManager fm) {

            super(fm);

        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {


            switch (position) {
                case 0:
                    return new AllFriendFragment();
                case 1:
                    return new MyFriendFragment();
                case 2:
                    return new MessagePageFragment();
                default:
                    return null;
            }


        }

        @Override
        public int getCount() {
            return 3;
        }


    }



}
