package com.example.os150.otp;


import android.app.ActivityGroup;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemberInfoActivity extends ActivityGroup {
    TextView mnicknametv;
    CircleImageView mprofileimage;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = mAuth.getCurrentUser();
    Uri ImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memberinfo);

        mnicknametv = (TextView) findViewById(R.id.nicknametv);
        mprofileimage = (CircleImageView) findViewById(R.id.pimage);


        try {
            mDatabase.child("userInfo").child(user.getUid()).child("nickname").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mnicknametv.setText(dataSnapshot.getValue(String.class));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.v("알림", "데이터 로드 실패");
                }
            });

            mDatabase.child("userInfo").child(user.getUid()).child("profileimage").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String profileimage = dataSnapshot.getValue(String.class);

                    if (profileimage != null) {
                        if ((profileimage.charAt(0) == 'a') == true) {
                            return;
                        } else {
                            ImageUri = Uri.parse(profileimage); //프로필 imageUri문자열 값 Uri로 변경
                            Log.v("알림", "데이터베이스에서 불러온 값 : " + profileimage);
                            Log.v("알림", "불러온 값 Uri 변환 : " + ImageUri);
                            mprofileimage.setImageURI(Uri.parse(getRealPathFromUri(ImageUri)));
                        }
                    } else {
                        Log.v("알림", "null이 아님");
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    Log.v("알림", "데이터 로드 실패");
                }
            });
        } catch (Exception e1) {
            e1.printStackTrace();
        }


        mnicknametv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        });

    }

    public String getRealPathFromUri(Uri contentUri) {
        if (contentUri.getPath().startsWith("/storage")) {
            return contentUri.getPath();
        }
        String id = DocumentsContract.getDocumentId(contentUri).split(":")[1];
        String[] columns = {MediaStore.Files.FileColumns.DATA};
        String selection = MediaStore.Files.FileColumns._ID + "=" + id;
        Cursor cursor = getContentResolver().query(MediaStore.Files.getContentUri("external"), columns, selection, null, null);
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

    @Override
    public void onBackPressed() {
    }
}
