package com.example.prashanttanksali.placepicker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapMarkerActivity extends AppCompatActivity implements OnMapReadyCallback {
    String[] dbContentsSplit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_marker);
        Intent intent = getIntent();
        String dbContents = intent.getStringExtra("dbContents");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        dbContentsSplit = dbContents.split(",");
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        /*LatLng sydney = new LatLng(65.07213,-2.109375);
        googleMap.addMarker(new MarkerOptions().position(sydney)
                .title("Marker in Sydney").snippet("Snippet").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        LatLng america = new LatLng(-33.852, 151.211);
        googleMap.addMarker(new MarkerOptions().position(america)
                .title("Marker in Sydney").snippet("Snippet").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(america));*/
        int noOfLoops = dbContentsSplit.length/5;
        LatLng[] latLngsArray = new LatLng[noOfLoops];
        for(int i=0;i<noOfLoops;i++){
            int latIndex = i*5 + 2;
            int longIndex = i*5 + 3;
            int rssiIndex = i*5 +4;
            double latitude = Double.parseDouble(dbContentsSplit[latIndex].substring(1,dbContentsSplit[latIndex].length()-1));
            double longitude = Double.parseDouble(dbContentsSplit[longIndex].substring(1,dbContentsSplit[longIndex].length()-1));
            double rssi = Double.parseDouble(dbContentsSplit[rssiIndex].substring(1,dbContentsSplit[rssiIndex].length()-1));
            float currentHue = 0.0f;
            if(rssi>= -50){
                currentHue = BitmapDescriptorFactory.HUE_GREEN;
            }
            else if(rssi>=-60 && rssi<-50){
                currentHue = BitmapDescriptorFactory.HUE_YELLOW;
            }
            else if(rssi>=-70 && rssi<-60){
                currentHue = BitmapDescriptorFactory.HUE_ORANGE;
            }
            else
                currentHue = BitmapDescriptorFactory.HUE_RED;
            latLngsArray[i] = new LatLng(latitude,longitude);
            googleMap.addMarker(new MarkerOptions().position(latLngsArray[i])
                    .title("Marker for "+i).snippet("Snippet").icon(BitmapDescriptorFactory.defaultMarker(currentHue)));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLngsArray[i]));
        }
    }
}
