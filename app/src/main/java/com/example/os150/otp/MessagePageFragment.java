package com.example.os150.otp;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by os150 on 2020-07-06.
 */

public class MessagePageFragment extends Fragment {
    List<ChatModel> MessagePageModel = new ArrayList<>();
    List<String> destinationUser = new ArrayList<>();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = mAuth.getCurrentUser();

    RecyclerView messagepage_recyclerView;

    String pushId;
    String chatRoomname;

    public MessagePageFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    //RecyclerView message_recyclerView 연결,MessagePageFragmentRecyclerViewAdapter를 RecyclerView에 지정
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messagepage, container, false);
        messagepage_recyclerView = (RecyclerView) view.findViewById(R.id.messagepage_recycleview);
        messagepage_recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        messagepage_recyclerView.setAdapter(new MessagePageFragmentRecyclerViewAdapter());
        return view;
    }

    class MessagePageFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public MessagePageFragmentRecyclerViewAdapter() {
            mDatabase.child("chatroom").orderByChild("users/" + user.getUid()).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    MessagePageModel.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        MessagePageModel.add(snapshot.getValue(ChatModel.class));

                    }

                    notifyDataSetChanged(); //ListView 항목 새로고침
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.v("알림", " 데이터베이스 로드 실패");
                }
            });
            chatRoomname = getActivity().getIntent().getStringExtra("ChatRoomName");
            Log.e("알림", "가져온 채팅방 이름 : " + chatRoomname);

        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatroom, parent, false);
            return new CustomViewHolder(view);
        }

        //실제 Data와 ViewHolder 연결
        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
            //상대방 Uid값 초기화
            String destination = null;
            for (String chatuser : MessagePageModel.get(position).users.keySet()) { //HashMap의 값 가져오기
                if (!chatuser.equals(user.getUid())) { // 본인 Uid와 다를 경우
                    destination = chatuser;
                    destinationUser.add(destination);
                    Log.v("알림", "상대방 Uid : " + destination);


                }
            }

            try {
                //문자열 형태로 내림차순 정렬하여 TreeMap에 저장 TreeMap의 key값은 String이며 value는 ChatModel의 Comment에서 가져온다.
                Map<String, ChatModel.Comment> commentMap = new TreeMap<>(Collections.<String>reverseOrder());
                commentMap.putAll(MessagePageModel.get(position).comments); //TreeMap으로 comments복사
                String lastmessage = commentMap.keySet().toArray()[0].toString(); //TreeMap 의 key의 배열값을 String값으로 변환하여 문자열 저장


                //Database에서 chatuserInfo항목의 상대방 uid NickName 정보 불러오기
                mDatabase.child("chatuserInfo").child(destination).child("nickname").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ((CustomViewHolder) holder).chatroomnickname.setText(dataSnapshot.getValue(String.class)); //VIewHolder의 nickname 에 문자열 지정
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.v("알림", "데이터 불러오기 실패");

                    }
                });
                //Database에서 chatuserInfo 항목의 상대방 UId Profileimage 정보 불러오기
                mDatabase.child("chatuserInfo").child(destination).child("profileimage").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String userprofile = dataSnapshot.getValue(String.class);
                        if (userprofile.charAt(0) != 'a') {
                            Glide.with(holder.itemView.getContext()).load(dataSnapshot.getValue(String.class)).into(((CustomViewHolder) holder).chatroomuserProfile);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.v("알림", " 데이터 불러오기 실패");
                    }
                });


            ((CustomViewHolder) holder).chatroomlastmessage.setText(MessagePageModel.get(position).comments.get(lastmessage).message);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {


                    // Activity간 데이터 전달 ( MessagePageActivity -> MessageActivity 로 상대방 Uid정보 전송 )
                    Intent intent = new Intent(view.getContext(), MessageActivity.class);
                    intent.putExtra("destinationUid", destinationUser.get(position));//상대방 uid
                    ActivityOptions activityOptions = null;

                    //기기의 안드로이드 버전이 Jelly_Bean 이상이어야 함
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(), R.anim.toright, R.anim.toleft);
                        startActivity(intent, activityOptions.toBundle());
                    }

                }
            });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        //RecyclerView 안에 들어갈 ViewHolder의 갯수 = MessagePageModel의 크기
        @Override
        public int getItemCount() {
            return MessagePageModel.size();
        }

    }

    //ViewHolder에 들어갈 Item 지정
    private class CustomViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView chatroomuserProfile;
        public TextView chatroomnickname;
        public TextView chatroomlastmessage;

        public CustomViewHolder(View view) {
            super(view);
            chatroomuserProfile = view.findViewById(R.id.chatroom_imageview_profile);
            chatroomnickname = view.findViewById(R.id.chatroom_textview_nickname);
            chatroomlastmessage = view.findViewById(R.id.chatroom_textView_message);

        }
    }


}
