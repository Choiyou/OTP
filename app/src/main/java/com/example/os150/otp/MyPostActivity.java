package com.example.os150.otp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by os150 on 2020-08-24.
 * MyPost Activity 자바 파일
 * 기능 : 목록 Adapter에 Intent Key값을 가져와 Key 값에 따른 처리
 *      : ItemView 클릭 시 항목의 Useruid 값과 Title값을 putExtra 이용해 PostActivity로 데이터 전송 및 Activity 전환
 *
 */

public class MyPostActivity extends AppCompatActivity {

    //Bulletin 타입의 리스트 생성
    List<Bulletin> MyPostModel = new ArrayList<>();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = mAuth.getCurrentUser();


    RecyclerView mypost_recyclerView;
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypost);

        mypost_recyclerView = (RecyclerView) findViewById(R.id.mypost_recycleview);
        mLayoutManager = new LinearLayoutManager(this);
        mypost_recyclerView.setLayoutManager(mLayoutManager);
        mypost_recyclerView.setAdapter(new MyPostActivityRecyclerViewAdapter());

        //ActionBar 숨기기
        ActionBar ab = getSupportActionBar();
        ab.hide();


    }

    //목록 어뎁터 추가
    class MyPostActivityRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public MyPostActivityRecyclerViewAdapter() {
            Intent intent = getIntent(); //Intent값 가져오기
            switch (intent.getStringExtra("key")) {
                case "내 게시글":
                    Log.v("알림", "내 게시글 항목 클릭");
                    //FireBase RealTimeDatabase의 [Bulletin]-[AllBulletin]의 Useruid값이 로그인한 유저 Uid값과 동일한 값 불러오기
                    mDatabase.child("Bulletin").child("AllBulletin").orderByChild("Useruid").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            MyPostModel.clear();//리스트 초기화
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                MyPostModel.add(snapshot.getValue(Bulletin.class));
                                Log.v("알림", "성공");
                            }
                            notifyDataSetChanged();//리스트 새로 고침
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("에러", " 데이터 베이스에서 데이터 불러오기 실패");
                        }
                    });
                    break;
                case "추천 게시글":

                    //FireBase RealTimeDatabase [UserLikeCategory]-[user.getUid]값 불러오기
                    mDatabase.child("UserLikeCategory").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String categoryitems = "";
                            MyPostModel.clear(); // 리스트 초기화
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                categoryitems = categoryitems + snapshot.getValue(String.class) + "\n";
                            }

                            Log.v("알림", "리스트 총 문장 ? " + categoryitems);

                            String[] citems = categoryitems.split("\n"); //Split로 citem '\n'기준 나누기

                            if (categoryitems.length() != 0) {
                                if (citems.length == 0) {
                                    Toast.makeText(getApplicationContext(), "선택된 항목이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                                }
                                for (int i = 0; i < citems.length; i++) {
                                    Log.v("알림", "최종 ㅣ " + citems[i]);

                                    //FireBase의 RealTimeDatabase의 [Bulletin]-[citems[i]]의 Useruid값 불러오기
                                    mDatabase.child("Bulletin").child(citems[i]).orderByChild("Useruid").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot2 : dataSnapshot.getChildren()) {
                                                MyPostModel.add(snapshot2.getValue(Bulletin.class));
                                                Log.v("알림", "추천 게시글 모델 : " + MyPostModel);
                                            }
                                            notifyDataSetChanged();//리스트 새로 고침
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.e("에러", "데이터베이스 데이터 불러오기 실패");
                                        }
                                    });
                                }


                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("에러", "데이터베이스 데이터 불러오기 실패");
                        }
                    });

                    break;

                case "동네 검색":
                    finish();
                    startActivity(new Intent(getApplicationContext(), TownSettingActivity.class));
                    break;

            }
        }

        //새로운 ViewHolder 생성 + item_post 레이아웃으로 디자인
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
            return new CustomViewHolder(view);
        }

        //실제 Data 와 ViewHolder 연결
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            ((CustomViewHolder) holder).PostPrice.setText(MyPostModel.get(position).Price);
            ((CustomViewHolder) holder).PostTitle.setText(MyPostModel.get(position).Title);
            Log.v("알림", "글 제목 : " + MyPostModel.get(position).Title);

            //ItemView 클릭 시
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent mypostintent = new Intent(view.getContext(), PostActivity.class);
                    mypostintent.putExtra("Useruid", MyPostModel.get(position).Useruid);
                    mypostintent.putExtra("Title", MyPostModel.get(position).Title);
                    startActivity(mypostintent);
                }
            });
            if (MyPostModel.get(position).BulletinImage1 != null) {
                Glide.with(getApplicationContext()).load(MyPostModel.get(position).BulletinImage1).into(((CustomViewHolder) holder).PostImage);
            }
        }

        //RecyclerView 안에 들어갈 ViewHolder 의 갯수는 MyPostModel의 크기와 같다
        @Override
        public int getItemCount() {
            return MyPostModel.size();
        }

        //ViewHolder에 들어갈 Item 지정
        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView PostImage;
            public TextView PostTitle;
            public TextView PostPrice;

            public CustomViewHolder(View view) {
                super(view);
                PostImage = view.findViewById(R.id.postItem_imageview);
                PostTitle = view.findViewById(R.id.postitem_title);
                PostPrice = view.findViewById(R.id.postitem_price);
            }
        }
    }

}
