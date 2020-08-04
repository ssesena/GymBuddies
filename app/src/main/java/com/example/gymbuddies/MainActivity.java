package com.example.gymbuddies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.gymbuddies.databinding.ActivityMainBinding;
import com.example.gymbuddies.fragments.ChatFragment;
import com.example.gymbuddies.fragments.EditProfileFragment;
import com.example.gymbuddies.fragments.HomeFragment;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.Parse;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    final FragmentManager fragmentManager = getSupportFragmentManager();
    private LocationRequest mLocationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_profile:
                        fragment = new EditProfileFragment();
                        break;
                    case R.id.action_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.action_chat:
                    default:
                        fragment = new ChatFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        binding.bottomNavigation.setSelectedItemId(R.id.action_home);

    }

    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
//        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                // do work here
//                onLocationChanged(locationResult.getLastLocation());
//            }
//        }
    }
}