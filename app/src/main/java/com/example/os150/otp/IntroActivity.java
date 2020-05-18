package com.example.os150.otp;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

/**
 * Created by os150 on 2020-05-18.
 */

public class IntroActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        ImageView introimage = (ImageView)findViewById(R.id.introimage);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent introintent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(introintent);
                finish();
            }
        },2500);
        // 애니메이션 오른쪽에서 왼쪽으로 좌표이동
        TranslateAnimation ani = new TranslateAnimation(Animation.RELATIVE_TO_SELF,1.0f,
                                                        Animation.RELATIVE_TO_SELF,0.0f,
                                                        Animation.RELATIVE_TO_SELF,0.0f,
                                                        Animation.RELATIVE_TO_SELF,0.0f);
        ani.setFillAfter(true);
        ani.setDuration(1500); // 지속 시간

        introimage.startAnimation(ani);

    }
    protected void onPause(){
        super.onPause();
        finish();
    }
}
