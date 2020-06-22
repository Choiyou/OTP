package com.example.os150.otp;


import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by os150 on 2020-05-19.
 */
@SuppressWarnings("deprecation")
public class SecondMainActivity extends ActivityGroup {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondmain);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }


        TabHost tabHost = (TabHost) findViewById(R.id.tabhost);

        tabHost.setup(this.getLocalActivityManager());

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

    @Override
    public void onBackPressed() {

    }
}
