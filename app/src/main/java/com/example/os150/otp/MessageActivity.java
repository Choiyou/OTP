package com.example.os150.otp;

import android.os.Bundle;
import android.os.PersistableBundle;
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
    RecyclerView recyclerView;
    String chatRoomname;
    String destinationUid;
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
        recyclerView = (RecyclerView) findViewById(R.id.messageActivity_recyclerview);
        destinationUid = getIntent().getStringExtra("destinationUid");
        ActionBar ab = getSupportActionBar();
        ab.hide();

        checkchatRoom();

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatModel chatModel = new ChatModel();
                chatModel.users.put(user.getUid(), true);
                chatModel.users.put(destinationUid, true);

                if (chatRoomname == null) {
                    mDatabase.child("mychatroom").push().setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            checkchatRoom();
                            Log.v("알림", "채팅 방 생성");

                        }
                    });
                } else {
                    ChatModel.Comment comment = new ChatModel.Comment();
                    comment.uid = user.getUid();
                    comment.message = messageedit.getText().toString();
                    comment.timestamp = ServerValue.TIMESTAMP;
                    mDatabase.child("mychatroom").child(chatRoomname).child("comments").push().setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            messageedit.setText("");

                        }
                    });
                }
            }
        });


    }

    void checkchatRoom() {
        mDatabase.child("mychatroom").orderByChild("users/" + user.getUid()).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatModel chatModel = snapshot.getValue(ChatModel.class);
                    if (chatModel.users.containsKey(destinationUid)) {
                        chatRoomname = snapshot.getKey();
                        sendbtn.setEnabled(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                        recyclerView.setAdapter(new RecyclerViewAdapter());
                        Log.v("알림", "ChatRoomname : " + chatRoomname);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<ChatModel.Comment> comments;

        public RecyclerViewAdapter() {
            comments = new ArrayList<>();
            mDatabase.child("chatuserInfo").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    destinationuserModel = dataSnapshot.getValue(UserModel.class);
                    getMessageList();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        void getMessageList() {
            mDatabase.child("mychatroom").child(chatRoomname).child("comments").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    comments.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        comments.add(snapshot.getValue(ChatModel.Comment.class));
                    }
                    notifyDataSetChanged();

                    recyclerView.scrollToPosition(comments.size() - 1);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
            return new MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            MessageViewHolder messageViewHolder = ((MessageViewHolder) holder);
            if (comments.get(position).uid.equals(user.getUid())) {
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.rightbubble);
                messageViewHolder.linearlayout_destination.setVisibility(recyclerView.INVISIBLE);
                messageViewHolder.textView_message.setTextSize(20);
                messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);
            } else {
                messageViewHolder.textView_nickname.setText(destinationuserModel.nickname);
                messageViewHolder.linearlayout_destination.setVisibility(View.VISIBLE);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.leftbubble);
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setTextSize(20);
                messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);
            }
            long unixTime = (long) comments.get(position).timestamp;
            Date date = new Date(unixTime);
            format.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = format.format(date);
            messageViewHolder.textView_timestamp.setText(time);
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }
    }


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
}
