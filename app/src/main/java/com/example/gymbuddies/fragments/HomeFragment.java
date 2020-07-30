package com.example.gymbuddies.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    public static final String TAG = "HomeFragment";
    FragmentHomeBinding binding;
    HomeFeedAdapter homeFeedAdapter;
    JSONArray matches;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ParseUser.getCurrentUser().fetchInBackground();
        matches = ParseUser.getCurrentUser().getJSONArray("filtered_matches");
        binding = FragmentHomeBinding.inflate(getLayoutInflater());

        ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(), R.array.filter_options, R.layout.custom_spinner_selected_item);
        adapter.setDropDownViewResource(R.layout.spinner_item1);

        binding.spinnerHomeFeed.setAdapter(adapter);
        homeFeedAdapter = new HomeFeedAdapter(getContext(), matches);
        binding.rvHomeMatchProfiles.setAdapter(homeFeedAdapter);
        binding.rvHomeMatchProfiles.setLayoutManager(new LinearLayoutManager(getContext()));

        final ParseUser user = ParseUser.getCurrentUser();

        if(user.getJSONArray("matches") == null){
            user.put("matches", new JSONArray());
            try {
                user.save();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        binding.spinnerHomeFeed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l){

                //Get the selected item from the filter options
                String filterOption = binding.spinnerHomeFeed.getSelectedItem().toString();

                //Save the filter option to Parse
                user.put("filter_option", filterOption);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e!=null){
                            Log.e(TAG, "Error while saving",e);
                            Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_LONG).show();
                        }

                        //After changing the filter option, the "filtered_matches" property needs to be updated
                        updateFilteredMatches();
                    }

                    //First, Iterate through the JSONObjects in the user's "matches" property
                    //Second, query the database to find the ParseUser that correlates to each JSONObjects "objectId" property
                    //Third, Add all of those ParseUsers to a separate ArrayList
                    //Fourth, Call a different function to choose the order of which ones to display

                    private void updateFilteredMatches() {
                        final ArrayList<ParseUser> potentialMatches = new ArrayList<>();
                        final JSONArray matches = user.getJSONArray("matches");
                        for(int i = 0; i < matches.length(); i++){
                            JSONObject match = null;
                            ParseQuery<ParseUser> query = ParseUser.getQuery();
                            try {
                                match = matches.getJSONObject(i);
                                query.whereEqualTo("objectId", match.getString("objectId"));
                                List<ParseUser> userMatches =  query.find();
                                potentialMatches.add(userMatches.get(0));
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }
                        sortMatches(potentialMatches);
                    }

                    private void sortMatches(List<ParseUser> userMatches){
                        ArrayList<ParseUser> filteredMatches = new ArrayList<>();
                        ParseUser user = ParseUser.getCurrentUser();
                        String userFilterOption = user.getString("filter_option");
                        String userExperience = user.getString("user_experience");
                        String userExperiencePreference = user.getString("experience_preference");

                        ArrayList<ParseUser> lastMatches = new ArrayList<>();
                        for(ParseUser userMatch:userMatches) {

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
                                if(userExperiencePreference.equals(matchExperience)){
                                    if(matchFilterOption.equals("Preference Only")){
                                        if(matchExperiencePreference.equals(userExperience)){
                                            filteredMatches.add(userMatch);
                                        }
                                    }
                                    else{
                                        filteredMatches.add(userMatch);
                                    }
                                }
                            } else if(userFilterOption.equals("Preference First")){
                                if(matchFilterOption.equals("Preference Only") && !matchExperiencePreference.equals(userExperience)){
                                    break;
                                }
                                else{
                                    if(userExperiencePreference.equals(matchExperience)){
                                        filteredMatches.add(userMatch);
                                    }
                                    else{
                                        lastMatches.add(userMatch);
                                    }
                                }
                            }
                        }
                        filteredMatches.addAll(lastMatches);
                        JSONArray matchesToShow = new JSONArray();
                        for(ParseUser match:filteredMatches){
                            matchesToShow.put(match);
                        }
                        user.put("filtered_matches", matchesToShow);
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e != null){
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

        return binding.getRoot();
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