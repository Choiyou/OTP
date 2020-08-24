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
 */

public class ConnectActivity extends AppCompatActivity {

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
        ActionBar ab = getSupportActionBar();
        ab.hide();
    }

    class ConnectActivityRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public ConnectActivityRecyclerViewAdapter() {
            mDatabase.child("Bulletin").child("AllBulletin").orderByChild("Useruid").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    PostModel.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        PostModel.add(snapshot.getValue(Bulletin.class));
                        Log.v("알림", "Post Model : " + PostModel);
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    Log.v("알림", "데이터 읽어오기 실패");
                }
            });

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            ((CustomViewHolder) holder).PostPrice.setText(PostModel.get(position).Price);
            ((CustomViewHolder) holder).PostTitle.setText(PostModel.get(position).Title);
            Log.v("알림", "test : " + PostModel.get(position).Title);

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
