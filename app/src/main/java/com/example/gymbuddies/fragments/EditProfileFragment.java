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

import java.util.List;

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

        return binding.getRoot();
    }

    private void showAll(FragmentEditProfileBinding binding) throws Exception{
            String workoutPreference = user.getJSONObject("preferences").getString("workout_preference");
            setSpinnerToValue(binding.spPreference, workoutPreference);

            String userExperience = user.getJSONObject("preferences").getString("user_experience");
            setSpinnerToValue(binding.spExperience, userExperience);

            String experiencePreference = user.getJSONObject("preferences").getString("experience_preference");
            setSpinnerToValue(binding.spExperiencePreference, experiencePreference);

            String biography = user.getString("biography");
            binding.etEditProfileBiography.setText(biography);

            ParseFile profileImage = user.getParseFile("profileImage");
            if(profileImage != null) {
                Glide.with(getContext()).load(profileImage.getUrl()).into(binding.ivEditProfileImage);
            }

            JSONArray gallery = user.getJSONArray("gallery");
            if(gallery == null){
                binding.gvEditProfileGallery.setAdapter(new GalleryGridAdapter(getContext(), new JSONArray()));
            }
            else{
                binding.gvEditProfileGallery.setAdapter(new GalleryGridAdapter(getContext(), gallery));
            }

            String screenName = user.getString("screenName");
            binding.tvEditProfileScreenName.setText(screenName);


    }

    private void setButtonOnClickListeners(final FragmentEditProfileBinding binding) {

        binding.btnChangePreference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = binding.spPreference.getSelectedItem().toString();
                try {
                    changeUserPreferences("workout_preference", value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        binding.btnChangeExperience.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = binding.spExperience.getSelectedItem().toString();
                try {
                    changeUserPreferences("user_experience", value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        binding.btnChangeExperiencePreference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = binding.spExperiencePreference.getSelectedItem().toString();
                try {
                    changeUserPreferences("experience_preference", value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    private void changeUserPreferences(String property, String value) throws JSONException {

        JSONObject jsonObject = user.getJSONObject("preferences");
        if(jsonObject == null){
            jsonObject = new JSONObject();
        }
        jsonObject.put(property, value);
        user.put("preferences", jsonObject);
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

}