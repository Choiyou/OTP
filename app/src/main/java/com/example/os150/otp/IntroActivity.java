package com.example.os150.otp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

/**
 * Created by os150 on 2020-05-18.
 */

public class IntroActivity extends Activity {
    ImageView introimage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        introimage = (ImageView) findViewById(R.id.introimage);


        TranslateAnimation ani = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        ani.setFillAfter(true);
        ani.setDuration(1500); // 지속 시간

        introimage.startAnimation(ani);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.v("알림", "메인화면으로 이동");

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();

            }
        }, 2000);


    }

    protected void onPause() {
        super.onPause();
        finish();
    }
}
