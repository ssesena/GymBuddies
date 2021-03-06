package com.example.gymbuddies;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
import android.widget.AdapterViewFlipper;
import android.widget.ImageSwitcher;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.gymbuddies.adapters.FlipperAdapter;
import com.example.gymbuddies.adapters.HomeFeedAdapter;
import com.example.gymbuddies.databinding.ActivityViewProfileBinding;
import com.example.gymbuddies.fragments.ChatFragment;
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
    public static final int REQUEST_CODE = 42;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

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

        JSONArray allPhotos = null;

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
//            Glide.with(this).load(matchProfileImageUrl).into(binding.ivViewProfileImage);

            try {
                allPhotos = getAllPhotos(matchProfileImageUrl, matchGallery);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        else {


            binding.btnFindGyms.setVisibility(View.GONE);
            binding.btnStartPrivateChat.setVisibility(View.GONE);

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
//            Glide.with(this).load(matchProfileImageUrl).into(binding.ivViewProfileImage);

            try {
                allPhotos = getAllPhotos(matchProfileImageUrl, matchGallery);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        setOnCLickListeners(matchId);
        FlipperAdapter flipperAdapter = new FlipperAdapter(this, allPhotos);
        binding.adapterViewFlipper.setAdapter(flipperAdapter);
    }

    private JSONArray getAllPhotos(String profileImageUrl, JSONArray gallery) throws JSONException {
        JSONObject photo = new JSONObject();
        photo.put("url", profileImageUrl);
        JSONArray finalPhotos = new JSONArray();
        finalPhotos.put(photo);
        for(int i = 0; i < gallery.length(); i++){
            finalPhotos.put(gallery.getJSONObject(i));
        }
        return finalPhotos;
    }

//    private void flipperImages(PhotoView photoView){
//
//    }


    private void setOnCLickListeners(final String matchId) {


        if(matchId != null) {
            binding.btnStartPrivateChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ViewProfileActivity.this, PrivateChatActivity.class);
                    intent.putExtra("matchId", matchId);
                    Log.i(TAG, matchId);
                    intent.putExtra("isNewChat", true);
                    startActivityForResult(intent, REQUEST_CODE);
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



    public void onPrevClicked(View view){
        binding.adapterViewFlipper.setInAnimation(this, R.animator.left_in);
        binding.adapterViewFlipper.setOutAnimation(this, R.animator.right_out);
        binding.adapterViewFlipper.showPrevious();
    }

    public void onNextClicked(View view){
        binding.adapterViewFlipper.setInAnimation(this, R.animator.right_in);
        binding.adapterViewFlipper.setOutAnimation(this, R.animator.left_out);
        binding.adapterViewFlipper.showNext();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            Intent intent = new Intent(this, MainActivity.class);
            Boolean newchat = true;
            intent.putExtra(ViewProfileActivity.class.getSimpleName(), newchat);
            startActivity(intent);
        }
    }
}