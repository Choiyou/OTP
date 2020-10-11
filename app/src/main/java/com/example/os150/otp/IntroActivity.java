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
 * IntroActivity 자바 파일
 * 기능 : Animation 동작 후 MainActivity로 전환
 */

public class IntroActivity extends Activity {
    ImageView introimage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        introimage = (ImageView) findViewById(R.id.introimage);


        //이동 Animation
        TranslateAnimation ani = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        ani.setFillAfter(true); //애니메이션 종료후 이미지 종료된 시점의 위치에 정지
        ani.setDuration(1500); // 지속 시간

        introimage.startAnimation(ani);

        //딜레이 처리
        new Handler().postDelayed(new Runnable() { // 2초 후 실행
            @Override
            public void run() {
                Log.v("알림", "메인화면으로 이동");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();

            }
        }, 2000);


    }

    //Activity 잠시 멈춤 상태
    protected void onPause() {
        super.onPause();
//        finish();
    }
}
