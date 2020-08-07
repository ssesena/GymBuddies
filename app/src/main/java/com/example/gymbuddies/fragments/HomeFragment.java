package com.example.gymbuddies.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.example.gymbuddies.R;
import com.example.gymbuddies.adapters.HomeFeedAdapter;
import com.example.gymbuddies.databinding.FragmentHomeBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;


public class HomeFragment extends Fragment {

    public static final String TAG = "HomeFragment";
    FragmentHomeBinding binding;
    HomeFeedAdapter homeFeedAdapter;
    JSONArray matches;
    private FusedLocationProviderClient fusedLocationProviderClient;
    Boolean mLocationPermissionGranted = false;
    public static final int ERROR_DIALOG_REQUEST = 9001;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ParseUser user = ParseUser.getCurrentUser();
        ParseUser.getCurrentUser().fetchInBackground();
        matches = ParseUser.getCurrentUser().getJSONArray("filtered_matches");
        binding = FragmentHomeBinding.inflate(getLayoutInflater());

        //For getting the user's current location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        final ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(), R.array.filter_options, R.layout.custom_spinner_selected_item);
        adapter.setDropDownViewResource(R.layout.spinner_item1);

        binding.spinnerHomeFeed.setAdapter(adapter);
        homeFeedAdapter = new HomeFeedAdapter(getContext(), matches);
        binding.rvHomeMatchProfiles.setAdapter(homeFeedAdapter);
        binding.rvHomeMatchProfiles.setLayoutManager(new LinearLayoutManager(getContext()));

        if (user.getJSONArray("matches") == null) {
            user.put("matches", new JSONArray());
            try {
                user.save();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        binding.spinnerHomeFeed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                //Get the selected item from the filter options
                String filterOption = binding.spinnerHomeFeed.getSelectedItem().toString();

                //Save the filter option to Parse
                user.put("filter_option", filterOption);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Error while saving", e);
                            Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_LONG).show();
                        }

                        //After changing the filter option, the "filtered_matches" property needs to be updated
                        updateFilteredMatches();
                        homeFeedAdapter.clear();
                        try {
                            homeFeedAdapter.addAll(user.getJSONArray("filtered_matches"));
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }

                    //First, Iterate through the JSONObjects in the user's "matches" property
                    //Second, query the database to find the ParseUser that correlates to each JSONObjects "objectId" property
                    //Third, Add all of those ParseUsers to a separate ArrayList
                    //Fourth, Call a different function to choose the order of which ones to display

                    private void updateFilteredMatches() {
                        final ArrayList<ParseUser> potentialMatches = new ArrayList<>();
                        final JSONArray matches = user.getJSONArray("matches");
                        for (int i = 0; i < matches.length(); i++) {
                            JSONObject match = null;
                            ParseQuery<ParseUser> query = ParseUser.getQuery();
                            try {
                                match = matches.getJSONObject(i);
                                query.whereEqualTo("objectId", match.getString("objectId"));
                                List<ParseUser> userMatches = query.find();
                                potentialMatches.add(userMatches.get(0));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }
                        sortMatches(potentialMatches);
                    }

                    private void sortMatches(List<ParseUser> userMatches) {
                        ArrayList<ParseUser> filteredMatches = new ArrayList<>();
                        ParseUser user = ParseUser.getCurrentUser();
                        String userFilterOption = user.getString("filter_option");
                        String userExperience = user.getString("user_experience");
                        String userExperiencePreference = user.getString("experience_preference");

                        ArrayList<ParseUser> lastMatches = new ArrayList<>();
                        for (ParseUser userMatch : userMatches) {

                            String matchFilterOption = userMatch.getString("filter_option");
                            String matchExperience = userMatch.getString("user_experience");
                            String matchExperiencePreference = userMatch.getString("experience_preference");


                            if (userFilterOption.equals("Closest")) {
                                if (matchFilterOption.equals("Preference Only")) {
                                    if (matchExperiencePreference.equals(userExperience)) {
                                        filteredMatches.add(userMatch);
                                    }
                                } else {
                                    filteredMatches.add(userMatch);
                                }
                            } else if (userFilterOption.equals("Preference Only")) {
                                if (userExperiencePreference.equals(matchExperience)) {
                                    if (matchFilterOption.equals("Preference Only")) {
                                        if (matchExperiencePreference.equals(userExperience)) {
                                            filteredMatches.add(userMatch);
                                        }
                                    } else {
                                        filteredMatches.add(userMatch);
                                    }
                                }
                            } else if (userFilterOption.equals("Preference First")) {
                                if (matchFilterOption.equals("Preference Only") && !matchExperiencePreference.equals(userExperience)) {
                                    break;
                                } else {
                                    if (userExperiencePreference.equals(matchExperience)) {
                                        filteredMatches.add(userMatch);
                                    } else {
                                        lastMatches.add(userMatch);
                                    }
                                }
                            }
                        }
                        filteredMatches.addAll(lastMatches);
                        JSONArray matchesToShow = new JSONArray();
                        for (ParseUser match : filteredMatches) {
                            matchesToShow.put(match);
                        }
                        user.put("filtered_matches", matchesToShow);
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        });
                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setSpinnerToValue(binding.spinnerHomeFeed, user.getString("filter_option"));

        String workout = user.getString("workout_preference");
        if(workout.equals("Buddy")){
            workout = "Buddie";
        }
        binding.tvCurrentWorkoutPreference.setText(workout+"s");

        return binding.getRoot();
    }

    private void updateLocation(Location location) {
        final ParseUser user = ParseUser.getCurrentUser();
        String latitude = String.valueOf(location.getLatitude());
        String longitude = String.valueOf(location.getLongitude());
        user.put("location", latitude + " " + longitude);
        Log.i(TAG, "Location updating");
        Log.i(TAG, "Location determined: " + location.toString());
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Issue updating location", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, e.toString());
                }
                Log.i(TAG, ParseUser.getCurrentUser().getString("location"));
            }
        });
    }

    private void getLocation(){
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        SmartLocation.with(getContext()).location()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        Log.i(TAG, "Got Location!");
                        updateLocation(location);
                    }
                });
    }



    public void setSpinnerToValue(Spinner spinner, String value) {
        int index = 0;
        SpinnerAdapter adapter = spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(value)) {
                index = i;
                break; // terminate loop
            }
        }
        spinner.setSelection(index);
    }


}