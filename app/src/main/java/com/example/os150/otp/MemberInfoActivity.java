package com.example.os150.otp;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created by os150 on 2020-05-19.
 */

public class MemberInfoActivity extends ActivityGroup {
    TextView nictnametv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memberinfo);

        nictnametv = (TextView) findViewById(R.id.nicknametv);


        nictnametv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
            }
        });
    }
}
