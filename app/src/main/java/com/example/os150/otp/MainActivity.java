package com.example.os150.otp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button msigninbtn;
    Button msignupbtn;
    Button mresetpwbtn;
    EditText midedit;
    EditText mpwedit;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        msigninbtn = (Button) findViewById(R.id.msigninbtn);
        msignupbtn = (Button) findViewById(R.id.msignupbtn);
        mresetpwbtn = (Button) findViewById(R.id.mpwresetbtn);
        midedit = (EditText) findViewById(R.id.inputid);
        mpwedit = (EditText) findViewById(R.id.inputpw);

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(getApplicationContext(), "권한 설정 허용", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getApplicationContext(), "권한 설정 실패", Toast.LENGTH_SHORT).show();

            }

        };
        TedPermission.with(getApplicationContext()).setPermissionListener(permissionListener).setDeniedMessage("권한설정을 허용하지 않을 경우 서비스를 제대로 이용하실 수 없습니다.").setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.ACCESS_COARSE_LOCATION).check();

        final ProgressDialog pd = new ProgressDialog(this);


        //로그인이 되어있다면
        if (user != null) {
            finish();
            startActivity(new Intent(getApplicationContext(), SecondMainActivity.class));
            //두번째메인페이지를 시작하고 이 액티비티는 종료
        }


        //로그인 버튼 클릭 시 이벤트
        msigninbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = midedit.getText().toString();
                String pw = mpwedit.getText().toString();

                // ID, PW 입력 유무 확인
                if (TextUtils.isEmpty(id)) {
                    midedit.setError("이메일을 입력해주세요.");
                    return;
                } else if (TextUtils.isEmpty(pw)) {
                    mpwedit.setError("비밀번호를 입력해주세요.");
                    return;
                }

                pd.setMessage("로그인중입니다. \n 잠시만 기다려주세요 :D  .... ");
                pd.show();

                mAuth.signInWithEmailAndPassword(id, pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            finish();
                            startActivity(new Intent(getApplicationContext(), SecondMainActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다. \n 다시 입력해주세요.", Toast.LENGTH_SHORT).show();
                            midedit.setText("");
                            mpwedit.setText("");
                        }
                        pd.dismiss();


                    }
                });


            }
        });

        //회원가입 버튼 클릭 시 이벤트
        msignupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(), TermsActivity.class));
            }
        });

        //비밀번호 재설정 버튼 클릭 시 이벤트
        mresetpwbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText resetpw = new EditText(MainActivity.this);
                resetpw.getTransformationMethod();
                resetpw.setSingleLine();
                final AlertDialog.Builder resetpwQ = new AlertDialog.Builder(MainActivity.this);
                resetpwQ.setTitle("비밀번호 재설정");
                resetpwQ.setView(resetpw);
                resetpwQ.setMessage("비밀번호 재설정을 위해 이메일을 입력해주세요.").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String emailaddress = resetpw.getText().toString().trim();
                        if (resetpw.getText().length() == 0) {
                            dialogInterface.dismiss();
                        } else {
                            mAuth.sendPasswordResetEmail(emailaddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "이메일을 확인해주세요.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "이메일 전송 실패", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.v("알림", "비밀번호 재설정 취소");
                    }
                });
                resetpwQ.show();

            }
        });

        ActionBar ab = getSupportActionBar();
        ab.hide();

    }

    @Override
    public void onBackPressed() {

        finish();
    }
}

