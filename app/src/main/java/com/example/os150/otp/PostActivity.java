package com.example.os150.otp;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

/**
 * Created by os150 on 2020-08-18.
 * PostActivity 자바 파일
 * 기능 : getStringExtra 통해 UserUid, Title, Price 값 가져와 String 형태로 저장
 *      : 가져온 Title 값 FireBase의 RealTimeDataBase와 비교 후 가져와 Bulletin(Title, Price, Bulletins, Image 등 ) 가져오기
 *      : postbtn_chat 버튼 클릭 시 자신의 게시글일 경우 Return, 자신의 게시글이 아닐 경우 상대방 Uid값 putExtra 이용하여 MessageActivity에 데이터 전달
 *        및 Activity 전환
 *
 */

public class PostActivity extends ActivityGroup {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = mAuth.getCurrentUser();

    TextView posttext_title;
    TextView posttext_price;
    TextView posttext_nickname;
    EditText postedit_post;
    Button postbtn_chat;

    ImageView BI_1;
    ImageView BI_2;
    ImageView BI_3;
    ImageView BI_4;

    String Useruid = null;
    String Title = null;
    String Price = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        posttext_title = findViewById(R.id.posttext_title);
        posttext_nickname = findViewById(R.id.posttext_nickname);
        posttext_price = findViewById(R.id.posttext_price);
        postedit_post = findViewById(R.id.postedit_post);
        postbtn_chat = findViewById(R.id.postbtn_chat);

        BI_1 = findViewById(R.id.BI_1);
        BI_2 = findViewById(R.id.BI_2);
        BI_3 = findViewById(R.id.BI_3);
        BI_4 = findViewById(R.id.BI_4);

        //Intent를 통해 Useruid, Title, Price 값 가져오기
        Useruid = getIntent().getStringExtra("Useruid");
        Title = getIntent().getStringExtra("Title");
        Price = getIntent().getStringExtra("Price");

        //FireBase RealTimeDataBase [Bulletin]-[AllBulletin]의 Title값과 Intent로 가져온 값 Title이 동일할 경우 값 불러오기
        mDatabase.child("Bulletin").child("AllBulletin").orderByChild("Title").equalTo(Title).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //그 게시글의 Uid와 아이템 Uid와 같을 경우
                    if (snapshot.getValue(Bulletin.class).Useruid.equals(Useruid)) {
                        // 이미지 가져와 문자열로 저장
                        String Bulletinimage1 = snapshot.getValue(Bulletin.class).BulletinImage1;
                        String Bulletinimage2 = snapshot.getValue(Bulletin.class).BulletinImage2;
                        String Bulletinimage3 = snapshot.getValue(Bulletin.class).BulletinImage3;
                        String Bulletinimage4 = snapshot.getValue(Bulletin.class).BulletinImage4;

                        String PostUserUid = snapshot.getValue(Bulletin.class).Useruid;

                        posttext_title.setText(snapshot.getValue(Bulletin.class).Title);
                        posttext_price.setText(snapshot.getValue(Bulletin.class).Price);
                        postedit_post.setText(snapshot.getValue(Bulletin.class).Bulletins);

                        //FireBase RealTimeDataBase의 [userInfo]-[PostUserUid]-[nickname] nickname 값 불러와 posttext_nickname에 값 설정
                        mDatabase.child("userInfo").child(PostUserUid).child("nickname").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                posttext_nickname.setText(dataSnapshot.getValue(String.class));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e("에러", "데이터베이스에서 데이터 불러오기 실패");
                            }
                        });

                        //이미지 경로가 없을 경우 ImageView를 안보이게 하며 높이 조절
                        if (Bulletinimage1 == null && Bulletinimage2 == null && Bulletinimage3 == null && Bulletinimage4 == null) {
                            BI_1.setVisibility(View.INVISIBLE);
                            BI_1.getLayoutParams().height = 0;
                            BI_2.setVisibility(View.INVISIBLE);
                            BI_2.getLayoutParams().height = 0;
                            BI_3.setVisibility(View.INVISIBLE);
                            BI_3.getLayoutParams().height = 0;
                            BI_4.setVisibility(View.INVISIBLE);
                            BI_4.getLayoutParams().height = 0;
                        }
                        if (Bulletinimage2 == null) {
                            BI_2.getLayoutParams().height = 0;
                            BI_3.getLayoutParams().height = 0;
                            BI_4.getLayoutParams().height = 0;
                        } else if (Bulletinimage3 == null) {
                            BI_3.getLayoutParams().height = 0;
                            BI_4.getLayoutParams().height = 0;
                        } else if (Bulletinimage4 == null) {
                            BI_4.getLayoutParams().height = 0;
                        }

                        try {
                            //이미지 Glide 이용하여 화면에 띄우기
                            Glide.with(getApplicationContext()).load(Bulletinimage1).into(BI_1);
                            Glide.with(getApplicationContext()).load(Bulletinimage2).into(BI_2);
                            Glide.with(getApplicationContext()).load(Bulletinimage3).into(BI_3);
                            Glide.with(getApplicationContext()).load(Bulletinimage4).into(BI_4);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("에러", "데이터베이스에서 데이터 불러오기 실패");
            }
        });

        //Postbtn_chat 버튼 클릭 시
        postbtn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatintent = new Intent(PostActivity.this, MessageActivity.class);
                chatintent.putExtra("destinationUid", Useruid);
                Log.v("알림", "채팅 방 Uid : " + Useruid);

                //Useruid값이 로그인한 UserUid와 동일할 경우
                if (Useruid.equals(user.getUid())) {
                    Toast.makeText(getApplicationContext(), "본인이 게시한 글입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(chatintent);
            }
        });


    }

    //BackButton 클릭시 종료
    @Override
    public void onBackPressed() {
        finish();
    }
}
