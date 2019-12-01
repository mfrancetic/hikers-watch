package com.mfrancetic.hikerswatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.security.Permission;
import java.security.Permissions;

public class MainActivity extends AppCompatActivity {

    private Context context;

    private ImageView mainBackgroundImageView;

    private LocationManager locationManager;

    private LocationListener locationListener;

    private int locationRequestCode = 1;

    private int minLocationUpdateTime = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainBackgroundImageView = findViewById(R.id.main_background_image_view);
        context = mainBackgroundImageView.getContext();

        setupLocationManagerAndListener();
        checkLocationPermission();


        Glide.with(context).load(R.drawable.forest).centerCrop().into(mainBackgroundImageView);
    }

    private void checkLocationPermission() {
        if (Build.VERSION.SDK_INT < 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minLocationUpdateTime, 0, locationListener);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, locationRequestCode);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minLocationUpdateTime, 0, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation != null) {
                    updateLocationDetails(lastKnownLocation);
                }
            }
        }
    }

    private void updateLocationDetails(Location lastKnownLocation) {

    }

    private void setupLocationManagerAndListener() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocationDetails(location);
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        String toastText = "";
        if (requestCode == locationRequestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    toastText = getString(R.string.location_access_granted);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minLocationUpdateTime, 0, locationListener);
                }
            } else {
                toastText = getString(R.string.location_access_denied);
            }
            Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
        }
    }
}