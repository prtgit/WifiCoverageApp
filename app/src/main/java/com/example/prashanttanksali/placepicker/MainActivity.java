package com.example.prashanttanksali.placepicker;

import android.Manifest;
import android.app.Activity;
import android.app.VoiceInteractor;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.net.wifi.WifiManager;
import android.net.wifi.ScanResult;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView txtPlaceName;
    private TextView txtAddress;
    //private WifiManager mWifiManager;
    private TextView txtSSID;
    private TextView txtRSSI;
    private String latitude;
    private String longitude;


    int PLACE_PICKER_REQUEST = 1;
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtPlaceName = (TextView)findViewById(R.id.txtPlaceName);
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        txtRSSI = (TextView) findViewById(R.id.txtRSSI);
        txtSSID = (TextView) findViewById(R.id.txtSSID);


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
    public void onSaveToDatabase(View view)throws InterruptedException{
        String jsonUrl = "http://192.168.0.14:9000/sendLocationDetails";
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        String strSSID = txtSSID.getText().toString().trim();
        String strLat = latitude.trim();
        String strLong = longitude.trim();
        String strRssi = txtRSSI.getText().toString().trim();
        if(strSSID.isEmpty()||strLat.isEmpty()||strLong.isEmpty()||strRssi.isEmpty()){
            Toast.makeText(this,"All details not received",Toast.LENGTH_SHORT);
            return;
        }
        try {

            json.put("SSID",""+strSSID);
            json.put("Lat",""+strLat);
            json.put("Long",""+strLong);
            json.put("Rssi",""+strRssi);
            array.put(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue mRequestQueue;

// Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

// Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

// Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

// Start the queue
        mRequestQueue.start();

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,jsonUrl,""+array,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response){
                        Toast.makeText(getApplicationContext(),"Response received ="+response,Toast.LENGTH_SHORT).show();
                        try {

                            String resp = response.getString("success");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(getApplicationContext(),""+error.getMessage(),Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });
        mRequestQueue.add(jsonRequest);


    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == PLACE_PICKER_REQUEST){
            if (resultCode == RESULT_OK){
                Place place = PlacePicker.getPlace(data, this);
                String name = String.format("%s", place.getName());
                String address = String.format("%s", place.getAddress());
                LatLng latLng = place.getLatLng();
                latitude = ""+latLng.latitude;
                longitude = ""+latLng.longitude;
                txtPlaceName.setText(name);
                //txtLatitude.setText(latitude);
                //txtLongitude.setText(longitude);
                txtAddress.setText(address);
                try {
                    getWifiInfo();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void getWifiInfo() throws InterruptedException {
        //WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(getApplicationContext().WIFI_SERVICE);
        final WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(getApplicationContext().WIFI_SERVICE);

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        //Toast.makeText(this,"SSID = "+wifiInfo.getSSID()+" Strength = "+wifiInfo.getRssi(),Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(),""+scanResults.size(),Toast.LENGTH_SHORT).show();
        txtSSID.setText(""+wifiInfo.getSSID());
        txtRSSI.setText(""+wifiInfo.getRssi());
        checkPermission();
        wifiManager.setWifiEnabled(true);
        wifiManager.startScan();
        BroadcastReceiver br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {


                List<ScanResult> scanResults = wifiManager.getScanResults();
                Toast.makeText(getApplicationContext(),"Size = "+scanResults.size(),Toast.LENGTH_LONG).show();
                for(ScanResult result:scanResults){
                    String resultString = "SSID = "+result.SSID+"\n BSSID = "+result.BSSID+"\n Signal Strength = "+result.level+"" +
                            "\nCapabilities = "+result.capabilities;
                    Toast.makeText(getApplicationContext(),resultString,Toast.LENGTH_LONG).show();
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        registerReceiver(br,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

    }
    private boolean checkPermission() {

        List<String> permissionsList = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(getApplicationContext(),android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("My App","Fine location was not granted");
            permissionsList.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("My App","Coarse location was not granted");
            permissionsList.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            Log.d("My App","Access Wifi state was not granted");
            permissionsList.add(Manifest.permission.ACCESS_WIFI_STATE);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            Log.d("My App","Change wifi state was not granted");
            permissionsList.add(Manifest.permission.CHANGE_WIFI_STATE);
        }
        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions(this, permissionsList.toArray(new String[permissionsList.size()]),PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
            return false;
        }
        return true;
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Do something with granted permission
            //mWifiListener.getScanningResults();
            Log.d("My App","Permission Granted");

        }
    }
    public void markOnGoogleMaps(View view)throws InterruptedException{
        String jsonUrl = "http://192.168.0.14:9000/getLocationDetails";

        RequestQueue mRequestQueue;

// Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

// Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

// Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

// Start the queue
        mRequestQueue.start();
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,jsonUrl,"",
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response){
                        Toast.makeText(getApplicationContext(),"Response received ="+response,Toast.LENGTH_SHORT).show();
                        try {

                            String resp = response.getString("output");
                            Intent myIntent = new Intent(MainActivity.this,MapMarkerActivity.class);
                            myIntent.putExtra("dbContents",resp);
                            startActivity(myIntent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(getApplicationContext(),""+error.getMessage(),Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });
        mRequestQueue.add(jsonRequest);


    }

}
