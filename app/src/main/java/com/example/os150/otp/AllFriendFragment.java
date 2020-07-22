package com.example.os150.otp;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

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

public class AllFriendFragment extends Fragment {
    List<UserModel> userModels = new ArrayList<>();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = mAuth.getCurrentUser();
    RecyclerView recyclerView;

    public AllFriendFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_allfriend, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.allfriend_recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new AllFriendFragmentRecyclerViewAdapter());


        return view;
    }

    class AllFriendFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public AllFriendFragmentRecyclerViewAdapter() {

            mDatabase.child("chatuserInfo").orderByChild("nickname").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userModels.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        userModels.add(snapshot.getValue(UserModel.class));
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
            String uprofile = userModels.get(position).profileimage;
            final Uri uprofiles = Uri.parse(uprofile);


            if (userModels.get(position).uid.equals(user.getUid())) {
                holder.itemView.setVisibility(View.INVISIBLE);
                holder.itemView.getLayoutParams().height = 0;
                return;
            }

            if (uprofile.charAt(0) != 'a') {

                ((CustomViewHolder) holder).chatuserP.setImageURI(Uri.parse(getRealPathFromUri(uprofiles)));
                ((CustomViewHolder) holder).cnickname.setText(userModels.get(position).nickname);

            } else {
                ((CustomViewHolder) holder).cnickname.setText(userModels.get(position).nickname);

            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    PopupMenu popup = new PopupMenu(getActivity(), holder.itemView);
                    popup.inflate(R.menu.option_menu);
                    popup.setGravity(Gravity.RIGHT);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            UserModel userModel = new UserModel();
                            userModel.nickname = userModels.get(position).nickname;
                            userModel.profileimage = userModels.get(position).profileimage;
                            userModel.uid = userModels.get(position).uid;

                            switch (menuItem.getItemId()) {
                                case R.id.insert:
                                    mDatabase.child("myfriendlist").child(user.getUid()).child(userModels.get(position).uid).setValue(userModel);

                                    break;
                                case R.id.notify:
                                    final EditText notifymessage = new EditText(getContext());
                                    notifymessage.getTransformationMethod();
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
                                                mDatabase.child("admin").child("notify").child("message").child(user.getUid()).child(userModels.get(position).nickname).setValue(notifymessage.getText().toString());

                                            }
                                            Toast.makeText(getContext(), "전송완료", Toast.LENGTH_SHORT).show();
                                        }
                                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Toast.makeText(getContext(), "취소", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    notifyalert.show();
                                    break;
                                case R.id.message:

//                                    String nickname = userModel.nickname;
//                                    mDatabase.child("mychatlist").child(user.getUid()).child(userModels.get(position).nickname).setValue(userModel);
                                    Intent intent = new Intent(view.getContext(), MessageActivity.class);
                                    intent.putExtra("destinationUid", userModels.get(position).uid);//상대방 uid
                                    ActivityOptions activityOptions = null;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(), R.anim.toright, R.anim.toleft);
                                        startActivity(intent, activityOptions.toBundle());
                                    }
                                    break;
                            }
                            return false;
                        }
                    });

                    popup.show();
                }
            });

        }

        @Override
        public int getItemCount() {
            return userModels.size();
        }

    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView chatuserP;
        public TextView cnickname;

        public CustomViewHolder(View view) {
            super(view);
            chatuserP = view.findViewById(R.id.chatuseritem_iv);
            cnickname = view.findViewById(R.id.chatuseritem_tv);


        }
    }


    public String getRealPathFromUri(Uri contentUri) {
        if (contentUri.getPath().startsWith("/storage")) {
            return contentUri.getPath();
        }
        String id = DocumentsContract.getDocumentId(contentUri).split(":")[1];
        String[] columns = {MediaStore.Files.FileColumns.DATA};
        String selection = MediaStore.Files.FileColumns._ID + "=" + id;
        Cursor cursor = getContext().getContentResolver().query(MediaStore.Files.getContentUri("external"), columns, selection, null, null);
        try {
            int columnindex = cursor.getColumnIndex(columns[0]);
            if (cursor.moveToFirst()) {
                return cursor.getString(columnindex);
            }
        } finally {
            cursor.close();
        }
        return null;
    }

}





























