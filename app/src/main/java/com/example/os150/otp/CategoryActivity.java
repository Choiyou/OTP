package com.example.os150.otp;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by os150 on 2020-05-19.
 */

public class CategoryActivity extends Activity {
    Button kids, man, pet, buy, recycling, book, free, game, sports, furniture, digital, woman, womanthings, beauty, processedfood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        kids = (Button) findViewById(R.id.kids);
        man = (Button) findViewById(R.id.man);
        pet = (Button) findViewById(R.id.pet);
        buy = (Button) findViewById(R.id.buy);
        recycling = (Button) findViewById(R.id.recycling);
        book = (Button) findViewById(R.id.book);
        free = (Button) findViewById(R.id.free);
        game = (Button) findViewById(R.id.game);
        sports = (Button) findViewById(R.id.sports);
        furniture = (Button) findViewById(R.id.furniture);
        digital = (Button) findViewById(R.id.digital);
        woman = (Button) findViewById(R.id.woman);
        womanthings = (Button) findViewById(R.id.womanthing);
        beauty = (Button) findViewById(R.id.beauty);
        processedfood = (Button) findViewById(R.id.processedfood);

        kids.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categoryi = new Intent(getApplicationContext(), SelectCategoryActivity.class);
                categoryi.putExtra("category", "kids");
                startActivity(categoryi);
            }
        });
        man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categoryi = new Intent(getApplicationContext(), SelectCategoryActivity.class);
                categoryi.putExtra("category", "man");
                startActivity(categoryi);
            }
        });
        pet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categoryi = new Intent(getApplicationContext(), SelectCategoryActivity.class);
                categoryi.putExtra("category", "pet");
                startActivity(categoryi);
            }
        });
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categoryi = new Intent(getApplicationContext(), SelectCategoryActivity.class);
                categoryi.putExtra("category", "buy");
                startActivity(categoryi);
            }
        });
        recycling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categoryi = new Intent(getApplicationContext(), SelectCategoryActivity.class);
                categoryi.putExtra("category", "recycling");
                startActivity(categoryi);
            }
        });
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categoryi = new Intent(getApplicationContext(), SelectCategoryActivity.class);
                categoryi.putExtra("category", "book");
                startActivity(categoryi);
            }
        });
        free.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categoryi = new Intent(getApplicationContext(), SelectCategoryActivity.class);
                categoryi.putExtra("category", "free");
                startActivity(categoryi);
            }
        });
        game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categoryi = new Intent(getApplicationContext(), SelectCategoryActivity.class);
                categoryi.putExtra("category", "game");
                startActivity(categoryi);
            }
        });
        sports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categoryi = new Intent(getApplicationContext(), SelectCategoryActivity.class);
                categoryi.putExtra("category", "sports");
                startActivity(categoryi);
            }
        });
        furniture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categoryi = new Intent(getApplicationContext(), SelectCategoryActivity.class);
                categoryi.putExtra("category", "furniture");
                startActivity(categoryi);
            }
        });
        digital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categoryi = new Intent(getApplicationContext(), SelectCategoryActivity.class);
                categoryi.putExtra("category", "digital");
                startActivity(categoryi);
            }
        });
        woman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categoryi = new Intent(getApplicationContext(), SelectCategoryActivity.class);
                categoryi.putExtra("category", "woman");
                startActivity(categoryi);
            }
        });
        womanthings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categoryi = new Intent(getApplicationContext(), SelectCategoryActivity.class);
                categoryi.putExtra("category", "womanthings");
                startActivity(categoryi);
            }
        });
        beauty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categoryi = new Intent(getApplicationContext(), SelectCategoryActivity.class);
                categoryi.putExtra("category", "beauty");
                startActivity(categoryi);
            }
        });
        processedfood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categoryi = new Intent(getApplicationContext(), SelectCategoryActivity.class);
                categoryi.putExtra("category", "processedfood");
                startActivity(categoryi);
            }
        });






    }
}
