package com.example.os150.otp;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
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
 * MessagePageFragment 자바 파일
 * 기능 : RecyclerView message_recyclerView 연결,MessagePageFragmentRecyclerViewAdapter를 RecyclerView에 지정
 *      : MessagePageFragment 목록 Adapter 생성
 *      : item_chatroom와 연결된 ViewHolder 생성
 *      : itemView 클릭 시 putExtra 통해 destination Uid 데이터 MessageActivity 로 전송 및 Activity 화면 전환
 *
 */

public class MessagePageFragment extends Fragment {

    //ChatModel 타입의 리스트
    //String 타입의 리스트
    List<ChatModel> MessagePageModel = new ArrayList<>();
    List<String> destinationUser = new ArrayList<>();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = mAuth.getCurrentUser();

    RecyclerView messagepage_recyclerView;

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

    //MessagePageFragment 목록 Adapter 생성
    class MessagePageFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public MessagePageFragmentRecyclerViewAdapter() {
            //FireBase RealTimeDataBase [chatroom]의 users의 로그인유저 Uid가 True일 경우
            mDatabase.child("chatroom").orderByChild("users/" + user.getUid()).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    MessagePageModel.clear(); //MessagePageModel 초기화

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        MessagePageModel.add(snapshot.getValue(ChatModel.class));

                    }

                    notifyDataSetChanged(); //ListView 항목 새로고침

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("에러", " 데이터베이스에서 데이터 불러오기 실패");
                }
            });
            chatRoomname = getActivity().getIntent().getStringExtra("ChatRoomName");
            Log.e("알림", "가져온 채팅방 이름 : " + chatRoomname);

        }

        //리스트 항목 표시하기 위한 뷰 생성 & 해당 뷰 관리할 VIewHolder 생성 후 리턴
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatroom, parent, false);
            return new CustomViewHolder(view);
        }

        //ViewHolder 객체에 Position 기반의 데이터 표시
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


                //FireBase RealTimeDataBase 의 [chatuserInfo]-[destination]-[nickname] 값 불러오기
                mDatabase.child("chatuserInfo").child(destination).child("nickname").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ((CustomViewHolder) holder).chatroomnickname.setText(dataSnapshot.getValue(String.class)); //VIewHolder의 nickname 에 문자열 지정
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("에러", "데이터베이스에서 데이터 불러오기 실패");

                    }
                });
                //FireBase RealTimeDataBase [chatuserInfo]-[destination]-[profileimage]값 불러오기
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
                        Log.e("에러", "데이터베이스 데이터 불러오기 실패");
                    }
                });


                ((CustomViewHolder) holder).chatroomlastmessage.setText(MessagePageModel.get(position).comments.get(lastmessage).message);
                //목록 ItemView 클릭 시 MessageActivity 화면 전환
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {

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
