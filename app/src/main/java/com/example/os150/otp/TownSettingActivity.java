package com.example.os150.otp;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

/**
 * Created by os150 on 2020-10-06.
 * TownSetting Activity 자바 파일
 * 기능 : GoogleMap 클릭 시 Marker 생성  lat , lon 표시
 * : searchbtn 클릭 시 검색 위치 Marker 생성 및 지도 이동
 * : 검색 위도, 경도 정보 각각의 TextView 에 Update
 */

//OnMapReadyCallBack 인터페이스 구현
public class TownSettingActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap gMap;
    Geocoder geocoder;

    Button townsearchbtn;
    EditText searchtown;
    TextView lanText;
    TextView lonText;


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
    }

    //Map이 사용 가능하면 호출
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        gMap = googleMap;

        //Camera 좌표 설정
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(36, 127), 15);
        gMap.moveCamera(cameraUpdate);

        geocoder = new Geocoder(this);

        //GoogleMap클릭 시
        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title("위치");
                Double lat = latLng.latitude;
                Double lon = latLng.longitude;
                //Marker 에 lat과 lon 표시
                markerOptions.snippet(lat.toString() + "," + lon.toString());
                markerOptions.position(new LatLng(lat, lon));
                googleMap.addMarker(markerOptions);
            }
        });
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
                        markerOptions.title("검색위치");
                        markerOptions.snippet(address);
                        markerOptions.position(latLng);

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
}
