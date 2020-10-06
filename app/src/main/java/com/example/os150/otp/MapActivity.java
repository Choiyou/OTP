package com.example.os150.otp;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
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
import java.util.Map;

/**
 * Created by os150 on 2020-05-19.
 */

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    String Nickname;
    String putUseruid;
    String putTitle;
    String putPrice;
    GoogleMap GMap;
    LatLng latlng;

    MarkerOptions Marker;
    List<Bulletin> MapModel = new ArrayList<>();
    List<LatLng> latLngs = new ArrayList<>();

    List<UserModel> nickname = new ArrayList<>();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mDatabase.child("Bulletin").child("AllBulletin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                latLngs.clear();
                MapModel.clear();
                nickname.clear();

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
                for (int i = 0; i < latLngs.size(); i++) {
                    final String titles = MapModel.get(i).Title;
                    Log.v("알림", "별명? : " + Nickname);
                    markerOptions.position(latlng).title(titles).icon(BitmapDescriptorFactory.fromResource(R.drawable.location));

                    GMap.addMarker(markerOptions);
                    GMap.setOnMarkerClickListener(markerClickListener);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            final String markerid = marker.getId();
            LatLng location = marker.getPosition();

            //  Intent postintent = new Intent(getApplicationContext(),PostActivity.class);
            for (int i = 0; i < latLngs.size(); i++) {

                Log.v("알림", "UserUid : " + MapModel.get(i).Useruid);
                Log.v("알림", "Title : " + MapModel.get(i).Title);
                putUseruid = MapModel.get(i).Useruid;
                putTitle = MapModel.get(i).Title;
                putPrice = MapModel.get(i).Price;
                Log.v("알림", "보낸 데이터 : " + putUseruid + ", " + putTitle);


            }

            GMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {

                    if (marker.getTitle().equals("현재 위치")) {
                        return;
                    }


                    Intent postintent = new Intent(getApplicationContext(), PostActivity.class);
                    postintent.putExtra("Useruid", putUseruid);
                    postintent.putExtra("Title", marker.getTitle().toString());
                    startActivity(postintent);
                }
            });

            // startActivity(postintent);
            // Toast.makeText(getApplicationContext(), "마커 클릭 : MarkerId = " + markerid + ", 위치 = " + location, Toast.LENGTH_SHORT).show();
//            String markerindex = new String();
//            String markerid = marker.getId();
//            for(int i = 0; i<markerid.length();i++){
//                if(48 <=markerid.charAt(i)&&markerid.charAt(i)<=57){
//                    markerindex+=markerid.charAt(i);
//                }
//                Intent postintent = new Intent(getApplicationContext(),PostActivity.class);
//                if(Integer.parseInt(markerindex)!=0){
//                   //보낼 데이터
//                    startActivity(postintent);
//                }
//            }
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
        LocationManager LM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "앱을 재시작하여 권한을 설정해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        Location location = LM.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            String provider = location.getProvider();
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        latlng = new LatLng(latitude, longitude);

        LM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000000, 1000, gpsLocationListener);
        LM.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000000, 1000, gpsLocationListener);
        NowLocation();
    }

    public void NowLocation() {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlng, 15);
        GMap.moveCamera(cameraUpdate);
        Marker = new MarkerOptions().position(latlng).title("현재 위치").icon(BitmapDescriptorFactory.fromResource(R.drawable.location));

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
