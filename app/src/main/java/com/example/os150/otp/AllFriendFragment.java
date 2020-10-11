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
 * AllFriendFragment 자바 파일
 * 기능 : AllFriend Fragment RecyclerViewAdapter RecyclerView 에 지정
 *      : AllFriend Fragment 목록 Adapter생성
 *      : item_chatuser와 연결된 ViewHolder 생성
 *      : itemView 클릭 시 팝업 메뉴 생성
 *        팝업 메뉴 = 친구 추가 / 신고하기 / 채팅하기
 *        각 메뉴 클릭시 기능에 맞게 수행
 */

public class AllFriendFragment extends Fragment {
    //UserModel 타입의 리스트 선언
    List<UserModel> AllFriendModels = new ArrayList<>();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = mAuth.getCurrentUser();

    RecyclerView allfriend_recyclerView;

    public AllFriendFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //RecyclerView allfirend_recyclerView 연결, AllFriendFragmentRecyclerViewAdapter를 RecyclerView에 지정
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_allfriend, container, false);
        allfriend_recyclerView = (RecyclerView) view.findViewById(R.id.allfriend_recycleview);
        allfriend_recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        allfriend_recyclerView.setAdapter(new AllFriendFragmentRecyclerViewAdapter());
        return view;
    }

    //AllFriend Fragment 목록 Adapter 생성

    class AllFriendFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public AllFriendFragmentRecyclerViewAdapter() {


            //FireBase RealTimeDataBase의 [chatuserInfo]의 nickname값 불러오기
            mDatabase.child("chatuserInfo").orderByChild("nickname").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    AllFriendModels.clear();//AllFriendModel리스트 초기화
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        AllFriendModels.add(snapshot.getValue(UserModel.class));
                    }
                    notifyDataSetChanged(); // ListVIew 항목 새로 고침
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatuser, parent, false);
            return new CustomViewHolder(view);
        }

        //ViewHolder 객체에 Position 기반의 데이터 표시
        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
            String uprofile = AllFriendModels.get(position).profileimage;
            final Uri uprofiles = Uri.parse(uprofile);


            //리스트에서 본인 Uid의 데이터를 가진 ItemView 안보이도록 설정
            if (AllFriendModels.get(position).uid.equals(user.getUid())) {
                holder.itemView.setVisibility(View.INVISIBLE);
                holder.itemView.getLayoutParams().height = 0;
                return;
            }

            if (uprofile.charAt(0) != 'a') {
                Glide.with(holder.itemView.getContext()).load(uprofiles).into(((CustomViewHolder) holder).chatuserP);
                ((CustomViewHolder) holder).cnickname.setText(AllFriendModels.get(position).nickname);


            } else {
                ((CustomViewHolder) holder).cnickname.setText(AllFriendModels.get(position).nickname);
                Glide.with(holder.itemView.getContext()).load(R.drawable.drawable_userimage).into(((CustomViewHolder) holder).chatuserP);

            }


            //목록 ItemView 클릭 시 PopupMenu 생성
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    PopupMenu allfriend_popup = new PopupMenu(getActivity(), holder.itemView);
                    allfriend_popup.inflate(R.menu.option_menu);
                    allfriend_popup.setGravity(Gravity.RIGHT);
                    allfriend_popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            UserModel userModel = new UserModel();
                            userModel.nickname = AllFriendModels.get(position).nickname;
                            userModel.profileimage = AllFriendModels.get(position).profileimage;
                            userModel.uid = AllFriendModels.get(position).uid;

                            switch (menuItem.getItemId()) {
                                // Popup 메뉴에서 친구추가 클릭 시
                                case R.id.insert:
                                    //FireBase RealTimeDataBase 의 [myfriendlist]-[user.getUid]-[상대방 Uid]값 추가
                                    mDatabase.child("myfriendlist").child(user.getUid()).child(AllFriendModels.get(position).uid).setValue(userModel);
                                    break;

                                //Popup 메뉴에서 신고하기 클릭 시
                                case R.id.notify:
                                    final EditText notifymessage = new EditText(getContext());
                                    notifymessage.getTransformationMethod();
                                    // AlertDialog 생성
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

                                                //FireBase RealTimeDataBase의 [admin]-[notify]-[message]-[user.getUid]-[상대방 nickname]에 editText에서 입력받은 값 설정
                                                mDatabase.child("admin").child("notify").child("message").child(user.getUid()).child(AllFriendModels.get(position).nickname).setValue(notifymessage.getText().toString());

                                            }
                                            Toast.makeText(getContext(), "신고가 접수되었습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Log.v("알림", "신고하기 다이얼로그 취소 버튼 클릭");
                                        }
                                    });
                                    notifyalert.show();
                                    break;

                                //Popup 메뉴에서 대화하기 클릭 시
                                case R.id.message:
                                    Intent allfriendintent = new Intent(view.getContext(), MessageActivity.class);
                                    allfriendintent.putExtra("destinationUid", AllFriendModels.get(position).uid);//상대방 uid
                                    ActivityOptions activityOptions = null;
                                    //기기의 안드로이드 버전이 JellyBean 이상이어야 함
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(), R.anim.toright, R.anim.toleft);
                                        startActivity(allfriendintent, activityOptions.toBundle());
                                    }
                                    break;
                            }
                            return false;
                        }
                    });

                    allfriend_popup.show();
                }
            });

        }

        //RecyclerView안에 들어갈 ViewHolder 의 갯수 = AllFriendModels 의 크기
        @Override
        public int getItemCount() {
            return AllFriendModels.size();
        }

    }

    //ViewHolder에 들어갈 Item 지정
    private class CustomViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView chatuserP;
        public TextView cnickname;

        public CustomViewHolder(View view) {
            super(view);
            chatuserP = view.findViewById(R.id.chatuseritem_iv);
            cnickname = view.findViewById(R.id.chatuseritem_tv);


        }
    }

}