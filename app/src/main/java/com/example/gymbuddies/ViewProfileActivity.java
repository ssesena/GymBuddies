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
        Intent intent = getIntent();
        Boolean userProfile = intent.getBooleanExtra(EditProfileFragment.class.getSimpleName(), false);
        if(!userProfile) {

            String matchBio = intent.getStringExtra("biography");
            String matchName = intent.getStringExtra("screenName");
            matchProfileImageUrl = intent.getStringExtra("profileImage");
            try {
                JSONObject matchPreferences = new JSONObject(intent.getStringExtra("preferences"));
                String userExperience = matchPreferences.getString("user_experience");
                String experiencePreference = matchPreferences.getString("experience_preference");
                String workoutPreference = matchPreferences.getString("workout_preference");
                binding.tvViewProfilePreference.setText("Looking for " + experiencePreference + " " + workoutPreference);
                binding.tvViewProfileExperience.setText(userExperience);
                matchGallery = new JSONArray(intent.getStringExtra("gallery"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            binding.tvViewProfileBiography.setText(matchBio);
            binding.tvViewProfileScreenName.setText(matchName);
            Glide.with(this).load(matchProfileImageUrl).into(binding.ivViewProfileImage);
        }
        else {
            ParseUser user = ParseUser.getCurrentUser();
            String userBio = user.getString("biography");
            String userName = user.getString("screenName");
            try {
                matchProfileImageUrl = user.getParseFile("profileImage").getUrl();
                JSONObject userPreferences = user.getJSONObject("preferences");
                String userExperience = userPreferences.getString("user_experience");
                String experiencePreference = userPreferences.getString("experience_preference");
                String workoutPreference = userPreferences.getString("workout_preference");
                binding.tvViewProfilePreference.setText("Looking for " + experiencePreference + " " + workoutPreference);
                binding.tvViewProfileExperience.setText(userExperience);
                matchGallery = user.getJSONArray("gallery");
            } catch (JSONException e) {
                e.printStackTrace();
            }
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