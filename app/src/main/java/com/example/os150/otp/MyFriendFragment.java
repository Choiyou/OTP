package com.example.os150.otp;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by os150 on 2020-07-06.
 */

public class MyFriendFragment extends Fragment {
    List<UserModel> myFrienduserModels = new ArrayList<>();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = mAuth.getCurrentUser();
    RecyclerView recyclerView;

    public MyFriendFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myfriend, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.myfriend_recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new MyFriendFragmentRecyclerViewAdapter());
        recyclerView.getAdapter().notifyDataSetChanged();
        return view;
    }

    class MyFriendFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public MyFriendFragmentRecyclerViewAdapter() {
            mDatabase.child("myfriendlist").child(user.getUid()).orderByChild("nickname").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    myFrienduserModels.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        myFrienduserModels.add(snapshot.getValue(UserModel.class));
                        Log.v("알립", "정보 : " + myFrienduserModels);


                    }
                    notifyDataSetChanged();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.v("알림", "데이터 불러오기 실패");
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
            String uriprofile = myFrienduserModels.get(position).profileimage;
            final Uri uriprofiles = Uri.parse(uriprofile);


            if (uriprofile.charAt(0) != 'a') {
                ((CustomViewHolder) holder).chatuserProfile.setImageURI(Uri.parse(getRealPathFromUri(uriprofiles)));
                ((CustomViewHolder) holder).chatnickname.setText(myFrienduserModels.get(position).nickname);
            } else {
                ((CustomViewHolder) holder).chatnickname.setText(myFrienduserModels.get(position).nickname);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    PopupMenu myfriendpopup = new PopupMenu(getActivity(), holder.itemView);
                    myfriendpopup.inflate(R.menu.myfriend_menu);
                    myfriendpopup.setGravity(Gravity.RIGHT);
                    myfriendpopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.delete:
                                    Log.v("알림", "선택 유저: " + myFrienduserModels.get(position).uid);

                                    mDatabase.child("myfriendlist").child(user.getUid()).child(myFrienduserModels.get(position).uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.v("알림", "데이터 삭제 성공");


                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.v("알림", "데이터 삭제 실패");
                                        }

                                    });
                                    refresh();
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
                                                mDatabase.child("admin").child("notify").child("message").child(user.getUid()).child(myFrienduserModels.get(position).nickname).setValue(notifymessage.getText().toString());

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
                                    Intent intent = new Intent(view.getContext(), MessageActivity.class);
                                    intent.putExtra("destinationUid", myFrienduserModels.get(position).uid);//상대방 uid
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

                    myfriendpopup.show();
                }
            });

        }


        @Override
        public int getItemCount() {
            return myFrienduserModels.size();
        }

    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView chatuserProfile;
        public TextView chatnickname;


        public CustomViewHolder(View view) {
            super(view);
            chatuserProfile = view.findViewById(R.id.chatuseritem_iv);
            chatnickname = view.findViewById(R.id.chatuseritem_tv);


        }
    }

    public void refresh() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.detach(this).attach(this).commit();
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
