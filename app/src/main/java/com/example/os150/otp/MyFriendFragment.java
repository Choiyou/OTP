package com.example.os150.otp;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
 * Created by os150 on 2020-07-06.
 * MyFriendFragment 자바 파일
 * 기능 : RecyclerView myFriend_recyclerView 연결,MyFriendFragmentRecyclerViewAdapter를 RecyclerView에 지정
 *      : MyFriendFragment 목록 어뎁터 생성
 *      : item_chatuser와 연결된 ViewHolder 생성
 *      : itemView 클릭 시 팝업 메뉴 생성
 *        팝업 메뉴 = 친구 삭제 / 신고하기 / 채팅하기
 *        각 기능에 맞게 수행
 */

public class MyFriendFragment extends Fragment {

    //UserModel 타입의 리스트
    List<UserModel> MyFriendModels = new ArrayList<>();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = mAuth.getCurrentUser();

    RecyclerView myfriend_recyclerView;

    public MyFriendFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    //RecyclerView myFriend_recyclerView 연결,MyFriendFragmentRecyclerViewAdapter를 RecyclerView에 지정
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myfriend, container, false);
        myfriend_recyclerView = (RecyclerView) view.findViewById(R.id.myfriend_recycleview);
        myfriend_recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        myfriend_recyclerView.setAdapter(new MyFriendFragmentRecyclerViewAdapter());
        myfriend_recyclerView.getAdapter().notifyDataSetChanged();
        return view;
    }

    //MyFriend Fragment 목록 생성
    class MyFriendFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public MyFriendFragmentRecyclerViewAdapter() {
            //FireBase RealTimeDataBase [myfriendlist]-[user.getUid]의 nickname 값 불러와
            mDatabase.child("myfriendlist").child(user.getUid()).orderByChild("nickname").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    MyFriendModels.clear(); //리스트 초기화
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        MyFriendModels.add(snapshot.getValue(UserModel.class));

                    }
                    notifyDataSetChanged(); // ListVIew 항목 새로 고침


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("에러", "데이터 베이스에서 데이터 불러오기 실패");
                }
            });


        }

        //리스트 항목 표시하기 위한 뷰 생성 & 해당 뷰 관리할 VIewHolder 생성 후 리턴
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatuser, parent, false);
            return new CustomViewHolder(view);
        }

        //ViewHolder 객체에 Position 기반의 데이터 표시
        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
            String uriprofile = MyFriendModels.get(position).profileimage;
            final Uri uriprofiles = Uri.parse(uriprofile);

            if (uriprofile.charAt(0) != 'a') {
                Glide.with(holder.itemView.getContext()).load(uriprofiles).into(((CustomViewHolder) holder).chatuserProfile);
                ((CustomViewHolder) holder).chatnickname.setText(MyFriendModels.get(position).nickname);
            } else {
                Glide.with(holder.itemView.getContext()).load(R.drawable.drawable_userimage).into(((CustomViewHolder) holder).chatuserProfile);
                ((CustomViewHolder) holder).chatnickname.setText(MyFriendModels.get(position).nickname);
            }
            //목록 ItemView 클릭 시 Popup 생성
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    //Popup 생성
                    PopupMenu myfriendpopup = new PopupMenu(getActivity(), holder.itemView);
                    myfriendpopup.inflate(R.menu.myfriend_menu); //myfriend_menu 와 연결
                    myfriendpopup.setGravity(Gravity.RIGHT); // popup 메뉴 오른쪽에 위치
                    myfriendpopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                //Popup 메뉴에서 친구 삭제 클릭 시
                                case R.id.delete:
                                    Log.v("알림", "선택 유저: " + MyFriendModels.get(position).uid);
                                    //FireBase의 RealTimeDataBase에서 [myfriendlist]-[user.getUid]-[상대방Uid]값 제거
                                    mDatabase.child("myfriendlist").child(user.getUid()).child(MyFriendModels.get(position).uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.v("알림", "데이터 삭제 성공");

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("에러", "데이터베이스에 데이터 삭제 실패");
                                        }

                                    });
                                    refresh(); // 데이터 삭제후 Fragment 갱신
                                    break;
                                //Popup메뉴에서 신고하기 클릭 시
                                case R.id.notify:
                                    final EditText notifymessage = new EditText(getContext());
                                    notifymessage.getTransformationMethod();
                                    //AlertDialog 생성
                                    AlertDialog.Builder notifyalert = new AlertDialog.Builder(getContext());
                                    notifyalert.setTitle("신고하기");
                                    notifyalert.setView(notifymessage);
                                    notifyalert.setMessage("신고 내용을 입력해주세요.");
                                    notifyalert.setCancelable(false).setPositiveButton("전송", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (notifymessage.getText().length() == 0) {
                                                return;
                                            } else {
                                                //FireBase RealTimeDatabase [admin]-[notify]-[message]-[로그인한 userUid]-[상대방 Uid] 에 editText 통해 입력된 데이터 값 저장
                                                mDatabase.child("admin").child("notify").child("message").child(user.getUid()).child(MyFriendModels.get(position).nickname).setValue(notifymessage.getText().toString());

                                            }
                                            Toast.makeText(getContext(), "신고가 접수되었습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Toast.makeText(getContext(), "취소", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    notifyalert.show();
                                    break;
                                //Popup 메뉴에서 대화하기 클릭 시
                                case R.id.message:
                                    Intent myfriendintent = new Intent(view.getContext(), MessageActivity.class);
                                    myfriendintent.putExtra("destinationUid", MyFriendModels.get(position).uid);//상대방 uid
                                    ActivityOptions activityOptions = null;
                                    // 기기의 안드로이드 버전이 Jelly_Bean 이상일 경우에만 작동
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(), R.anim.toright, R.anim.toleft);
                                        startActivity(myfriendintent, activityOptions.toBundle());
                                    }
                                    break;
                            }
                            return false;
                        }
                    });

                    myfriendpopup.show();
                }
            });

        }

        //RecyclerView안에 들어갈 ViewHolder 의 갯수 = MyFriendModels 의 크기
        @Override
        public int getItemCount() {
            return MyFriendModels.size();
        }

    }

    //ViewHolder에 들어갈 Item 지정
    private class CustomViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView chatuserProfile;
        public TextView chatnickname;

        public CustomViewHolder(View view) {
            super(view);
            chatuserProfile = view.findViewById(R.id.chatuseritem_iv);
            chatnickname = view.findViewById(R.id.chatuseritem_tv);

        }
    }

    //Fragment 새로 고침 함수
    public void refresh() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction(); //FragmentTrasaction 참조 객체 가져오기
        transaction.detach(this).attach(this).commit(); // FragmentTransaction Fragment가 존재할 경우 떼어 내고 다시 붙이는 작업
    }

}
