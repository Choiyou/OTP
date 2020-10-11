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
 * Created by os150 on 2020-05-19.
 * ConnectActivity 자바 파일
 * 기능 : Item_post 레이아웃과 연결한 ViewHolder생성
 *      : 생성된 ViewHolder에 Data 연결
 *      : 선택된 ItemView의 UserUid와 Title 값을 putExtra을 이용하여 PostActivity로 전송
 *      : PostActivity로 전환
 */

public class ConnectActivity extends AppCompatActivity {

    //Bulletin 타입의 리스트 선언
    List<Bulletin> PostModel = new ArrayList<>();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = mAuth.getCurrentUser();

    RecyclerView main_recyclerView;
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        main_recyclerView = (RecyclerView) findViewById(R.id.main_recycleview);
        mLayoutManager = new LinearLayoutManager(this);

        main_recyclerView.setLayoutManager(mLayoutManager);
        main_recyclerView.setAdapter(new ConnectActivityRecyclerViewAdapter());

        //ActionBar 숨기기
        ActionBar ab = getSupportActionBar();
        ab.hide();

    }

    //목록 어뎁터 추가
    class ConnectActivityRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public ConnectActivityRecyclerViewAdapter() {
            //Firebase의 RealTimeDataBase에서 [Bulletin]-[AllBulletin]항목의 Useruid값 불러오기
            mDatabase.child("Bulletin").child("AllBulletin").orderByChild("Useruid").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    PostModel.clear(); //PostModel 리스트 초기화
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        PostModel.add(snapshot.getValue(Bulletin.class)); //PostModel에 불러온값 추가
                        // Log.v("알림", "Post Model : " + PostModel);
                    }
                    notifyDataSetChanged(); //새로고침
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("에러", "데이터베이스에서 데이터 불러오기 실패");
                }
            });

        }

        //리스트 항목 표시하기 위한 뷰 생성 & 해당 뷰 관리할 VIewHolder 생성 후 리턴
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
            return new CustomViewHolder(view);
        }


        //ViewHolder 객체에 Position 기반의 데이터 표시
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

            ((CustomViewHolder) holder).PostPrice.setText(PostModel.get(position).Price);
            ((CustomViewHolder) holder).PostTitle.setText(PostModel.get(position).Title);

            //목록 ItemView 클릭 시 게시글 페이지로 전환 및 데이터 전송
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent postintent = new Intent(view.getContext(), PostActivity.class);
                    postintent.putExtra("Useruid", PostModel.get(position).Useruid);
                    postintent.putExtra("Title", PostModel.get(position).Title);
                    startActivity(postintent);
                }
            });
            if (PostModel.get(position).BulletinImage1 != null) {
                Glide.with(getApplicationContext()).load(PostModel.get(position).BulletinImage1).into(((CustomViewHolder) holder).PostImage);
            }
        }

        //RecyclerView 안에 들어갈 ViewHolder 의 갯수 = PostModels 의 크기
        @Override
        public int getItemCount() {
            return PostModel.size();
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
