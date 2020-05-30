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

/**
 * Created by os150 on 2020-05-19.
 */

public class MemberInfoActivity extends ActivityGroup {
    TextView nicknametv;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    FirebaseUser user;
    CircleImageView imageprofile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memberinfo);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        imageprofile = (CircleImageView)findViewById(R.id.pimage);
        nicknametv = (TextView) findViewById(R.id.nicknametv);


        try {

            mDatabase.child("users").child("userInfo").child(user.getUid()).child("nickname").addValueEventListener( new ValueEventListener() {
                @Override
                public void onDataChange( DataSnapshot dataSnapshot) {

                        nicknametv.setText(dataSnapshot.getValue(String.class));

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.v("알림", "데이터 Load실패");

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        mDatabase.child("users").child("userInfo").child(user.getUid().toString()).child("profileimage").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.v("알림","테스트 "+dataSnapshot.getValue(String.class));
                String profileimageUri = dataSnapshot.getValue(String.class);

                Uri imageuri = Uri.parse(profileimageUri);
                Log.v("알림","테스트 2"+imageuri);

                if((imageuri.toString().charAt(0)=='a')==true){
                    Log.v("알림","기본 이미지 "+imageuri.toString().charAt(0));
                    imageprofile.setImageURI(imageuri);
                }
                else {
                    getRealPathFromURI(imageuri);
                    Log.v("알림", "절대 경로" + getRealPathFromURI(imageuri));
                    imageprofile.setImageURI(Uri.parse(getRealPathFromURI(imageuri)));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("알림","image load 실패");
            }
        });

        nicknametv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                finish();

            }
        });
    }
    public String getRealPathFromURI (Uri contentUri){
        if(contentUri.getPath().startsWith("/storage")){
            return contentUri.getPath();
        }
        String id = DocumentsContract.getDocumentId(contentUri).split(":")[1];
        String[] columns = {MediaStore.Files.FileColumns.DATA};
        String selection = MediaStore.Files.FileColumns._ID+"="+id;
        Cursor cursor = getContentResolver().query(MediaStore.Files.getContentUri("external"),columns,selection,null,null);
        try{
            int columnIndex = cursor.getColumnIndex(columns[0]);
            if(cursor.moveToFirst()){
                return cursor.getString(columnIndex);
            }
        }finally{
            cursor.close();
        }
        return null;
    }
}
