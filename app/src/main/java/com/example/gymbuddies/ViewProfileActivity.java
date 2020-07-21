package com.example.gymbuddies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.gymbuddies.databinding.ActivityViewProfileBinding;
import com.example.gymbuddies.fragments.EditProfileFragment;
import com.google.gson.Gson;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ViewProfileActivity extends AppCompatActivity {

    public int photoIndex;
    ActivityViewProfileBinding binding;
    JSONArray matchGallery = null;
    String matchProfileImageUrl = null;
    public static final String TAG = "ViewProfileActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        photoIndex = -1;

        //Getting intent from either home fee or edit profile screen
        Intent intent = getIntent();

        //Getting a boolean value to determine whether or not to display the user or match screen
        Boolean userProfile = intent.getBooleanExtra(EditProfileFragment.class.getSimpleName(), false);
        if(!userProfile) {

            //Retrieving all of the info from the properties for the match and populating views
            String matchBio = intent.getStringExtra("biography");
            String matchName = intent.getStringExtra("screenName");
            matchProfileImageUrl = intent.getStringExtra("profileImage");
            String userExperience = intent.getStringExtra("user_experience");
            String experiencePreference = intent.getStringExtra("experience_preference");
            String workoutPreference = intent.getStringExtra("workout_preference");
            binding.tvViewProfilePreference.setText("Looking for " + experiencePreference + " " + workoutPreference);
            binding.tvViewProfileExperience.setText(userExperience);
            try {
                matchGallery = new JSONArray(intent.getStringExtra("gallery"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            binding.tvViewProfileBiography.setText(matchBio);
            binding.tvViewProfileScreenName.setText(matchName);
            Glide.with(this).load(matchProfileImageUrl).into(binding.ivViewProfileImage);
        }
        else {

            //Retrieving current logged in user
            ParseUser user = ParseUser.getCurrentUser();

            //Populating screen with user info
            String userBio = user.getString("biography");
            String userName = user.getString("screenName");
            matchProfileImageUrl = user.getParseFile("profileImage").getUrl();
            String userExperience = user.getString("user_experience");
            String experiencePreference = user.getString("experience_preference");
            String workoutPreference = user.getString("workout_preference");
            binding.tvViewProfilePreference.setText("Looking for " + experiencePreference + " " + workoutPreference);
            binding.tvViewProfileExperience.setText(userExperience);
            matchGallery = user.getJSONArray("gallery");
            binding.tvViewProfileBiography.setText(userBio);
            binding.tvViewProfileScreenName.setText(userName);
            Glide.with(this).load(matchProfileImageUrl).into(binding.ivViewProfileImage);
        }

        setOnCLickListeners();
    }

    private void setOnCLickListeners() {
        binding.ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean next = false;
                try {
                    showNextImage(next);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        binding.ivForwardArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean next = true;
                try {
                    showNextImage(next);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showNextImage(Boolean next) throws JSONException {
        if(next){
            photoIndex++;
            if(photoIndex < matchGallery.length()){
                String matchImageUrl = matchGallery.getJSONObject(photoIndex).getString("url");
                Glide.with(this).load(matchImageUrl).into(binding.ivViewProfileImage);
            }
            else{
                photoIndex--;
            }
        }
        else{
            photoIndex--;
            if(photoIndex < 0){
                Glide.with(this).load(matchProfileImageUrl).into(binding.ivViewProfileImage);
                photoIndex = -1;
            }
            else{
                String matchImageUrl = matchGallery.getJSONObject(photoIndex).getString("url");
                Glide.with(this).load(matchImageUrl).into(binding.ivViewProfileImage);
            }
        }
    }
}