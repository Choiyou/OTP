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
 * Created by os150 on 2020-07-25.
 * 카테고리 버튼 선택시 전환된 페이지
 * 기능 : Category Activity 에서 getStringExtra 통해 가져온 값 FireBase RealTimeDataBase 값과 비교 후 리스트에 Bulletin 값 추가
 *      : item_post 레이아웃과 연결된 ViewHolder 생성
 *      : 선택된 ItemView의 Useruid & Title값 putExtra를 통해 PostActivity로 값 전달 및 Activity 화면 전환
 */

public class SelectCategoryActivity extends AppCompatActivity {
    //Bulletin 객체 타입의 리스트
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

        //ActionBar 숨기기
        ActionBar ab = getSupportActionBar();
        ab.hide();

    }

    //SelectCategory 목록 Adapter 생성
    class SelectCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public SelectCategoryAdapter() {
            Intent cintent = getIntent(); //Intent 값 가져오기
            switch (cintent.getStringExtra("category")) {
                case "kids":
                    //FireBase RealTimeDataBase 의 [Bulletin]-[AllBulletin]의 category 값과 '유아동, 유아도서' 값 비교 후 값 가져오기
                    mDatabase.child("Bulletin").child("AllBulletin").orderByChild("Category").equalTo("유아동, 유아도서").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            CategoryModel.clear(); // CategoryModel ArrayList 초기화
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                CategoryModel.add(snapshot.getValue(Bulletin.class));
                            }
                            notifyDataSetChanged(); // 새로 고침
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("에러", "데이터베이스에서 데이터 불러오기 실패");
                        }
                    });
                    break;

                case "man":
                    //FireBase RealTimeDataBase 의 [Bulletin]-[AllBulletin]의 category 값과 '남성패션, 잡화' 값 비교 후 값 가져오기
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
                            Log.e("에러", "데이터베이스에서 데이터 불러오기 실패");
                        }
                    });
                    break;

                case "pet":
                    //FireBase RealTimeDataBase 의 [Bulletin]-[AllBulletin]의 category 값과 '반려 동물 용품' 값 비교 후 값 가져오기
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
                            Log.e("에러", "데이터베이스에서 데이터 불러오기 실패");
                        }
                    });
                    break;
                case "buy":
                    //FireBase RealTimeDataBase 의 [Bulletin]-[AllBulletin]의 category 값과 '삽니다' 값 비교 후 값 가져오기
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
                            Log.e("에러", "데이터베이스에서 데이터 불러오기 실패");
                        }
                    });
                    break;

                case "recycling":
                    //FireBase RealTimeDataBase 의 [Bulletin]-[AllBulletin]의 category 값과 '기타 중고 용품' 값 비교 후 값 가져오기
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
                            Log.e("에러", "데이터베이스에서 데이터 불러오기 실패");
                        }
                    });
                    break;

                case "book":
                    //FireBase RealTimeDataBase 의 [Bulletin]-[AllBulletin]의 category 값과 '도서, 티켓, 음반' 값 비교 후 값 가져오기
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
                            Log.e("에러", "데이터베이스에서 데이터 불러오기 실패");
                        }
                    });
                    break;

                case "free":
                    //FireBase RealTimeDataBase 의 [Bulletin]-[AllBulletin]의 category 값과 '무료 나눔, 대여' 값 비교 후 값 가져오기
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
                            Log.e("에러", "데이터베이스에서 데이터 불러오기 실패");
                        }
                    });
                    break;

                case "game":
                    //FireBase RealTimeDataBase 의 [Bulletin]-[AllBulletin]의 category 값과 '게임, 취미' 값 비교 후 값 가져오기
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
                            Log.e("에러", "데이터베이스에서 데이터 불러오기 실패");
                        }
                    });
                    break;

                case "sports":
                    //FireBase RealTimeDataBase 의 [Bulletin]-[AllBulletin]의 category 값과 '스포츠, 레저' 값 비교 후 값 가져오기
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
                            Log.e("에러", "데이터베이스에서 데이터 불러오기 실패");
                        }
                    });
                    break;

                case "furniture":
                    //FireBase RealTimeDataBase 의 [Bulletin]-[AllBulletin]의 category 값과 '가구, 인테리어' 값 비교 후 값 가져오기
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
                            Log.e("에러", "데이터베이스에서 데이터 불러오기 실패");
                        }
                    });
                    break;

                case "digital":
                    //FireBase RealTimeDataBase 의 [Bulletin]-[AllBulletin]의 category 값과 '디지털, 가전' 값 비교 후 값 가져오기
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
                            Log.e("에러", "데이터베이스에서 데이터 불러오기 실패");
                        }
                    });
                    break;

                case "woman":
                    //FireBase RealTimeDataBase 의 [Bulletin]-[AllBulletin]의 category 값과 '여성의류' 값 비교 후 값 가져오기
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
                            Log.e("에러", "데이터베이스에서 데이터 불러오기 실패");
                        }
                    });
                    break;

                case "womanthings":
                    //FireBase RealTimeDataBase 의 [Bulletin]-[AllBulletin]의 category 값과 '여성잡화' 값 비교 후 값 가져오기
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
                            Log.e("에러", "데이터베이스에서 데이터 불러오기 실패");
                        }
                    });
                    break;

                case "beauty":
                    //FireBase RealTimeDataBase 의 [Bulletin]-[AllBulletin]의 category 값과 '뷰티, 미용' 값 비교 후 값 가져오기
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
                            Log.e("에러", "데이터베이스에서 데이터 불러오기 실패");
                        }
                    });
                    break;

                case "processedfood":
                    //FireBase RealTimeDataBase 의 [Bulletin]-[AllBulletin]의 category 값과 '생활, 가공식품' 값 비교 후 값 가져오기
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
                            Log.e("에러", "데이터베이스에서 데이터 불러오기 실패");
                        }
                    });
                    break;

            }

        }

        //리스트 항목 표시하기 위한 뷰 생성 & 해당 뷰 관리할 VIewHolder 생성 후 리턴
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false); //뷰 생성
            return new CustomViewHolder(view);
        }

        //ViewHolder 객체에 Position 기반의 데이터 표시
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            ((CustomViewHolder) holder).category_price.setText(CategoryModel.get(position).Price);
            ((CustomViewHolder) holder).category_title.setText(CategoryModel.get(position).Title);

            Log.v("알림", "test : " + CategoryModel.get(position).Title);

            //목록 ItemView 클릭 시 게시글 페이지로 전환 및 데이터 전송
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

        //아이템의 갯수 CategoryModel 크기만큼 반환
        @Override
        public int getItemCount() {
            Log.v("알림", "ChatModel 크기 : " + CategoryModel.size());
            return CategoryModel.size();
        }


    }


    //ViewHolder에 들어갈 Item 지정
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
