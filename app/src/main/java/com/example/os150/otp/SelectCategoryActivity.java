package com.example.os150.otp;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.ActivityOptions;
import android.app.Fragment;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
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

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by os150 on 2020-07-25.
 */

public class SelectCategoryActivity extends Activity {
    private ArrayList<PostItem> postItem = new ArrayList<>();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = mAuth.getCurrentUser();

    RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerView = (RecyclerView) findViewById(R.id.selectcategory_recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(new SelectCategoryAdapter());
        recyclerView.getAdapter().notifyDataSetChanged();

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

    class SelectCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public SelectCategoryAdapter() {

        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return postItem.size();
        }

    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {
        public ImageView categoryImageView;
        public TextView category_title;
        public TextView category_price;

        public CustomViewHolder(View view) {
            super(view);
            categoryImageView = view.findViewById(R.id.categoryItem_imageview);
            category_title = view.findViewById(R.id.categoryItem_textView_title);
            category_price = view.findViewById(R.id.categoryItem_textView_price);
        }
    }
}
