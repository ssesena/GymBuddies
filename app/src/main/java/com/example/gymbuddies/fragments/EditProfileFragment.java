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
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;


public class EditProfileFragment extends Fragment {
    ParseUser user = ParseUser.getCurrentUser();
    public static final String TAG = "EditProfileFragment";
    private FragmentEditProfileBinding binding;
    public static final int GALLERY_PHOTO_LIMIT = 6;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


        return binding.getRoot();
    }

    private void findMatches() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", user.getUsername());
        query.whereEqualTo("workout_preference", user.getString("workout_preference"));
        String[]matchPreferences = findPreferredExperience(user.getString("user_experience"));
        String[]userPreferences = findPreferredExperience(user.getString("experience_preference"));
        query.whereContainedIn("experience_preference", Arrays.asList(matchPreferences));
        query.whereContainedIn("user_experience", Arrays.asList(userPreferences));
        Log.i(TAG, query.toString());
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
                findMatches();


            }
        });

        binding.btnChangeExperience.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = binding.spExperience.getSelectedItem().toString();
                changeUserPreferences("user_experience", value);
                findMatches();


            }
        });

        binding.btnChangeExperiencePreference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = binding.spExperiencePreference.getSelectedItem().toString();
                changeUserPreferences("experience_preference", value);
                findMatches();


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
//        user.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//            if(e!=null){
//                Log.e(TAG, "Error while saving",e);
//                Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_LONG).show();
//            }
//            }
//        });
        try {
            user.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
    }

    private String[] findPreferredExperience(String experiencePreference){
        if(experiencePreference.equals("Beginner")){
            return new String[]{"Beginner", "Intermediate"};
        }
        else if(experiencePreference.equals("Intermediate")){
            return new String[]{"Beginner", "Intermediate", "Advanced"};
        }
        else if (experiencePreference.equals("Advanced")){
            return new String[]{"Intermediate", "Advanced", "Expert"};
        }
        return new String[]{"Advanced", "Expert"};
    }

}