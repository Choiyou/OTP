package com.example.os150.otp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * Created by os150 on 2020-05-19.
 */

public class SignupActivity extends Activity {

    Button backbtn;
    Button ssignupbtn;
    EditText snameedit;
    EditText snicknameedit;
    EditText semailedit;
    EditText spwedit;
    EditText spwcedit;
    EditText sphonenumedit;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        backbtn = (Button) findViewById(R.id.backbtn);
        ssignupbtn = (Button) findViewById(R.id.signupbtn);
        snameedit = (EditText) findViewById(R.id.name);
        snicknameedit = (EditText) findViewById(R.id.nickname);
        semailedit = (EditText) findViewById(R.id.email);
        spwedit = (EditText) findViewById(R.id.pw);
        spwcedit = (EditText) findViewById(R.id.pwcheck);
        sphonenumedit = (EditText) findViewById(R.id.phonenum);

        final ProgressDialog pd2 = new ProgressDialog(this);


        // 뒤로가기 버튼 클릭 시 이벤트
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        ssignupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String sname = snameedit.getText().toString();
                final String snickname = snicknameedit.getText().toString();
                final String semail = semailedit.getText().toString();
                String spw = spwedit.getText().toString();
                String spwc = spwcedit.getText().toString();
                final String sphonenum = sphonenumedit.getText().toString();

                // 이름, 별명, 이메일, 비밀번호, 비밀번호확인 입력 유무 확인
                if (TextUtils.isEmpty(sname)) {
                    snameedit.setError("이름을 입력해주세요.");
                    return;
                } else if (TextUtils.isEmpty(snickname)) {
                    snicknameedit.setError("별명을 입력해주세요.");
                    return;
                } else if (TextUtils.isEmpty(semail)) {
                    semailedit.setError("이메일을 입력해주세요.");
                    return;
                } else if (TextUtils.isEmpty(spw)) {
                    spwedit.setError("비밀번호를 입력해주세요.");
                    return;
                } else if (TextUtils.isEmpty(spwc)) {
                    spwcedit.setError("비밀번호 확인을 입력해주세요.");
                    return;
                }

                // 비밀번호 유효성 검사
                if (spw.length() <= 5) {
                    spwedit.setError("6자리 이상 입력해주세요.");
                    spwedit.setText("");
                }
                if (!spwc.equals(spw)) {
                    spwcedit.setError("비밀번호가 일치하지 않습니다.");
                    spwcedit.setText("");
                    spwcedit.requestFocus();
                    return;
                }
                //이메일 유효성 검사
                if (!Patterns.EMAIL_ADDRESS.matcher(semail).matches()) {
                    semailedit.setError("이메일 형식이 아닙니다.");
                    semailedit.setText("");
                }


                pd2.setMessage("회원가입이 진행되고 있습니다. \n 잠시만 기다려주세요...");
                pd2.show();

                mAuth.createUserWithEmailAndPassword(semail, spw).
                        addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Uri image = Uri.parse("android.resource://com.example.os150.opt/drawable/" + getResources().getResourceEntryName(R.drawable.drawable_userimage));
                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                            //회원가입 유저 정보 Database 저장
                            UserInfo userInfo = new UserInfo(sname, snickname, sphonenum, semail, image.toString());
                            mDatabase.child("userInfo").child(mAuth.getCurrentUser().getUid()).setValue(userInfo);
                            Log.v("알림", "프로필 이미지 경로 : " + image.toString() + "\n회원가입 성공");

                            //Chat가능 User DataBase저장
                            UserModel userModel = new UserModel();
                            userModel.nickname = snickname;
                            userModel.uid = mAuth.getCurrentUser().getUid();
                            userModel.profileimage = image.toString();

                            mDatabase.child("chatuserInfo").child(mAuth.getCurrentUser().getUid()).setValue(userModel);

                            startActivity(new Intent(getApplicationContext(), SecondMainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "회원가입에 실패하셨습니다.\n 이미 존재하는 아이디일 수 있습니다.", Toast.LENGTH_SHORT).show();

                        }

                    }
                });

                pd2.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}
