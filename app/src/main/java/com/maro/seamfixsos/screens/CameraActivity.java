package com.maro.seamfixsos.screens;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.JsonObject;
import com.maro.seamfixsos.R;
import com.maro.seamfixsos.tools.ApiClient;
import com.maro.seamfixsos.tools.ApiInterface;
import com.maro.seamfixsos.util.Constants;
import com.maro.seamfixsos.util.GpsUtils;
import com.maro.seamfixsos.util.Sos;
import com.maro.seamfixsos.util.UserLocation;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CameraActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    LocationCallback locationCallback;
    private boolean mTrackingLocation;
    String encodedString;
    private boolean isGPS;
    SharedPreferences sharedPreferences;
    Set<String> nSet;
    List<String> nList;
    ProgressBar progressBar;

    LocationManager locationManager;
    ImageView captureImage;
    double longitude;
    double  latitude;
    LocationRequest locationRequest;
    UserLocation userLocation;
    private final int REQUEST_CODE_PERMISSIONS = 1001;
    Sos sos;
    private final String[] REQUIRED_PERMISSIONS = {"android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.ACCESS_FINE_LOCATION"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        CameraView camera = findViewById(R.id.camera);
        progressBar = findViewById(R.id.uploadProgress);
        captureImage = findViewById(R.id.capture);
        camera.setLifecycleOwner(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(3000);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        userLocation = new UserLocation();

        sharedPreferences = getApplicationContext().getSharedPreferences("Numbers", MODE_PRIVATE);

         nSet = sharedPreferences.getStringSet("Numbers", new HashSet<>());
         nList = new ArrayList<>(nSet);

        Log.i("Numbers", "List: " + nList);

        camera.addCameraListener(new CameraListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onPictureTaken(@NonNull PictureResult result) {
                super.onPictureTaken(result);
               byte[] data =  result.getData();

               encodedString = Base64.getEncoder().encodeToString(data);

                submitInfo();
            }
        });

        //check if GPS is enabled and start tracking location
        if (isGPS){
            startTrackingLocation();
        }

        captureImage.setOnClickListener(view -> {
            camera.takePicture();
            progressBar.setVisibility(View.VISIBLE);
            Log.i("Numbers", "List: " + nList);
            Log.i("Lat:", "onComplete: "+ latitude);

        });

        //Check if app permissions have been granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA}, Constants.LOCATION_REQUEST);
            return;
        }
        //Get Location data once the activity opens up
        fusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
            if(task.getResult()!=null){
                latitude = task.getResult().getLatitude();
                longitude =  task.getResult().getLongitude();
                userLocation.setLatitude(latitude);
                userLocation.setLongitude(longitude);
                Log.i("Lat:", "onComplete: "+ latitude);
                Log.i("Long:", "onComplete: "+ longitude);
                Log.i("Location:", "onComplete: "+ userLocation.getLatitude());

            }
        });
        //Check if the device's GPS is turned on and the request for it to be turned on if it is not.
        if ( !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            settingsRequest();
        }

        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                longitude = locationResult.getLastLocation().getLongitude();
                latitude =  locationResult.getLastLocation().getLatitude();
                Log.i("Lat:", "onComplete: "+ latitude);
                Log.i("Long:", "onComplete: "+ longitude);
            }
        };
        new GpsUtils(this).turnOnGps(isGPSEnable -> isGPS = isGPSEnable);
    }

    public void settingsRequest(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("GPS is disabled. Please enable it to continue")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.cancel();
                    finish();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ( !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            settingsRequest();
        }

    }

    private  void startTrackingLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.LOCATION_REQUEST);
        }else{
            mTrackingLocation = true;
            fusedLocationClient.requestLocationUpdates(getLocationRequest(),
                    locationCallback,
                    Looper.myLooper());
            Log.i("Lat:", "onComplete: "+ latitude);
            Log.i("Long:", "onComplete: "+ longitude);
        }
    }

    private LocationRequest getLocationRequest(){
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    private void submitInfo(){
        ApiInterface apiInterface;
        apiInterface = ApiClient.sendAlert().create(ApiInterface.class);

        sos = new Sos();
        sos.setPhoneNumbers(nList);
        sos.setImage(encodedString);
        sos.setUserLocation(userLocation);

        Call<JsonObject> call = apiInterface.sendSos(sos);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if(response.code()==200){
                    Toast.makeText(CameraActivity.this, "Successfully Sent", Toast.LENGTH_SHORT).show();
                    Log.i("Success", "Great: " + response.body() );

                } else{
                    Toast.makeText(CameraActivity.this, "Some went wrong" + response.errorBody(), Toast.LENGTH_SHORT).show();
                    Log.i("sadly", "Error: " + response.body() );
                    Log.i("sadly", "Error: " + response.code() );
                }
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "FAILED " , Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                Log.i("sadly", "onFailure: " + t.toString() );
            }
        });
    }

}