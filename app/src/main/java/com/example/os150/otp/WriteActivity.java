package com.example.os150.otp;

import android.*;
import android.Manifest;
import android.app.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * Created by os150 on 2020-05-19.
 */

public class WriteActivity extends Activity {
    Button okbtn;
    Button insertphoto;
    Spinner categorySpinner;
    CheckBox showlocation;
    ImageView Bulletin_imageview1;
    ImageView Bulletin_imageview2;
    ImageView Bulletin_imageview3;
    ImageView Bulletin_imageview4;

    EditText write_title;
    EditText write_price;
    EditText write_Bulletin;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = mAuth.getCurrentUser();

    double longi, lati;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        okbtn = (Button) findViewById(R.id.ok);
        insertphoto = (Button) findViewById(R.id.insertphoto);
        categorySpinner = (Spinner) findViewById(R.id.categoryspinner);
        showlocation = (CheckBox) findViewById(R.id.showlocation);
        Bulletin_imageview1 = (ImageView) findViewById(R.id.Bulletin_imageview1);
        Bulletin_imageview2 = (ImageView) findViewById(R.id.Bulletin_imageview2);
        Bulletin_imageview3 = (ImageView) findViewById(R.id.Bulletin_imageview3);
        Bulletin_imageview4 = (ImageView) findViewById(R.id.Bulletin_imageview4);
        write_title = (EditText) findViewById(R.id.write_title);
        write_price = (EditText) findViewById(R.id.write_price);
        write_Bulletin = (EditText) findViewById(R.id.write_Bulletin);
        final LocationManager locationM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                String selectcategory = parent.getItemAtPosition(position).toString();
                //      Toast.makeText(getApplicationContext(), selectcategory, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        showlocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked) {
                    Toast.makeText(getApplicationContext(), "현재 위치정보를 저장합니다.", Toast.LENGTH_SHORT).show();
                    if (ActivityCompat.checkSelfPermission(WriteActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(WriteActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(), "앱권한에서 허용되지 않은 항목을 확인해주세요.", Toast.LENGTH_SHORT).show();
                        showlocation.setChecked(false);
                        onBackPressed();
                        return;
                    }

//                    Location location = locationM.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    locationM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, gpsLocationListener);
                    locationM.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, gpsLocationListener);
                } else {
                    AlertDialog.Builder locationAlert = new AlertDialog.Builder(WriteActivity.this);
                    locationAlert.setMessage("위치 표시를 끌 경우 지도에는 판매글이 표시되지 않습니다.");
                    locationAlert.setPositiveButton("끄기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getApplicationContext(), "uncheck 완료", Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getApplicationContext(), "uncheck 취소", Toast.LENGTH_SHORT).show();
                            showlocation.setChecked(true);

                        }
                    });
                    locationAlert.show();
                }

            }

            LocationListener gpsLocationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();

                    longi = longitude;
                    lati = latitude;
                    //  Toast.makeText(getApplicationContext(),"경도 : "+longi+", 위도 : "+lati,Toast.LENGTH_SHORT).show();
                    Log.v("알림", "경도 : " + longi + ", 위도 : " + lati);

                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String povider) {
                }

            };

        });
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Title = write_title.getText().toString();
                String Price = write_price.getText().toString();
                String Bulletins = write_Bulletin.getText().toString();
                String Category = categorySpinner.getSelectedItem().toString();
                String Useruid = user.getUid();
                double Lat = lati;
                double Lng = longi;


                if (Title.length() == 0) {
                    write_title.setError("제목을 입력해주세요.");
                } else if (Bulletins.length() == 0) {
                    write_Bulletin.setError("내용을 입력해주세요.");
                } else if (Category.equals("카테고리")) {
                    Toast.makeText(getApplicationContext(), "카테고리를 선택해주세요.", Toast.LENGTH_SHORT).show();

                } else if (write_price.getText().length() == 0) {
                    write_price.setError("가격을 입력해주세요.");
                } else {
                    Bulletin bulletin = new Bulletin();
                    bulletin.Category = Category;
                    bulletin.Title = Title;
                    bulletin.Price = Price;
                    bulletin.Bulletins = Bulletins;
                    bulletin.Useruid = Useruid;
                    bulletin.Lat = Lat;
                    bulletin.Lng = Lng;

                    mDatabase.child("Bulletin").child("AllBulletin").push().setValue(bulletin);
                    mDatabase.child("Bulletin").child(Category).push().setValue(bulletin);
                    write_title.setText(null);
                    write_price.setText(null);
                    write_Bulletin.setText(null);
                    finish();
                    startActivity(new Intent(getApplicationContext(), SecondMainActivity.class));
                }
            }

        });
    }


    @Override
    public void onBackPressed() {

    }
}
