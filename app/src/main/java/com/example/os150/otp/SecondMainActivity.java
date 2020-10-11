package com.example.os150.otp;


import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by os150 on 2020-05-19.
 * SecondMainActivity 자바 파일
 * 기능 : 로그인한 유저가 존재하지 않을 경우 IntroActivity로 화면 전환
 *      : Layout 각 Tab별로 표시되는 Text 설정 및 연결 Activity 설정
 */
@SuppressWarnings("deprecation")
public class SecondMainActivity extends ActivityGroup {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondmain);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //로그인한 user가 없다면 IntroActivity로 화면 전환

        if (user == null) {
            finish();
            startActivity(new Intent(getApplicationContext(), IntroActivity.class));
        }


        TabHost tabHost = (TabHost) findViewById(R.id.tabhost);

        tabHost.setup(this.getLocalActivityManager()); // TabWidget 정상 작동을 위한 작업

        //각 Tab 별 표시 텍스트 설정 및 연결된 PageVIew
        TabHost.TabSpec tabSpecMain = tabHost.newTabSpec("MAIN").setIndicator("메인");
        tabSpecMain.setContent(new Intent(getApplicationContext(), ConnectActivity.class));
        tabHost.addTab(tabSpecMain);


        TabHost.TabSpec tabSpecCategory = tabHost.newTabSpec("CATEGORY").setIndicator("카테고리");
        tabSpecCategory.setContent(new Intent(getApplicationContext(), CategoryActivity.class));
        tabHost.addTab(tabSpecCategory);

        TabHost.TabSpec tabSpecWrite = tabHost.newTabSpec("WRITE").setIndicator("글\n쓰기");
        tabSpecWrite.setContent(new Intent(getApplicationContext(), WriteActivity.class));
        tabHost.addTab(tabSpecWrite);

        TabHost.TabSpec tabSpecMemberInfo = tabHost.newTabSpec("MEMBERINFO").setIndicator("개인정보");
        tabSpecMemberInfo.setContent(new Intent(getApplicationContext(), MemberInfoActivity.class));
        tabHost.addTab(tabSpecMemberInfo);

        TabHost.TabSpec tabSpecChat = tabHost.newTabSpec("CHAT").setIndicator("채팅");
        tabSpecChat.setContent(new Intent(getApplicationContext(), ChatActivity.class));
        tabHost.addTab(tabSpecChat);

        TabHost.TabSpec tabSpecMap = tabHost.newTabSpec("MAP").setIndicator("지도");
        tabSpecMap.setContent(new Intent(getApplicationContext(), MapActivity.class));
        tabHost.addTab(tabSpecMap);

        tabHost.setCurrentTab(0);

    }

    //BackButton 클릭시 동작 x
    @Override
    public void onBackPressed() {

    }
}
