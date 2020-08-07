package com.example.os150.otp;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;

/**
 * Created by os150 on 2020-07-06.
 */

public class ChatActivity extends AppCompatActivity {
    ViewPager chatviewPage;
    Button allfriendpage;
    Button myfriendpage;
    Button messagepage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatviewPage = (ViewPager) findViewById(R.id.chatviewPage);
        allfriendpage = (Button) findViewById(R.id.allfriendpage);
        myfriendpage = (Button) findViewById(R.id.myfriendpage);
        messagepage = (Button) findViewById(R.id.messagepage);


        chatviewPage.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        chatviewPage.setCurrentItem(0);
        chatviewPage.getAdapter().notifyDataSetChanged();
        chatviewPage.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                return false;
            }
        });


        allfriendpage.setTag(0);

        allfriendpage.setOnClickListener(PagingListener);
        myfriendpage.setTag(1);
        myfriendpage.setOnClickListener(PagingListener);
        messagepage.setTag(2);
        messagepage.setOnClickListener(PagingListener);

        ActionBar ab = getSupportActionBar();
        ab.hide();
    }

    View.OnClickListener PagingListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int tag = (int) view.getTag();

            chatviewPage.setCurrentItem(tag);
            chatviewPage.getAdapter().notifyDataSetChanged();

            if (tag == 0) {
                allfriendpage.setTextColor(Color.BLUE);
                myfriendpage.setTextColor(Color.BLACK);
                messagepage.setTextColor(Color.BLACK);
            } else if (tag == 1) {
                allfriendpage.setTextColor(Color.BLACK);
                myfriendpage.setTextColor(Color.BLUE);
                messagepage.setTextColor(Color.BLACK);
            } else if (tag == 2) {
                allfriendpage.setTextColor(Color.BLACK);
                myfriendpage.setTextColor(Color.BLACK);
                messagepage.setTextColor(Color.BLUE);
            }

        }
    };

    private class pagerAdapter extends FragmentStatePagerAdapter {
        public pagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
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
