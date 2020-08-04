package com.example.gymbuddies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;

import com.bumptech.glide.Glide;
import com.example.gymbuddies.adapters.HomeFeedAdapter;
import com.example.gymbuddies.databinding.ActivityViewProfileBinding;
import com.example.gymbuddies.fragments.EditProfileFragment;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.google.gson.Gson;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

public class ViewProfileActivity extends AppCompatActivity {

    public int photoIndex;
    ActivityViewProfileBinding binding;
    JSONArray matchGallery = null;
    String matchProfileImageUrl = null;
    public static final String TAG = "ViewProfileActivity";
    ParseUser match;
    Matrix matrix = new Matrix();
    float scale = 1f;
    ScaleGestureDetector scaleGestureDetector;
    PhotoViewAttacher photoViewAttacher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Animation right_to_left_in = AnimationUtils.loadAnimation(this, R.anim.right_to_left_in);
        Animation right_to_left_out = AnimationUtils.loadAnimation(this, R.anim.right_to_left_out);
        Animation left_to_right_in = AnimationUtils.loadAnimation(this, R.anim.left_to_right_in);
        Animation left_to_right_out = AnimationUtils.loadAnimation(this, R.anim.left_to_right_out);
//        binding.isImageSlideLeft.setInAnimation(right_to_left_in);
//        binding.isImageSlideLeft.setOutAnimation(right_to_left_out);
//        binding.isImageSlideRight.setOutAnimation(left_to_right_out);
//        binding.isImageSlideRight.setInAnimation(left_to_right_in);

        photoIndex = -1;

        //Getting intent from either home fee or edit profile screen
        Intent intent = getIntent();
        String matchId = intent.getStringExtra("matchId");
        if(matchId == null){
            Log.i(TAG, "matchId is null");
        }
        else{
            Log.i(TAG, "matchId is not null");
        }

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
                match = (ParseUser) Parcels.unwrap(intent.getParcelableExtra(HomeFeedAdapter.class.getSimpleName()));
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
        setOnCLickListeners(matchId);
    }

//    private void flipperImages(PhotoView photoView){
//
//    }


    private void setOnCLickListeners(final String matchId) {
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

        if(matchId != null) {
            binding.btnStartPrivateChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ViewProfileActivity.this, PrivateChatActivity.class);
                    intent.putExtra("matchId", matchId);
                    Log.i(TAG, matchId);
                    intent.putExtra("isNewChat", true);
                    startActivity(intent);
                }
            });
        }

        binding.btnFindGyms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewProfileActivity.this, GymMapActivity.class);
                intent.putExtra(ViewProfileActivity.class.getSimpleName(), Parcels.wrap(match));
                startActivity(intent);
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