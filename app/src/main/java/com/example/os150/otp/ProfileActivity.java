package com.example.os150.otp;

import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by os150 on 2020-05-21.
 */

public class ProfileActivity extends ActivityGroup {

    Button profilereset;
    Button pwchange;
    Button withdrawal;
    Button logout;
    Button nicknamechange;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        profilereset = (Button)findViewById(R.id.profilechange);
        pwchange = (Button)findViewById(R.id.pwchange);
        withdrawal = (Button)findViewById(R.id.withdrawal);
        logout = (Button)findViewById(R.id.logout);
        nicknamechange = (Button)findViewById(R.id.nicknamechange);


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                finish();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));

            }
        });

        withdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder withdrawalQ = new AlertDialog.Builder(ProfileActivity.this);
                withdrawalQ.setMessage("정말 계정을 삭제하시겠습니까 ? ").setCancelable(false).setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                     user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                             Toast.makeText(getApplicationContext(),"삭제 완료",Toast.LENGTH_SHORT).show();
                             finish();
                             startActivity(new Intent(getApplicationContext(),MainActivity.class));

                         }
                     });
                     mDatabase = FirebaseDatabase.getInstance().getReference();
                     mDatabase.child("users").child("userInfo").child(user.getUid()).setValue(null);
                    }
                });
                withdrawalQ.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(),"취소",Toast.LENGTH_SHORT).show();
                    }
                });
                withdrawalQ.show();
            }
        });
    }
}
