package com.example.os150.otp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by os150 on 2020-05-19.
 * Map Activity Java 파일
 * 기능 : SupportMapFragment 통해 구글 맵 호출
 *      : FireBase RealTimeDataBase의 모든 게시글에서 lat, lng 정보 가져와 LatLng에 값 저장 및 게시글 정보 mapModel에 저장
 *      : 불러온 정보의 latlng 정보 갯수 만큼 마크 생성
 *      : 마커 클릭시 Title과 snippet값 PostActivity로 값 전달 및 화면 전환
 *      : getLocation() nowLocation() 함수 작성
 *      */

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    String putUseruid;
    String putTitle;

    GoogleMap GMap;
    LatLng latlng;

    MarkerOptions Marker;

    List<Bulletin> MapModel = new ArrayList<>();
    List<LatLng> latLngs = new ArrayList<>();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        //SupportMapFragment 통한 레이아웃 fragmentId 참조하여 구글 맵 호출
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Firebase RealTimeDataBase의 [Bulletin]-[AllBulletin]값 가져와
        mDatabase.child("Bulletin").child("AllBulletin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // List값 초기화
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



                Log.v("알림", "위치 정보 갯수 : " + latLngs.size());
                // 위치 정보의 크기 만큼 루프문 작동
                for (int i = 0; i < latLngs.size(); i++) {
                    final String titles = MapModel.get(i).Title;
                    markerOptions.position(latLngs.get(i)).title(titles).snippet(MapModel.get(i).Useruid).icon(BitmapDescriptorFactory.fromResource(R.drawable.location));

                    GMap.addMarker(markerOptions);
                    GMap.setOnMarkerClickListener(markerClickListener);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //Marker 클릭 리스너
    GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {

            for (int i = 0; i < latLngs.size(); i++) {
                putUseruid = MapModel.get(i).Useruid;
                putTitle = MapModel.get(i).Title;
                Log.v("알림", "보낸 데이터 : " + putUseruid + ", " + putTitle);


            }

            GMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {

                    if (marker.getTitle().equals("현재 위치")) {
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.GMap = googleMap;
        GMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        GMap.setMyLocationEnabled(true);
        getLocation();
    }

    public void getLocation() {
        //LocationManager 선언
        LocationManager LM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //권한 check
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "앱을 재시작하여 권한을 설정해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        // Location 객체 반환받음
        Location location = LM.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        latlng = new LatLng(latitude, longitude);

        //CallBack 등록
        LM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000000, 1000, gpsLocationListener);
        LM.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000000, 1000, gpsLocationListener);
        NowLocation();
    }

    public void NowLocation() {

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlng, 15);
        GMap.moveCamera(cameraUpdate);

        //Marker 표시
        Marker = new MarkerOptions().position(latlng).title("현재 위치").icon(BitmapDescriptorFactory.fromResource(R.drawable.location));
        //Marker 추가
        GMap.addMarker(Marker);
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