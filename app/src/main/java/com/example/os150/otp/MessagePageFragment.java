package com.example.os150.otp;

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
 */

public class MessagePageFragment extends Fragment {
    List<UserModel> chatroomuserModel = new ArrayList<>();
    List<ChatModel> chatModel = new ArrayList<>();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = mAuth.getCurrentUser();
    RecyclerView recyclerView;

    public MessagePageFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messagepage, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.messagepage_recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new MessagePageFragmentRecyclerViewAdapter());
        return view;
    }

    class MessagePageFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public MessagePageFragmentRecyclerViewAdapter() {
            mDatabase.child("mychatroom").orderByChild("users/" + user.getUid()).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    chatModel.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        chatModel.add(snapshot.getValue(ChatModel.class));

                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatuser, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

            String destination = null;

            for (String chatuser : chatModel.get(position).users.keySet()) {
                if (!chatuser.equals(user.getUid())) {
                    destination = chatuser;
                    Log.v("알림", "목적지 : " + destination);

                }
            }
            Log.v("알림", destination);
            mDatabase.child("userInfo").child(destination).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        @Override
        public int getItemCount() {
            return chatModel.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public CircleImageView chatroomuserProfile;
            public TextView chatroomnickname;
            public TextView chatroomlastmessage;
            public TextView chatroomtimestamp;

            public CustomViewHolder(View view) {
                super(view);
                chatroomuserProfile = view.findViewById(R.id.chatroom_imageview_profile);
                chatroomnickname = view.findViewById(R.id.chatroom_textview_nickname);
                chatroomlastmessage = view.findViewById(R.id.chatroom_textView_message);
                chatroomtimestamp = view.findViewById(R.id.chatroom_textView_timestamp);

            }
        }
    }

}
