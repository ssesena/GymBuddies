package com.example.gymbuddies.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gymbuddies.LoginActivity;
import com.example.gymbuddies.PhotoActivity;
import com.example.gymbuddies.R;
import com.example.gymbuddies.ViewProfileActivity;
import com.example.gymbuddies.adapters.GalleryGridAdapter;
import com.example.gymbuddies.databinding.FragmentEditProfileBinding;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment {
    ParseUser user = ParseUser.getCurrentUser();
    public static final String TAG = "EditProfileFragment";
    private FragmentEditProfileBinding binding;
    public static final int GALLERY_PHOTO_LIMIT = 6;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditProfileFragment newInstance(String param1, String param2) {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEditProfileBinding.inflate(getLayoutInflater());
        ArrayAdapter adapterPreferences = ArrayAdapter.createFromResource(getContext(), R.array.workout_preferences, R.layout.spinner_item1);
        binding.spPreference.setAdapter(adapterPreferences);

        ArrayAdapter adapterExperience = ArrayAdapter.createFromResource(getContext(), R.array.workout_experience, R.layout.spinner_item1);
        binding.spExperience.setAdapter(adapterExperience);
        binding.spExperiencePreference.setAdapter(adapterExperience);

        setButtonOnClickListeners(binding);

        try {
            showAll(binding);
        } catch (Exception e) {
            e.printStackTrace();
        }

        findMatches();

        return binding.getRoot();
    }

    private void findMatches() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", user.getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> matches, ParseException e) {
                if(e!=null){
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.i(TAG, matches.size()+"");

                //Get logged in user's location and parse correctly
                String userCoords[] = user.getString("location").split(" ");
                double userLat = Double.parseDouble(userCoords[0]);
                double userLon = Double.parseDouble(userCoords[1]);

                //Create Array and Hash Table to keep track of which matches are the closest
                ArrayList<Double> distances = new ArrayList<>();
                Hashtable<Double, ParseUser> distanceToMatchDict = new Hashtable<Double, ParseUser>();
                ArrayList<ParseUser> closestMatches = new ArrayList<>();


                //Iterate through the matches
                for(ParseUser match:matches){

//                    Log.i(TAG, match.toString());

                    //Parse matches location correctly
                    String matchCoords[] = match.getString("location").split(" ");
                    double matchLat = Double.parseDouble(matchCoords[0]);
                    double matchLon = Double.parseDouble(matchCoords[1]);

                    //Find distance between user and match
                    double distance = findDistance(userLat,userLon,matchLat,matchLon);

                    //Add distance to list and hash table
                    distances.add(distance);
                    distanceToMatchDict.put(distance, match);
                }

                Log.i(TAG, distances.toString());
                Log.i(TAG, distanceToMatchDict.toString());

                //After all that, now I need to sort them
                Collections.sort(distances);

                //Now to iterate through the distances
                for(double distance:distances){

                    //And add them to the user's potential match list
                    closestMatches.add(distanceToMatchDict.get(distance));
                }

                //Create a new JSON Array to store matches
                JSONArray newMatches = new JSONArray();


                //Next we update the user's matches by iterating through the new list
                for(ParseUser match:closestMatches){
                    newMatches.put(match);
                }

                //Finally we update the user's matches
                user.put("matches", newMatches);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e!=null){
                            Log.e(TAG, "Error while saving",e);
                            Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }

    private void showAll(FragmentEditProfileBinding binding) throws Exception{

        String workoutPreference = user.getString("workout_preference");
        String userExperience = user.getString("user_experience");
        String experiencePreference = user.getString("experience_preference");


        setSpinnerToValue(binding.spPreference, workoutPreference);

        setSpinnerToValue(binding.spExperience, userExperience);

        setSpinnerToValue(binding.spExperiencePreference, experiencePreference);

        String biography = user.getString("biography");
        binding.etEditProfileBiography.setText(biography);

        ParseFile profileImage = user.getParseFile("profileImage");
        Glide.with(getContext()).load(profileImage.getUrl()).into(binding.ivEditProfileImage);

        JSONArray gallery = user.getJSONArray("gallery");
        binding.gvEditProfileGallery.setAdapter(new GalleryGridAdapter(getContext(), new JSONArray()));

        binding.gvEditProfileGallery.setAdapter(new GalleryGridAdapter(getContext(), gallery));


        String screenName = user.getString("screenName");
        binding.tvEditProfileScreenName.setText(screenName);


    }

    private void setButtonOnClickListeners(final FragmentEditProfileBinding binding) {

        binding.btnChangePreference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = binding.spPreference.getSelectedItem().toString();
                changeUserPreferences("workout_preference", value);

            }
        });

        binding.btnChangeExperience.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = binding.spExperience.getSelectedItem().toString();
                changeUserPreferences("user_experience", value);

            }
        });

        binding.btnChangeExperiencePreference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = binding.spExperiencePreference.getSelectedItem().toString();
                changeUserPreferences("experience_preference", value);

            }
        });

        binding.btnChangeBiography.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String biography = binding.etEditProfileBiography.getText().toString();
                changeBiography(biography);
            }
        });

        binding.ivAddImageToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean addProfileImage = true;
                Intent intent = new Intent(getContext(), PhotoActivity.class);
                intent.putExtra(EditProfileFragment.class.getSimpleName(), addProfileImage);
                startActivity(intent);
            }
        });

        binding.btnAddPhotoToGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONArray gallery = user.getJSONArray("gallery");
                if(gallery == null){
                    gallery = new JSONArray();
                }
                if(gallery.length() == 6){
                    Toast.makeText(getContext(), "Already at photo limit!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Boolean addProfileImage = false;
                Intent intent = new Intent(getContext(), PhotoActivity.class);
                intent.putExtra(EditProfileFragment.class.getSimpleName(), addProfileImage);
                startActivity(intent);
            }
        });

        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                goLoginActivity();
            }
        });

        binding.btnEditProfileViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean userProfile = true;
                goViewProfileActivity(userProfile);
            }
        });
    }

    private void goViewProfileActivity(Boolean userProfile) {
        Intent intent = new Intent(getContext(), ViewProfileActivity.class);
        intent.putExtra(EditProfileFragment.class.getSimpleName(), userProfile);
        startActivity(intent);
    }

    private void goLoginActivity() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void changeBiography(String biography) {
        user.put("biography", biography);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null){
                    Log.e(TAG, "Error while saving",e);
                    Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void changeUserPreferences(String property, String value) {
        user.put(property, value);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
            if(e!=null){
                Log.e(TAG, "Error while saving",e);
                Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_LONG).show();
            }
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

    private static double findDistance(double lat1, double lon1, double lat2, double lon2) {
//        double φ1 = lat1 * Math.PI/180, φ2 = lat2 * Math.PI/180, Δλ = (lon2-lon1) * Math.PI/180, R = 6371e3;
//        double d = Math.acos( Math.sin(φ1)*Math.sin(φ2) + Math.cos(φ1)*Math.cos(φ2) * Math.cos(Δλ) ) * R;
        double R = 3958.8; // miles
        double φ1 = lat1 * Math.PI/180; // φ, λ in radians
        double φ2 = lat2 * Math.PI/180;
        double Δφ = (lat2-lat1) * Math.PI/180;
        double Δλ = (lon2-lon1) * Math.PI/180;

        double a = Math.sin(Δφ/2) * Math.sin(Δφ/2) +
                        Math.cos(φ1) * Math.cos(φ2) *
                                Math.sin(Δλ/2) * Math.sin(Δλ/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double d = R * c; // in miles
        Log.i(TAG, lat1+","+lon1+","+lat2+","+lon2);
        return d;


//        if ((lat1 == lat2) && (lon1 == lon2)) {
//            Log.i(TAG, "Returning 0");
//            return 0;
//        }
//        else {
//            double theta = lon1 - lon2;
//            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
//            dist = Math.acos(dist);
//            dist = Math.toDegrees(dist);
//            dist = dist * 60 * 1.1515;
//            return (dist);
//        }
    }

}