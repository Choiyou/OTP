package com.example.os150.otp;


import android.app.ActivityGroup;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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

/**
 * Created by os150 on 2020-05-19.
 * MemberInfo Activity Java 파일
 * 기능 : RealTimeDatabase userInfo - 자신의 Uid의 nickname 값 불러와 mnicknametv TextView 에 설정
 * : RealTimeDatabase userInfo - 자신의 Uid의 profileimage 값 불러와 CircleImageView 설정
 * : mnicknametv TextView 클릭 시 ProfileActivity로 화면 전환
 * : mypost TextView 클릭 시 TextView 의 Text 값 putExtra 이용하여 MyPostActivity로 데이터 전송 및 Activity 전환
 * : recommendation TextView 클릭 시 TextView 의 Text 값 putExtra 이용하여 MyPostActivity로 데이터 전송
 * 및 AlertDialog 통해 관심 카테고리 설정 및 변경 후 MyPostActivity로 선택여부에 따라 화면 전환
 * : localset TextVIew 클릭 시 TextView 의 Text 값 putExtra 이용하여 MyPostActivity로 데이터 전송 및 Activity 전환
 */
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
    //Integer 타입의 리스트 선언
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

        try {
            //FireBase RealTimeDatabase 의 [userInfo]-[user.getUid())]-[nickname]의 데이터 값 불러오기
            mDatabase.child("userInfo").child(user.getUid()).child("nickname").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mnicknametv.setText(dataSnapshot.getValue(String.class));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("에러", "데이터베이스에서 데이터 불러오기 실패");
                }
            });

            //FireBase RealTimeDatabase 의 [userInfo]-[user.getUid]-[profileimage] 데이터 값 불러오기
            mDatabase.child("userInfo").child(user.getUid()).child("profileimage").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String profileimage = dataSnapshot.getValue(String.class);

                    if (profileimage != null) {
                        if ((profileimage.charAt(0) == 'a') == true) {
                            return;
                        } else {
                            ImageUri = Uri.parse(profileimage); //프로필 imageUri문자열 값 Uri로 변경
                            Glide.with(getApplicationContext()).load(profileimage).into(mprofileimage);
                        }
                    } else {
                        Log.v("알림", "null이 아님");
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("에러", "데이터베이스에서 데이터 불러오기 실패");
                }
            });
        } catch (Exception e1) {
            e1.printStackTrace();
        }


        //nickname TextView 클릭 시
        mnicknametv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        });
        // 내 게시글 TextView 클릭시
        mypost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myposts = new Intent(getApplicationContext(), MyPostActivity.class);
                myposts.putExtra("key", "내 게시글");
                startActivity(myposts);
            }
        });
        // 추천 게시글 TextView 클릭 시
        recommendation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent recommend = new Intent(getApplicationContext(), MyPostActivity.class);
                recommend.putExtra("key", "추천 게시글");
                final AlertDialog.Builder recommendalert = new AlertDialog.Builder(MemberInfoActivity.this); //AlertDialog 생성
                recommendalert.setTitle("관심 카테고리");
                recommendalert.setMessage("기존에 설정하신 카테고리가 있습니까?");
                recommendalert.setCancelable(false).setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(recommend);
                    }
                }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //FireBase RealTimeDatabase의 [UserLikeCategory]-[user.getUid] 데이터 값 불러와
                        mDatabase.child("UserLikeCategory").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                try {
                                    categorylist.clear(); // 리스트 초기화
                                    final String[] itemArray = getResources().getStringArray(R.array.카테고리);
                                    final boolean[] checkArray = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};
                                    final AlertDialog.Builder recommendAlert = new AlertDialog.Builder(MemberInfoActivity.this);
                                    recommendAlert.setTitle("관심 카테고리를 선택해주세요.");
                                    recommendAlert.setMultiChoiceItems(itemArray, checkArray, new DialogInterface.OnMultiChoiceClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                            if (b) {
                                                Log.v("알림", "선택된 항목은 ? " + itemArray[i]);
                                                categorylist.add(i); // 리스트에 추가
                                            } else {
                                                categorylist.remove(Integer.valueOf(i)); //리스트에서 값 제거
                                            }
                                        }
                                    }).setCancelable(false).setPositiveButton("선택완료", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            String categoryitem = "";

                                            //FireBase RealTimeDataBase의 [UserLikeCategory]-[user.getUid]값 제거
                                            mDatabase.child("UserLikeCategory").child(user.getUid()).removeValue();

                                            //리스트 크기 만큼 반복문 루프
                                            for (int z = 0; z < categorylist.size(); z++) {
                                                //카테고리리스트에서 가져온 값 String categoryitem 에 저장
                                                categoryitem = categoryitem + itemArray[categorylist.get(z)];
                                                //z가 categorylist크기보다 1 작을 때
                                                if (z != categorylist.size() - 1) {
                                                    categoryitem = categoryitem + "\n"; //\n 추가
                                                }
                                            }
                                            //data '\n'으로 나눠 저장
                                            String data[] = categoryitem.split("\n");

                                            //data의 길이만큼 반복문
                                            for (int y = 0; y < data.length; y++) {
                                                Log.v("알림", "나눈 것 : " + data[y]);
                                                //FireBase RealTimeDataBase의 [UserLikeCategory]-[user.getUid]-[category n] 값 설정
                                                mDatabase.child("UserLikeCategory").child(user.getUid()).child("category" + y).setValue(data[y]);
                                            }
                                            Log.v("알림", "선택한 아이템 : " + categorylist);
                                            startActivity(recommend); // MyPostActivity로 전환
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
                                Log.e("에러", "데이터베이스에서 데이터 불러오기 실패");
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
        //동네 설정 TextView 클릭 시
        localset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent localseti = new Intent(getApplicationContext(), MyPostActivity.class);
                localseti.putExtra("key", "동네 검색");
                startActivity(localseti);
            }
        });


    }

    //BackButton 동작 X
    @Override
    public void onBackPressed() {
    }
}
