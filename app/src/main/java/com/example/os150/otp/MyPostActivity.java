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
 */

public class MyPostActivity extends AppCompatActivity {

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

        ActionBar ab = getSupportActionBar();
        ab.hide();


    }

    class MyPostActivityRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public MyPostActivityRecyclerViewAdapter() {
            Intent intent = getIntent();
            switch (intent.getStringExtra("key")) {
                case "내 게시글":
                    Toast.makeText(getApplicationContext(), "내 게시글", Toast.LENGTH_SHORT).show();

                    mDatabase.child("Bulletin").child("AllBulletin").orderByChild("Useruid").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            MyPostModel.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                MyPostModel.add(snapshot.getValue(Bulletin.class));
                                Log.v("알림", "성공");
                            }
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.v("알림", "데이터 읽어오기 실패");
                        }
                    });
                    break;
                case "추천 게시글":
                    mDatabase.child("UserLikeCategory").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String categoryitems = "";
                            MyPostModel.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                categoryitems = categoryitems + snapshot.getValue(String.class) + "\n";
                                Log.v("알림", "DataSnap : " + snapshot.getValue(String.class));

                            }


                            Log.v("알림", "총 문장 ? " + categoryitems);


                            String[] citems = categoryitems.split("\n");

                            if (categoryitems.length() != 0) {
                                if (citems.length == 0) {
                                    Toast.makeText(getApplicationContext(), "선택된 항목이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                                }
                                for (int i = 0; i < citems.length; i++) {
                                    Log.v("알림", "최종 ㅣ " + citems[i]);


                                    mDatabase.child("Bulletin").child(citems[i]).orderByChild("Useruid").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot2 : dataSnapshot.getChildren()) {
                                                MyPostModel.add(snapshot2.getValue(Bulletin.class));
                                                Log.v("알림", "추천 게시글 모델 : " + MyPostModel);
                                            }

                                            notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }


                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    break;

                case "동네 설정":
                    Toast.makeText(getApplicationContext(), "동네 설정", Toast.LENGTH_SHORT).show();
                    break;

            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            ((CustomViewHolder) holder).PostPrice.setText(MyPostModel.get(position).Price);
            ((CustomViewHolder) holder).PostTitle.setText(MyPostModel.get(position).Title);
            Log.v("알림", "글 제목 : " + MyPostModel.get(position).Title);

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
