package com.example.os150.otp;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by os150 on 2020-10-06.
 * TownSetting Activity 자바 파일
 * 기능 : GoogleMap 클릭 시 Marker 생성  lat , lon 표시
 * : searchbtn 클릭 시 검색 위치 Marker 생성 및 지도 이동
 * : 검색 위도, 경도 정보 각각의 TextView 에 Update
 * : 이하 MapActivity 참조
 */

//OnMapReadyCallBack 인터페이스 구현
public class TownSettingActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap gMap;
    Geocoder geocoder;

    Button townsearchbtn;
    EditText searchtown;
    TextView lanText;
    TextView lonText;

    String putUseruid;
    String putTitle;

    LatLng latlng;

    MarkerOptions Marker;
    List<Bulletin> MapModel = new ArrayList<>();
    List<LatLng> latLngs = new ArrayList<>();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = mAuth.getCurrentUser();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_townsetting);

        searchtown = (EditText) findViewById(R.id.searchTown);
        townsearchbtn = (Button) findViewById(R.id.town_search_btn);
        lanText = (TextView) findViewById(R.id.lanText);
        lonText = (TextView) findViewById(R.id.lonText);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.townmap);
        mapFragment.getMapAsync(this); //GoogleMap 객체를 얻어오기 위한 작업

        mDatabase.child("Bulletin").child("AllBulletin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                latLngs.clear();
                MapModel.clear();

                //Marker 옵션 설정
                final MarkerOptions markerOptions = new MarkerOptions();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    double lat = snapshot.getValue(Bulletin.class).Lat;
                    double lng = snapshot.getValue(Bulletin.class).Lng;

                    Log.v("알림", "위치 ? : " + lat + "," + lng);
                    if (lat == 0 && lng == 0) {
                        continue;
                    }
                    LatLng latlng = new LatLng(lat, lng);
                    latLngs.add(latlng);
                    MapModel.add(snapshot.getValue(Bulletin.class));


                }

                Log.e("알림", "위치 정보 ?" + latLngs);
                Log.e("알림", latLngs.get(0).toString());


                Log.v("알림", "위치 정보 갯수 : " + latLngs.size());
                for (int i = 0; i < latLngs.size(); i++) {
                    final String titles = MapModel.get(i).Title;
                    markerOptions.position(latLngs.get(i)).title(titles).snippet(MapModel.get(i).Useruid).icon(BitmapDescriptorFactory.fromResource(R.drawable.location));

                    gMap.addMarker(markerOptions);
                    gMap.setOnMarkerClickListener(markerClickListener);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {

            for (int i = 0; i < latLngs.size(); i++) {

                Log.v("알림", "UserUid : " + MapModel.get(i).Useruid);
                Log.v("알림", "Title : " + MapModel.get(i).Title);
                putUseruid = MapModel.get(i).Useruid;
                putTitle = MapModel.get(i).Title;
                Log.v("알림", "보낸 데이터 : " + putUseruid + ", " + putTitle);


            }

            gMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {

                    if (marker.getTitle().equals("현재 위치") || marker.getTitle().equals("검색위치")) {
                        return;
                    }


                    Intent postintent = new Intent(getApplicationContext(), PostActivity.class);
                    postintent.putExtra("Useruid", marker.getSnippet().toString());
                    postintent.putExtra("Title", marker.getTitle().toString());
                    startActivity(postintent);
                }
            });

            return false;
        }
    };

    //Map이 사용 가능하면 호출
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        gMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        gMap.setMyLocationEnabled(true);
        getLocation();

        //Camera 좌표 설정
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(36, 127), 15);
        gMap.moveCamera(cameraUpdate);

        geocoder = new Geocoder(this);

        //검색 버튼 클릭 시
        townsearchbtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                String townname = searchtown.getText().toString();
                //주소 타입의 List = null 선언
                List<Address> addressList = null;
                if (townname.length() != 0) {
                    try {
                        addressList = geocoder.getFromLocationName(townname, 10);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {

                        String[] splitTownname = addressList.get(0).toString().split(",");
                        String address = splitTownname[0].substring(splitTownname[0].indexOf("\"") + 1, splitTownname[0].length() - 2);
                        String latitude = splitTownname[10].substring(splitTownname[10].indexOf("=") + 1);
                        String longitude = splitTownname[12].substring(splitTownname[12].indexOf("=") + 1);

                        lanText.setText("위도 : " + latitude);

                        double lat = Double.parseDouble(latitude);
                        LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                        lonText.setText("경도 : " + longitude);
                        double lng = Double.parseDouble(longitude);

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.title("검색위치").snippet(address).position(latLng);

                        gMap.addMarker(markerOptions);
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));//지도 위치 latLng 로 이동


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                //editText에 입력된 Data가 없을 경우
                else {
                    searchtown.setError("위치를 입력해주세요");
                }
            }
        });

    }

    public void getLocation() {
        LocationManager LM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "앱을 재시작하여 권한을 설정해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        Location location = LM.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        latlng = new LatLng(latitude, longitude);

        LM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000000, 1000, gpsLocationListener);
        LM.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000000, 1000, gpsLocationListener);
        NowLocation();
    }

    public void NowLocation() {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlng, 15);
        gMap.moveCamera(cameraUpdate);
        Marker = new MarkerOptions().position(latlng).title("현재 위치").icon(BitmapDescriptorFactory.fromResource(R.drawable.location));

        gMap.addMarker(Marker);
    }

    final LocationListener gpsLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double longitude = location.getLongitude();//위도 값
            double latitude = location.getLatitude(); // 경도 값
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    public void onRestart() {
        super.onRestart();

    }
}
