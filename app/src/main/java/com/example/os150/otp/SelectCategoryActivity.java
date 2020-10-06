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
 * Created by os150 on 2020-07-25.
 */

public class SelectCategoryActivity extends AppCompatActivity {

    List<Bulletin> CategoryModel = new ArrayList<>();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = mAuth.getCurrentUser();

    RecyclerView selectcategory_recyclerView;
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectcategory);

        selectcategory_recyclerView = (RecyclerView) findViewById(R.id.selectcategory_recycleview);
        mLayoutManager = new LinearLayoutManager(this);
        selectcategory_recyclerView.setLayoutManager(mLayoutManager);
        selectcategory_recyclerView.setAdapter(new SelectCategoryAdapter());

        ActionBar ab = getSupportActionBar();
        ab.hide();

    }


    class SelectCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public SelectCategoryAdapter() {
            Intent intent = getIntent();
            switch (intent.getStringExtra("category")) {
                case "kids":
                    mDatabase.child("Bulletin").child("AllBulletin").orderByChild("Category").equalTo("유아동, 유아도서").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            CategoryModel.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                CategoryModel.add(snapshot.getValue(Bulletin.class));
                            }
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.v("알림", "데이터 읽어오기 실패");
                        }
                    });
                    Toast.makeText(getApplicationContext(), "kids", Toast.LENGTH_SHORT).show();
                    break;
                case "man":
                    mDatabase.child("Bulletin").child("AllBulletin").orderByChild("Category").equalTo("남성패션, 잡화").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            CategoryModel.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                CategoryModel.add(snapshot.getValue(Bulletin.class));
                            }
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.v("알림", "데이터 읽어오기 실패");
                        }
                    });
                    Toast.makeText(getApplicationContext(), "man", Toast.LENGTH_SHORT).show();
                    break;
                case "pet":
                    mDatabase.child("Bulletin").child("AllBulletin").orderByChild("Category").equalTo("반려 동물 용품").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            CategoryModel.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                CategoryModel.add(snapshot.getValue(Bulletin.class));
                            }
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.v("알림", "데이터 읽어오기 실패");
                        }
                    });
                    Toast.makeText(getApplicationContext(), "pet", Toast.LENGTH_SHORT).show();
                    break;
                case "buy":
                    mDatabase.child("Bulletin").child("AllBulletin").orderByChild("Category").equalTo("삽니다").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            CategoryModel.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                CategoryModel.add(snapshot.getValue(Bulletin.class));
                            }
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.v("알림", "데이터 읽어오기 실패");
                        }
                    });
                    Toast.makeText(getApplicationContext(), "buy", Toast.LENGTH_SHORT).show();
                    break;
                case "recycling":
                    mDatabase.child("Bulletin").child("AllBulletin").orderByChild("Category").equalTo("기타 중고 용품").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            CategoryModel.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                CategoryModel.add(snapshot.getValue(Bulletin.class));
                            }
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.v("알림", "데이터 읽어오기 실패");
                        }
                    });
                    Toast.makeText(getApplicationContext(), "recycling", Toast.LENGTH_SHORT).show();
                    break;
                case "book":
                    mDatabase.child("Bulletin").child("AllBulletin").orderByChild("Category").equalTo("도서, 티켓, 음반").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            CategoryModel.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                CategoryModel.add(snapshot.getValue(Bulletin.class));
                            }
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.v("알림", "데이터 읽어오기 실패");
                        }
                    });
                    Toast.makeText(getApplicationContext(), "book", Toast.LENGTH_SHORT).show();
                    break;
                case "free":
                    mDatabase.child("Bulletin").child("AllBulletin").orderByChild("Category").equalTo("무료 나눔, 대여").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            CategoryModel.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                CategoryModel.add(snapshot.getValue(Bulletin.class));
                            }
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.v("알림", "데이터 읽어오기 실패");
                        }
                    });
                    Toast.makeText(getApplicationContext(), "free", Toast.LENGTH_SHORT).show();
                    break;
                case "game":
                    mDatabase.child("Bulletin").child("AllBulletin").orderByChild("Category").equalTo("게임, 취미").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            CategoryModel.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                CategoryModel.add(snapshot.getValue(Bulletin.class));
                            }
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.v("알림", "데이터 읽어오기 실패");
                        }
                    });
                    Toast.makeText(getApplicationContext(), "game", Toast.LENGTH_SHORT).show();
                    break;
                case "sports":
                    mDatabase.child("Bulletin").child("AllBulletin").orderByChild("Category").equalTo("스포츠, 레저").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            CategoryModel.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                CategoryModel.add(snapshot.getValue(Bulletin.class));
                            }
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.v("알림", "데이터 읽어오기 실패");
                        }
                    });
                    Toast.makeText(getApplicationContext(), "sports", Toast.LENGTH_SHORT).show();
                    break;
                case "furniture":
                    mDatabase.child("Bulletin").child("AllBulletin").orderByChild("Category").equalTo("가구, 인테리어").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            CategoryModel.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                CategoryModel.add(snapshot.getValue(Bulletin.class));
                            }
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.v("알림", "데이터 읽어오기 실패");
                        }
                    });
                    Toast.makeText(getApplicationContext(), "furniture", Toast.LENGTH_SHORT).show();
                    break;
                case "digital":
                    mDatabase.child("Bulletin").child("AllBulletin").orderByChild("Category").equalTo("디지털, 가전").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            CategoryModel.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                CategoryModel.add(snapshot.getValue(Bulletin.class));
                            }
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.v("알림", "데이터 읽어오기 실패");
                        }
                    });
                    Toast.makeText(getApplicationContext(), "digital", Toast.LENGTH_SHORT).show();
                    break;
                case "woman":
                    mDatabase.child("Bulletin").child("AllBulletin").orderByChild("Category").equalTo("여성의류").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            CategoryModel.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                CategoryModel.add(snapshot.getValue(Bulletin.class));
                            }
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.v("알림", "데이터 읽어오기 실패");
                        }
                    });
                    Toast.makeText(getApplicationContext(), "woman", Toast.LENGTH_SHORT).show();
                    break;
                case "womanthings":
                    mDatabase.child("Bulletin").child("AllBulletin").orderByChild("Category").equalTo("여성잡화").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            CategoryModel.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                CategoryModel.add(snapshot.getValue(Bulletin.class));
                            }
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.v("알림", "데이터 읽어오기 실패");
                        }
                    });
                    Toast.makeText(getApplicationContext(), "womanthings", Toast.LENGTH_SHORT).show();
                    break;
                case "beauty":
                    mDatabase.child("Bulletin").child("AllBulletin").orderByChild("Category").equalTo("뷰티, 미용").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            CategoryModel.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                CategoryModel.add(snapshot.getValue(Bulletin.class));
                            }
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.v("알림", "데이터 읽어오기 실패");
                        }
                    });
                    Toast.makeText(getApplicationContext(), "beauty", Toast.LENGTH_SHORT).show();
                    break;
                case "processedfood":
                    mDatabase.child("Bulletin").child("AllBulletin").orderByChild("Category").equalTo("생활, 가공식품").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            CategoryModel.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                CategoryModel.add(snapshot.getValue(Bulletin.class));
                            }
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.v("알림", "데이터 읽어오기 실패");
                        }
                    });
                    Toast.makeText(getApplicationContext(), "processedfood", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            ((CustomViewHolder) holder).category_price.setText(CategoryModel.get(position).Price);
            ((CustomViewHolder) holder).category_title.setText(CategoryModel.get(position).Title);
            Log.v("알림", "test : " + CategoryModel.get(position).Title);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent categoryintent = new Intent(view.getContext(), PostActivity.class);
                    categoryintent.putExtra("Useruid", CategoryModel.get(position).Useruid);
                    categoryintent.putExtra("Title", CategoryModel.get(position).Title);
                    startActivity(categoryintent);
                }
            });
            if (CategoryModel.get(position).BulletinImage1 != null) {

                Glide.with(getApplicationContext()).load(CategoryModel.get(position).BulletinImage1).into(((CustomViewHolder) holder).categoryImageView);
            }
        }

        @Override
        public int getItemCount() {

            Log.v("알림", "ChatModel 크기 : " + CategoryModel.size());
            return CategoryModel.size();
        }


    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {
        public ImageView categoryImageView;
        public TextView category_title;
        public TextView category_price;


        public CustomViewHolder(View view) {
            super(view);
            categoryImageView = view.findViewById(R.id.postItem_imageview);
            category_title = view.findViewById(R.id.postitem_title);
            category_price = view.findViewById(R.id.postitem_price);
        }
    }


}
