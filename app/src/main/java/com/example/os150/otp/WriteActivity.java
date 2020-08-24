package com.example.os150.otp;

import android.Manifest;
import android.app.Activity;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by os150 on 2020-05-19.
 */

public class WriteActivity extends Activity {
    private static int WRITE_ALBUM_IMAGE_REQUEST = 1;

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
    FirebaseStorage storage = FirebaseStorage.getInstance();

    Bulletin bulletin = new Bulletin();

    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
    Date date = new Date();

    double longi, lati; // 경도, 위도

    Uri write_albumuri;
    Uri Bulletin_image1;
    Uri Bulletin_image2;
    Uri Bulletin_image3;
    Uri Bulletin_image4;


    int count;
    int i = 0;

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


        //System으로부터 GPS통한 위치 서비스 제공하는 LocationManager 선언
        final LocationManager locationM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //사진 첨부 버튼 클릭 시
        insertphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent walbumIntent = new Intent(Intent.ACTION_PICK);
                walbumIntent.setType("image/*");
                walbumIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // 여러 이미지 항목 선택 가능
                walbumIntent.setAction(Intent.ACTION_GET_CONTENT);
                walbumIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                walbumIntent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(walbumIntent, WRITE_ALBUM_IMAGE_REQUEST);
            }
        });

        //카테고리 스피너 아이템 선택 리스너
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                String selectcategory = parent.getItemAtPosition(position).toString();
                Log.v("알림", " 선택한 항목 : " + selectcategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //위치 표시 CheckBox 체크 상황
        showlocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    if (ActivityCompat.checkSelfPermission(WriteActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(WriteActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(), "앱권한에서 허용되지 않은 항목을 확인해주세요.", Toast.LENGTH_SHORT).show();
                        showlocation.setChecked(false);
                        onBackPressed();
                        return;
                    }
                    try {
                        locationM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, gpsLocationListener); //GPS 이용 위치 제공 1초, 1미터당 해당 값 갱신
                        locationM.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, gpsLocationListener); // 기지국, WIFI를 이용한 위치 제공 1초, 1미터당 해당 값 갱신
                    } catch (SecurityException e) {
                        Toast.makeText(getApplicationContext(), "위치 권한이 필요합니다. 권한을 확인해주세요.", Toast.LENGTH_SHORT).show();
                    }
                } else { // checkBox = unCheck
                    AlertDialog.Builder locationAlert = new AlertDialog.Builder(WriteActivity.this);
                    locationAlert.setMessage("위치 표시를 끌 경우 지도에는 판매글이 표시되지 않습니다.");
                    locationAlert.setPositiveButton("끄기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.v("알림", "위치 표시 unCheck");
                        }
                    }).setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.v("알림", "닫기 ");
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

        // 작성 완료 시
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Title = write_title.getText().toString();
                String Price = write_price.getText().toString();
                String Bulletins = write_Bulletin.getText().toString();
                String Category = categorySpinner.getSelectedItem().toString();
                String Useruid = user.getUid();
                Uri BulletinImage1 = Bulletin_image1;
                Uri BulletinImage2 = Bulletin_image2;
                Uri BulletinImage3 = Bulletin_image3;
                Uri BulletinImage4 = Bulletin_image4;
                double Lat = lati;
                double Lng = longi;
                Log.v("알림", "이미지 1 : " + BulletinImage1);
                Log.v("알림", "이미지 2: " + BulletinImage2);
                Log.v("알림", "이미지 3 : " + BulletinImage3);
                Log.v("알림", "이미지 4 : " + BulletinImage4);


                if (Title.length() == 0) {
                    write_title.setError("제목을 입력해주세요.");
                } else if (Bulletins.length() == 0) {
                    write_Bulletin.setError("내용을 입력해주세요.");
                } else if (Category.equals("카테고리")) {
                    Toast.makeText(getApplicationContext(), "카테고리를 선택해주세요.", Toast.LENGTH_SHORT).show();

                } else if (write_price.getText().length() == 0) {
                    write_price.setError("가격을 입력해주세요.");
                } else {
                    bulletin.Category = Category;
                    bulletin.Title = Title;
                    bulletin.Price = Price;
                    bulletin.Bulletins = Bulletins;
                    bulletin.Useruid = Useruid;
                    bulletin.Lat = Lat;
                    bulletin.Lng = Lng;
                    try {
                        bulletin.BulletinImage1 = BulletinImage1.toString();
                        bulletin.BulletinImage2 = BulletinImage2.toString();
                        bulletin.BulletinImage3 = BulletinImage3.toString();
                        bulletin.BulletinImage4 = BulletinImage4.toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mDatabase.child("Bulletin").child("AllBulletin").push().setValue(bulletin);
                    mDatabase.child("Bulletin").child(Category).push().setValue(bulletin);

                    write_title.setText(null);
                    write_price.setText(null);
                    write_Bulletin.setText(null);

                    finish();
                    startActivity(new Intent(getApplicationContext(), SecondMainActivity.class));
                    //   startActivity(new Intent(getApplicationContext(),PostActivity.class));

                }
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WRITE_ALBUM_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {

                //기존 이미지 지우기
                Bulletin_imageview1.setImageResource(0);
                Bulletin_imageview2.setImageResource(0);
                Bulletin_imageview3.setImageResource(0);
                Bulletin_imageview4.setImageResource(0);

                write_albumuri = data.getData(); //Uri 가져오기
                ClipData cd = data.getClipData();
                String write_albumImage = formatter.format(date);
//                StorageReference wsrf = storage.getReferenceFromUrl("gs://otpdata-edb66.appspot.com/").child("postImage/" + write_albumImage + "_" + count++ + ".png");

                if (cd != null) {
                    if (i < cd.getItemCount()) {

                        for (i = 0; i < 4; i++) {
                            try {
                                StorageReference wsrf = storage.getReferenceFromUrl("gs://otpdata-edb66.appspot.com/").child("postImage/" + (write_albumImage + "_" + count++) + "_" + i + ".png");
                                Uri albumnumber = cd.getItemAt(i).getUri();
                                Log.v("알림", "i = " + i);
                                Log.v("알림", "이미지 Uri : " + albumnumber);

                                wsrf.putFile(albumnumber).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Uri downloadUri = taskSnapshot.getDownloadUrl();
                                        Log.v("알림", "실제 다운로드된 이미지 : " + downloadUri);


                                    }

                                });
                                switch (i) {
                                    case 0:
                                        Glide.with(getApplicationContext()).load(albumnumber).into(Bulletin_imageview1);
                                        Log.v("알림", "낄낄 : " + albumnumber);
                                        Bulletin_image1 = albumnumber;
                                        break;
                                    case 1:
                                        Glide.with(getApplicationContext()).load(albumnumber).into(Bulletin_imageview2);
                                        Bulletin_image2 = albumnumber;
                                        Log.v("알림", "낄낄 : " + albumnumber);

                                        break;
                                    case 2:
                                        Glide.with(getApplicationContext()).load(albumnumber).into(Bulletin_imageview3);
                                        Bulletin_image3 = albumnumber;
                                        Log.v("알림", "낄낄 : " + albumnumber);
                                        break;
                                    case 3:
                                        Glide.with(getApplicationContext()).load(albumnumber).into(Bulletin_imageview4);
                                        Bulletin_image4 = albumnumber;
                                        Log.v("알림", "낄낄 : " + albumnumber);
                                        break;
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                } else if (write_albumuri != null) {
                    StorageReference wsrf = storage.getReferenceFromUrl("gs://otpdata-edb66.appspot.com/").child("postImage/" + write_albumImage + "_" + count++ + ".png");

                    wsrf.putFile(write_albumuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri write_downloadalbumUri1 = taskSnapshot.getDownloadUrl();
                            Glide.with(getApplicationContext()).load(write_albumuri).into(Bulletin_imageview1);
                            Bulletin_image1 = write_downloadalbumUri1;
                        }
                    });
                }

            }
        }
    }

    @Override
    public void onBackPressed() {

    }
}
