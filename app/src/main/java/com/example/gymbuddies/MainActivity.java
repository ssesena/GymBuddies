package com.example.gymbuddies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.gymbuddies.databinding.ActivityMainBinding;
import com.example.gymbuddies.fragments.ChatFragment;
import com.example.gymbuddies.fragments.EditProfileFragment;
import com.example.gymbuddies.fragments.HomeFragment;
import com.example.gymbuddies.models.Chat;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    final FragmentManager fragmentManager = getSupportFragmentManager();
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    Boolean mLocationPermissionGranted = false;
    public static final int ERROR_DIALOG_REQUEST = 9001;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;
    public static final String TAG = "MainActivity";
    public static final int MESSAGE_REQUEST_CODE = 40;
    public static final int IMAGE_REQUEST_CODE = 41;

    private LocationManager lm;
    private LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Intent intent = getIntent();

        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }

                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_profile:
                        fragment = new EditProfileFragment();
                        break;
                    case R.id.action_chat:
                        fragment = new ChatFragment();
                        break;
                    case R.id.action_home:
                    default:
                        fragment = new HomeFragment();
                        break;

                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });

        if(intent.getBooleanExtra(ViewProfileActivity.class.getSimpleName(), false)){
            binding.bottomNavigation.setSelectedItemId(R.id.action_chat);
        }
        else {

            binding.bottomNavigation.setSelectedItemId(R.id.action_home);
        }

        ParseUser user = ParseUser.getCurrentUser();
        if(user.getUsername().equals("ssesena")) {
            Log.i(TAG, "Current User: " + ParseUser.getCurrentUser().getUsername());
            getLoc();
        }

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

    }
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(checkMapServices()){
            if(!mLocationPermissionGranted){
                getLocationPermission();
            }
            else{
                SmartLocation.with(this).location()
                        .oneFix()
                        .start(new OnLocationUpdatedListener() {
                            @Override
                            public void onLocationUpdated(Location location) {
                                updateLocation(location);
                            }
                        });
            }
        }
    }

    private void updateLocation(Location location) {
        final ParseUser user = ParseUser.getCurrentUser();
        String latitude = String.valueOf(location.getLatitude());
        String longitude = String.valueOf(location.getLongitude());
        user.put("location", latitude+" "+longitude);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Toast.makeText(MainActivity.this, "Issue updating location", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, e.toString());
                }
                Log.i(TAG, user.getString("location"));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG, "Request Code: "+String.valueOf(requestCode));
        if(requestCode == IMAGE_REQUEST_CODE){
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
            Fragment fragment = new EditProfileFragment();
            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
            Log.i(TAG, "Just took image");
        }
        if(requestCode == MESSAGE_REQUEST_CODE){
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
            Fragment fragment = new ChatFragment();
            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
            Log.i(TAG, "Just sent a message");
        }
    }

    private void updateLoc(Location location) {
        final ParseUser user = ParseUser.getCurrentUser();
        String latitude = String.valueOf(location.getLatitude());
        String longitude = String.valueOf(location.getLongitude());
        user.put("location", latitude + " " + longitude);
        Log.i(TAG, "Location updating");
        Log.i(TAG, "Location determined: " + location.toString());
        try {
            user.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.i(TAG, ParseUser.getCurrentUser().getString("location"));

    }

    private void getLoc(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.i(TAG, "Permission denied");
            return;
        }
        Log.i(TAG, "App Opened");
        SmartLocation.with(this).location()
                .oneFix()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        Log.i(TAG, "Got Location!");
                        updateLoc(location);
                    }
                });
    }


    boolean saveBitmapToFile(File dir, String fileName, Bitmap bm,
                             Bitmap.CompressFormat format, int quality) {

        File imageFile = new File(dir,fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);

            bm.compress(format,quality,fos);

            fos.close();

            return true;
        }
        catch (IOException e) {
            Log.e("app",e.getMessage());
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return false;
    }
}