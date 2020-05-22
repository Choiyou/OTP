package com.example.os150.otp;

import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by os150 on 2020-05-21.
 */

public class ProfileActivity extends ActivityGroup {

    Button profilereset;
    Button pwchange;
    Button withdrawal;
    Button logout;
    Button nicknamechange;
    TextView nicknametv;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        profilereset = (Button)findViewById(R.id.profilechange);
        pwchange = (Button)findViewById(R.id.pwchange);
        withdrawal = (Button)findViewById(R.id.withdrawal);
        logout = (Button)findViewById(R.id.logout);
        nicknamechange = (Button)findViewById(R.id.nicknamechange);
        nicknametv = (TextView) findViewById(R.id.nicknametv);



        mDatabase.child("users").child("userInfo").child(user.getUid().toString()).child("nickname").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                nicknametv.setText(dataSnapshot.getValue(String.class));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("알림","데이터 Load실패");

            }
        });



        //비밀번호 변경 클릭 시
        pwchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText pwchangeed = new EditText(ProfileActivity.this);
                pwchangeed.getTransformationMethod();
                AlertDialog.Builder pwchangeQ = new AlertDialog.Builder(ProfileActivity.this);
                pwchangeQ.setTitle("비밀번호 변경");
                pwchangeQ.setView(pwchangeed);
                pwchangeQ.setMessage("새로운 비밀번호를 입력해 주세요.\n 변경 이후 되돌릴 수 없습니다.").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        user.updatePassword(pwchangeed.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(),"변경 완료",Toast.LENGTH_SHORT).show();
                                mAuth.signOut();
                                finish();
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            }
                        });
                    }
                });
                pwchangeQ.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(),"취소",Toast.LENGTH_SHORT).show();
                    }
                });
                pwchangeQ.show();

            }
        });

        //로그아웃 버튼 클릭 시
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                finish();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));

            }
        });

        //회원탈퇴 버튼 클릭 시
        withdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AlertDialog.Builder withdrawalQ = new AlertDialog.Builder(ProfileActivity.this);
                    withdrawalQ.setMessage("정말 계정을 삭제하시겠습니까 ? ").setCancelable(false).setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.v("알림","계정 삭제 진행 완료");
                                    Toast.makeText(getApplicationContext(), "삭제 완료", Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));


                                }
                            });
                            mDatabase.child("users").child("userInfo").child(user.getUid()).setValue(null);

                        }
                    });
                    withdrawalQ.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getApplicationContext(), "취소", Toast.LENGTH_SHORT).show();
                        }
                    });
                    withdrawalQ.show();
                }
                catch(Exception e){
                    Log.e("에러", Log.getStackTraceString(e));

                }
            }
        });

        nicknamechange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText nchange = new EditText(ProfileActivity.this);
                nchange.getTransformationMethod();

                AlertDialog.Builder nicknameQ = new AlertDialog.Builder(ProfileActivity.this);
                nicknameQ.setTitle("별명");
                nicknameQ.setView(nchange);
                nicknameQ.setMessage("새로운 별명을 입력해주세요.").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mDatabase.child("users").child("userInfo").child(user.getUid().toString()).child("nickname").setValue(nchange.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(),"별명 변경 완료", Toast.LENGTH_SHORT).show();
//                                finish();
//                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            }
                        });

                    }
                });
                nicknameQ.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(),"취소",Toast.LENGTH_SHORT).show();
                    }
                });
                nicknameQ.show();


            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }
}
