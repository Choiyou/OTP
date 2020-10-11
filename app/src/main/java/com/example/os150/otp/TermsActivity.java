package com.example.os150.otp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

/**
 * Created by os150 on 2020-05-20.
 * Terms Activity 자바 파일
 * 기능 : allcb 클릭 시 Check 여부에 따른 기능 수행
 *      : nextbtn 클릭 시 termscb & pipcb 모두 check 상태일 경우에만 SignupActivity 로 화면 전환
 *      : backbtn 클릭 시 MainActivity로 화면 전환
 **/

public class TermsActivity extends Activity { //
    CheckBox allcb;
    CheckBox termscb;
    CheckBox pipcb;
    Button nextbtn;
    Button backbutton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        allcb = (CheckBox) findViewById(R.id.allcb);
        termscb = (CheckBox) findViewById(R.id.termscb);
        pipcb = (CheckBox) findViewById(R.id.pipcb);
        nextbtn = (Button) findViewById(R.id.nextbtn);
        backbutton = (Button) findViewById(R.id.backbutton);

        //allcb 클릭시
        allcb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //allcb가 check 되어 있으면 Termscb & pipcb 모두 check
                if (allcb.isChecked()) {
                    termscb.setChecked(true);
                    pipcb.setChecked(true);
                } else {
                    // 아니면 Termscb & pipcb 모두 uncheck
                    termscb.setChecked(false);
                    pipcb.setChecked(false);
                }
            }
        });
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (termscb.isChecked() && pipcb.isChecked()) {
                    startActivity(new Intent(getApplicationContext(), SignupActivity.class));
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "모두 동의해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });


    }

    //BackButton 클릭 x
    @Override
    public void onBackPressed() {
        //  super.onBackPressed();
    }
}
