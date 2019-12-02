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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.math.RoundingMode;
import java.security.Permission;
import java.security.Permissions;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Context context;

    private ImageView mainBackgroundImageView;

    private TextView latitudeTextView;

    private TextView longitudeTextView;

    private TextView accuracyTextView;

    private TextView altitudeTextView;

    private TextView addressTextView;

    private LocationManager locationManager;

    private LocationListener locationListener;

    private int locationRequestCode = 1;

    private int minLocationUpdateTime = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainBackgroundImageView = findViewById(R.id.main_background_image_view);
        latitudeTextView = findViewById(R.id.latitude_text_view);
        longitudeTextView = findViewById(R.id.longitude_text_view);
        accuracyTextView = findViewById(R.id.accuracy_text_view);
        altitudeTextView = findViewById(R.id.altitude_text_view);
        addressTextView = findViewById(R.id.address_text_view);

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
        clearLocationDetails();
        latitudeTextView.append(" " + parseDouble(lastKnownLocation.getLatitude()));
        longitudeTextView.append(" " + parseDouble(lastKnownLocation.getLongitude()));
        accuracyTextView.append(" " + parseDouble(lastKnownLocation.getAccuracy()));
        altitudeTextView.append(" " + parseDouble(lastKnownLocation.getAltitude()));
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), 1);
            if (addressList != null && addressList.size() > 0) {
                String address = addressList.get(0).getAddressLine(0);
                String[] splitAddress = address.split(",");
                addressTextView.append("\n" + splitAddress[0] + "\n" + splitAddress[1] );
            }
        } catch (Exception e) {
            e.printStackTrace();
            addressTextView.setVisibility(View.GONE);
        }
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

    private String parseDouble(double doubleValue) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setRoundingMode(RoundingMode.UP);
        return decimalFormat.format(doubleValue);
    }

    private void clearLocationDetails() {
        latitudeTextView.setText(getString(R.string.latitude));
        longitudeTextView.setText(getString(R.string.longitude));
        accuracyTextView.setText(getString(R.string.accuracy));
        altitudeTextView.setText(getString(R.string.altitude));
        addressTextView.setText(getString(R.string.address));
    }
}