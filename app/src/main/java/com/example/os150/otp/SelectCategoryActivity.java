package com.example.os150.otp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by os150 on 2020-07-25.
 */

public class SelectCategoryActivity extends Activity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectcategory);

        SelectCategoryBulletin();

    }

    private void SelectCategoryBulletin() {
        Intent intent = getIntent();
        switch (intent.getStringExtra("category")) {
            case "kids":
                Toast.makeText(getApplicationContext(), "kids", Toast.LENGTH_SHORT).show();
                break;
            case "man":
                Toast.makeText(getApplicationContext(), "man", Toast.LENGTH_SHORT).show();
                break;
            case "pet":
                Toast.makeText(getApplicationContext(), "pet", Toast.LENGTH_SHORT).show();
                break;
            case "buy":
                Toast.makeText(getApplicationContext(), "buy", Toast.LENGTH_SHORT).show();
                break;
            case "recycling":
                Toast.makeText(getApplicationContext(), "recycling", Toast.LENGTH_SHORT).show();
                break;
            case "book":
                Toast.makeText(getApplicationContext(), "book", Toast.LENGTH_SHORT).show();
                break;
            case "free":
                Toast.makeText(getApplicationContext(), "free", Toast.LENGTH_SHORT).show();
                break;
            case "game":
                Toast.makeText(getApplicationContext(), "game", Toast.LENGTH_SHORT).show();
                break;
            case "sports":
                Toast.makeText(getApplicationContext(), "sports", Toast.LENGTH_SHORT).show();
                break;
            case "furniture":
                Toast.makeText(getApplicationContext(), "furniture", Toast.LENGTH_SHORT).show();
                break;
            case "digital":
                Toast.makeText(getApplicationContext(), "digital", Toast.LENGTH_SHORT).show();
                break;
            case "woman":
                Toast.makeText(getApplicationContext(), "woman", Toast.LENGTH_SHORT).show();
                break;
            case "womanthings":
                Toast.makeText(getApplicationContext(), "womanthings", Toast.LENGTH_SHORT).show();
                break;
            case "beauty":
                Toast.makeText(getApplicationContext(), "beauty", Toast.LENGTH_SHORT).show();
                break;
            case "processfood":
                Toast.makeText(getApplicationContext(), "processfood", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
