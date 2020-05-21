package com.example.os150.otp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    Button signinbtn;
    Button signupbtn;
    Button pwresetbtn;
    EditText inputemail;
    EditText inputpw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signinbtn = (Button)findViewById(R.id.signinbtn);
        signupbtn = (Button)findViewById(R.id.signupbtn);
        pwresetbtn = (Button)findViewById(R.id.pwresetbtn);
        inputemail = (EditText)findViewById(R.id.inputid);
        inputpw = (EditText)findViewById(R.id.inputpw);

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        final ProgressDialog progressDialog = new ProgressDialog(this);
       /* if(user!=null){
            finish();
            startActivity(new Intent(getApplicationContext(), SecondMainActivity.class));
        }*/
        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent termsintent = new Intent(getApplicationContext(),TermsActivity.class);
              startActivity(termsintent);
              finish();
            }
        });

        signinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = inputemail.getText().toString();
                String pw = inputpw.getText().toString();

                if(TextUtils.isEmpty(id)){
                    inputemail.setError("이메일을 입력해주세요.");
                    return;
                }else if(TextUtils.isEmpty(pw)){
                    inputpw.setError("비밀번호를 입력해주세요.");
                    return;
                }
                progressDialog.setMessage("로그인중입니다.\n 잠시만 기다려주세요 :) ");
                progressDialog.show();

                mAuth.signInWithEmailAndPassword(id,pw).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent tabintent = new Intent(getApplicationContext(),SecondMainActivity.class);
                            startActivity(tabintent);
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(),"로그인 실패",Toast.LENGTH_SHORT).show();
                            inputemail.setText("");
                            inputpw.setText("");
                        }
                        progressDialog.dismiss();

                    }
                });

            }
        });
        pwresetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"비밀번호 재설정 작업 check",Toast.LENGTH_SHORT).show();

            }
        });
        // ActionBar 숨기기
        ActionBar ab = getSupportActionBar();
        ab.hide();


    }
}
