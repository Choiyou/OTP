package com.example.os150.otp;

import android.*;
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

/**
 * Created by os150 on 2020-05-19.
 */
@SuppressWarnings("deprecation")
public class SecondMainActivity extends ActivityGroup {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_secondmain);

            PermissionListener permissionListener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                            Toast.makeText(getApplicationContext(), "권한 설정 허용", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(intent);
//                finish();
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                            Toast.makeText(getApplicationContext(), "권한 설정 실패", Toast.LENGTH_SHORT).show();

                    }
            };

            TedPermission.with(getApplicationContext()).setPermissionListener(permissionListener)
                    .setDeniedMessage("권한 설정 허용 하지 않을 경우 서비스를 제대로 이용하실수 없습니다.")
                    .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA, android.Manifest.permission.ACCESS_COARSE_LOCATION).check();



            TabHost tabHost = (TabHost)findViewById(R.id.tabhost);

            tabHost.setup(this.getLocalActivityManager());

            TabHost.TabSpec tabSpecMain = tabHost.newTabSpec("MAIN").setIndicator("메인");
            tabSpecMain.setContent(new Intent(getApplicationContext(),ConnectActivity.class));
            tabHost.addTab(tabSpecMain);

            TabHost.TabSpec tabSpecCategory = tabHost.newTabSpec("CATEGORY").setIndicator("카테고리");
            tabSpecCategory.setContent(new Intent(getApplicationContext(),CategoryActivity.class));
            tabHost.addTab(tabSpecCategory);

            TabHost.TabSpec tabSpecWrite = tabHost.newTabSpec("WRITE").setIndicator("글쓰기");
            tabSpecWrite.setContent(new Intent(getApplicationContext(),WriteActivity.class));
            tabHost.addTab(tabSpecWrite);

            TabHost.TabSpec tabSpecMemberInfo = tabHost.newTabSpec("MEMBERINFO").setIndicator("개인정보");
            tabSpecMemberInfo.setContent(new Intent(getApplicationContext(),MemberInfoActivity.class));
            tabHost.addTab(tabSpecMemberInfo);

            TabHost.TabSpec tabSpecChat = tabHost.newTabSpec("CHAT").setIndicator("채팅");
            tabSpecChat.setContent(new Intent(getApplicationContext(),ChatActivity.class));
            tabHost.addTab(tabSpecChat);

            TabHost.TabSpec tabSpecMap = tabHost.newTabSpec("MAP").setIndicator("지도");
            tabSpecMap.setContent(new Intent(getApplicationContext(),MapActivity.class));
            tabHost.addTab(tabSpecMap);

            tabHost.setCurrentTab(0);

    }
}
