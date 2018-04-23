package com.example.prashanttanksali.placepicker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.net.wifi.WifiManager;
import android.net.wifi.ScanResult;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private TextView txtPlaceName;
    private TextView txtLatitude;
    private TextView txtLongitude;
    private TextView txtAddress;
    private WifiManager mWifiManager;

    int PLACE_PICKER_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtPlaceName = (TextView)findViewById(R.id.txtPlaceName);
        txtLatitude = (TextView) findViewById(R.id.txtLatitude);
        txtLongitude = (TextView) findViewById(R.id.txtLongitude);
        txtAddress = (TextView) findViewById(R.id.txtAddress);


    }
    public void onGetPlace(View view) throws InterruptedException{
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this),PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == PLACE_PICKER_REQUEST){
            if (resultCode == RESULT_OK){
                Place place = PlacePicker.getPlace(data, this);
                String name = String.format("%s", place.getName());
                String address = String.format("%s", place.getAddress());
                LatLng latLng = place.getLatLng();
                String latitude = ""+latLng.latitude;
                String longitude = ""+latLng.longitude;
                txtPlaceName.setText(name);
                txtLatitude.setText(latitude);
                txtLongitude.setText(longitude);
                txtAddress.setText(address);

            }
        }
    }
}
