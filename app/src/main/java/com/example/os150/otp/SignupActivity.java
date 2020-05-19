package com.example.os150.otp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.regex.Pattern;

/**
 * Created by os150 on 2020-05-19.
 */

public class SignupActivity extends Activity {

    private FirebaseAuth mAuth;
    Button backbtn;
    Button signupbtn;
    EditText mname;
    EditText mnickname;
    EditText mphonenum;
    EditText memail;
    EditText mpw;
    EditText mpwc;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        backbtn = (Button)findViewById(R.id.backbtn);
        signupbtn = (Button)findViewById(R.id.signupbtn);
        mname = (EditText)findViewById(R.id.name);
        mnickname = (EditText)findViewById(R.id.nickname);
        mphonenum = (EditText)findViewById(R.id.phonenum);
        memail = (EditText)findViewById(R.id.email);
        mpw = (EditText)findViewById(R.id.pw);
        mpwc = (EditText)findViewById(R.id.pwcheck);

        final ProgressDialog progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backintent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(backintent);
                finish();
            }
        });

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = mname.getText().toString();
                final String nickname = mnickname.getText().toString();
                final  String phonenum = mphonenum.getText().toString();
                final String email = memail.getText().toString();
                final String pw = mpw.getText().toString();
                final  String pwc = mpwc.getText().toString();
                final String profileimage = getResources().getDrawable(R.drawable.drawable_userimage).toString();


                if(TextUtils.isEmpty(name)){
                    mname.setError("이름을 입력해주세요.");
                    return;
                }else if(TextUtils.isEmpty(nickname)){
                    mnickname.setError("별명을 입력해주세요.");
                    return;
                }else if(TextUtils.isEmpty(phonenum)){
                    mphonenum.setError("휴대폰번호를 입력해주세요.");
                    return;
                }else if(TextUtils.isEmpty(email)){
                    memail.setError("이메일을 입력해주세요.");
                    return;
                }else if(TextUtils.isEmpty(pw)){
                    mpw.setError("비밀번호를 입력해주세요.");
                    return;
                }else if(TextUtils.isEmpty(pwc)){
                    mpwc.setError("비밀번호 확인을 입력해주세요.");
                    return;
                }
                if(pw.length()<=5){
                    mpw.setError("6글자 이상 입력해주세요.");
                    mpw.setText("");
                }if(!pwc.equals(pw)){
                    mpwc.setError("비밀번호가 일치하지 않습니다.");
                    mpwc.setText("");
                    mpwc.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    memail.setError("이메일 형식이 아닙니다");
                }

                progressDialog.setMessage("회원가입중입니다 \n기다려주세요 :)");
                progressDialog.show();
                mAuth.createUserWithEmailAndPassword(email,pw).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
                            com.example.os150.otp.UserInfo userInfo = new com.example.os150.otp.UserInfo(name,nickname,phonenum,email,profileimage);
                            mDatabase.child("userInfo").child(mAuth.getCurrentUser().getUid()).setValue(userInfo);
                            Toast.makeText(getApplicationContext(),"회원가입 성공", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),TabActivity.class));
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(),"회원가입 실패",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });

    }


    @Override
    public void onBackPressed() {
      //  super.onBackPressed();
    }
}
