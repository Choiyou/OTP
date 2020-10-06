package com.example.os150.otp;


import android.app.ActivityGroup;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemberInfoActivity extends ActivityGroup {
    TextView mnicknametv;
    TextView mypost;
    TextView recommendation;
    TextView localset;
    CircleImageView mprofileimage;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = mAuth.getCurrentUser();
    Uri ImageUri;
    List<Integer> categorylist = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memberinfo);

        mnicknametv = (TextView) findViewById(R.id.nicknametv);
        mprofileimage = (CircleImageView) findViewById(R.id.pimage);
        mypost = (TextView) findViewById(R.id.myPost);
        recommendation = (TextView) findViewById(R.id.recommendation);
        localset = (TextView) findViewById(R.id.localset);

        Log.v("알림", "My Post : " + mypost.getText());
        Log.v("알림", "Recomment : " + recommendation.getText());
        Log.v("알림", "Local : " + localset.getText());

        try {
            mDatabase.child("userInfo").child(user.getUid()).child("nickname").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mnicknametv.setText(dataSnapshot.getValue(String.class));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.v("알림", "데이터 로드 실패");
                }
            });

            mDatabase.child("userInfo").child(user.getUid()).child("profileimage").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String profileimage = dataSnapshot.getValue(String.class);

                    if (profileimage != null) {
                        if ((profileimage.charAt(0) == 'a') == true) {
                            return;
                        } else {
                            ImageUri = Uri.parse(profileimage); //프로필 imageUri문자열 값 Uri로 변경
                            Log.v("알림", "데이터베이스에서 불러온 값 : " + profileimage);
                            Log.v("알림", "불러온 값 Uri 변환 : " + ImageUri);
                            Glide.with(getApplicationContext()).load(profileimage).into(mprofileimage);
                        }
                    } else {
                        Log.v("알림", "null이 아님");
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.v("알림", "데이터 로드 실패");
                }
            });
        } catch (Exception e1) {
            e1.printStackTrace();
        }


        mnicknametv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        });
        mypost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myposts = new Intent(getApplicationContext(), MyPostActivity.class);
                myposts.putExtra("key", "내 게시글");
                startActivity(myposts);
            }
        });
        recommendation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent recommend = new Intent(getApplicationContext(), MyPostActivity.class);
                recommend.putExtra("key", "추천 게시글");
                final AlertDialog.Builder recommendalert = new AlertDialog.Builder(MemberInfoActivity.this);
                recommendalert.setTitle("관심 카테고리");
                recommendalert.setMessage("기존에 설정하신 카테고리로 계속할까요?");
                recommendalert.setCancelable(false).setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        startActivity(recommend);
                    }
                }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mDatabase.child("UserLikeCategory").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                try {
                                    categorylist.clear();
                                    final String[] itemArray = getResources().getStringArray(R.array.카테고리);
                                    final boolean[] checkArray = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};
                                    final AlertDialog.Builder recommendAlert = new AlertDialog.Builder(MemberInfoActivity.this);
                                    recommendAlert.setTitle("관심 카테고리를 선택해주세요.");
                                    recommendAlert.setMultiChoiceItems(itemArray, checkArray, new DialogInterface.OnMultiChoiceClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                            if (b) {
                                                Toast.makeText(getApplicationContext(), itemArray[i], Toast.LENGTH_SHORT).show();
                                                categorylist.add(i);
                                            } else {
                                                categorylist.remove(Integer.valueOf(i));
                                            }
                                        }
                                    }).setCancelable(false).setPositiveButton("선택완료", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            String categoryitem = "";

                                            mDatabase.child("UserLikeCategory").child(user.getUid()).removeValue();

                                            for (int z = 0; z < categorylist.size(); z++) {
                                                categoryitem = categoryitem + itemArray[categorylist.get(z)];
                                                if (z != categorylist.size() - 1) {
                                                    categoryitem = categoryitem + "\n";
                                                }
                                            }
                                            String data[] = categoryitem.split("\n");

                                            for (int y = 0; y < data.length; y++) {
                                                Log.v("알림", "나눈 것 : " + data[y]);
                                                mDatabase.child("UserLikeCategory").child(user.getUid()).child("category" + y).setValue(data[y]);
                                            }

                                            Log.v("알림", "선택한 아이템 : " + categorylist);
                                            startActivity(recommend);

                                        }
                                    }).setNeutralButton("취소", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Log.v("알림", "다이어로그 취소 버튼 클릭");
                                        }
                                    }).create().show();


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                }).setNeutralButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.v("알림", "취소 클릭");

                    }
                }).create().show();


            }
        });
        localset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent localseti = new Intent(getApplicationContext(), MyPostActivity.class);
                localseti.putExtra("key", "동네 설정");
                startActivity(localseti);
            }
        });


    }

    @Override
    public void onBackPressed() {
    }
}
