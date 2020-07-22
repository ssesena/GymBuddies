package com.example.gymbuddies.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
        matches = ParseUser.getCurrentUser().getJSONArray("matches");
        binding = FragmentHomeBinding.inflate(getLayoutInflater());

        ArrayAdapter adapterPreferences = ArrayAdapter.createFromResource(getContext(), R.array.filter_options, R.layout.spinner_item1);
        binding.spinnerHomeFeed.setAdapter(adapterPreferences);
        homeFeedAdapter = new HomeFeedAdapter(getContext(), matches);
        binding.rvHomeMatchProfiles.setAdapter(homeFeedAdapter);
        binding.rvHomeMatchProfiles.setLayoutManager(new LinearLayoutManager(getContext()));


        return binding.getRoot();
    }

}