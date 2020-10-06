package com.example.os150.otp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by os150 on 2020-07-16.
 */

public class MessageActivity extends AppCompatActivity {
    Button sendbtn;
    EditText messageedit;
    RecyclerView message_recyclerView;
    String chatRoomname;
    String destinationUid;
    String pushId;
    SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm");
    UserModel destinationuserModel;


    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = mAuth.getCurrentUser();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        sendbtn = (Button) findViewById(R.id.sendbtn);
        messageedit = (EditText) findViewById(R.id.messageedit);
        message_recyclerView = (RecyclerView) findViewById(R.id.messageActivity_recyclerview);
        destinationUid = getIntent().getStringExtra("destinationUid");

        //ActionBar 숨기기
        ActionBar ab = getSupportActionBar();
        ab.hide();


        //채팅방 확인 함수
        ChackChatRoom();

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ChatModel chatModel = new ChatModel();

                //ChatUser의 로그인 user와 상대방 Uid chatModel에 저장
                chatModel.users.put(user.getUid(), true);
                chatModel.users.put(destinationUid, true);

                //채팅방이 없을 경우
                if (chatRoomname == null) {

                    //Database에 chatModel 값 저장 ( 로그인 user, 상대방 )
                    DatabaseReference pushChatroom = mDatabase.child("chatroom").push();
                    pushId = pushChatroom.getKey();
                    Log.e("알림", "푸쉬값? : " + pushId);

                    pushChatroom.setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            ChackChatRoom(); // 채팅방 확인 함수
                            Log.v("알림", "채팅 방 생성 성공");
                        }
                    });


                }
                //채팅방이 있을 경우
                else {
                    final ChatModel.Comment comment = new ChatModel.Comment();
                    //ChatModel의 Comment에 uid, message, timestamp 저장
                    comment.uid = user.getUid();
                    comment.message = messageedit.getText().toString();
                    comment.timestamp = ServerValue.TIMESTAMP;

                    //message 입력 창이 비어있지 않아야 전송
                    if (messageedit.length() != 0) {
                        //DataBase에 메시지 입력
                        mDatabase.child("chatroom").child(chatRoomname).child("comments").push().setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                messageedit.setText("");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.v("알림", "오류" + e);
                            }
                        });
                    }
                }
            }
        });


    }


    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<ChatModel.Comment> comments = new ArrayList<>();

        public RecyclerViewAdapter() {
            //Database에서 "chatuserInfo"에서 상대방 Uid의 데이터 가져오기
            mDatabase.child("chatuserInfo").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    destinationuserModel = dataSnapshot.getValue(UserModel.class);
                    getMessage();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.v("알림", "데이터 불러오기 실패");
                }
            });
        }

        void getMessage() {
            //채팅방의 comment 값 가져오기
            mDatabase.child("chatroom").child(chatRoomname).child("comments").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //리스트 초기화
                    comments.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        comments.add(snapshot.getValue(ChatModel.Comment.class));
                    }
                    notifyDataSetChanged(); // Listview 항목 새로 고침
                    message_recyclerView.scrollToPosition(comments.size() - 1); // 메시지 List의 가장 하단으로 스크롤
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.v("알림", "데이터 불러오기 실패");
                }
            });
        }

        //item_message와 연결
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
            return new MessageViewHolder(view);
        }

        //실제 Data와 VIewHolder연결
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            MessageViewHolder messageViewHolder = ((MessageViewHolder) holder);

            long unixTime = (long) comments.get(position).timestamp;

            Date date = new Date(unixTime);
            format.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = format.format(date);

            //메시지의 Uid가 로그인한 User의 Uid와 동일한 경우 ( 내가 보낸 메시지 )
            if (comments.get(position).uid.equals(user.getUid())) {
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.linearlayout_destination.setVisibility(message_recyclerView.INVISIBLE);
                messageViewHolder.textView_message.setTextSize(16);
                messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT); // 오른쪽에 표시
            }
            //아닌 경우 ( 상대방이 보낸 메시지 )
            else {
                messageViewHolder.textView_nickname.setText(destinationuserModel.nickname);
                messageViewHolder.linearlayout_destination.setVisibility(View.VISIBLE);
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setTextSize(16);
                if (destinationuserModel.profileimage.charAt(0) != 'a') {
                    Glide.with(messageViewHolder.itemView.getContext()).load(destinationuserModel.profileimage).into(messageViewHolder.imageview_profile);
                } else {
                    Glide.with(messageViewHolder.itemView.getContext()).load(R.drawable.drawable_userimage).into(messageViewHolder.imageview_profile);
                }
                messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT); // 왼쪽에 표시
            }

            // 메시지 보낸 시간 표시
            messageViewHolder.textView_timestamp.setText(time);
        }

        //RecyclerVIew 안에 들어갈 VIewHolder의 갯수는 comments의 크기와 같다
        @Override
        public int getItemCount() {
            return comments.size();
        }
    }


    //ViewHolder에 들어갈 Item 지정
    private class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView textView_message;
        TextView textView_timestamp;
        TextView textView_nickname;
        CircleImageView imageview_profile;
        LinearLayout linearlayout_destination;
        LinearLayout linearLayout_main;

        public MessageViewHolder(View view) {
            super(view);
            textView_message = (TextView) view.findViewById(R.id.messageItem_textView_message);
            textView_timestamp = (TextView) view.findViewById(R.id.messageItem_textView_timestamp);
            textView_nickname = (TextView) view.findViewById(R.id.messageItem_textview_nickname);
            imageview_profile = (CircleImageView) view.findViewById(R.id.messageItem_imageview_profile);
            linearlayout_destination = (LinearLayout) view.findViewById(R.id.messageItem_linearlayout_destination);
            linearLayout_main = (LinearLayout) view.findViewById(R.id.messageItem_LinearLayout);
        }
    }

    //채팅방 확인 함수
    void ChackChatRoom() {
        //DataBase에서 "chatroom"항목의 users가 로그인한 유저의 Uid와 동일한 경우
        mDatabase.child("chatroom").orderByChild("users/" + user.getUid()).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    ChatModel chatModel = snapshot.getValue(ChatModel.class); //chatModel에 그 값 불러온다
                    if (chatModel.users.containsKey(destinationUid)) { // 만약 불러온 user의 값이 상대방 Uid와 동일한 경우
                        chatRoomname = snapshot.getKey(); //채팅방 이름 가져오고
                        sendbtn.setEnabled(true);
                        message_recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                        message_recyclerView.setAdapter(new RecyclerViewAdapter());
                        Log.v("알림", "ChatRoomname : " + chatRoomname);


                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("알림", "데이터 불러오기 실패");

            }
        });

    }


}
