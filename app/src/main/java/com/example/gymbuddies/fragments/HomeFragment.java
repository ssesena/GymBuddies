package com.example.gymbuddies.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    public static final String TAG = "HomeFragment";
    FragmentHomeBinding binding;
    HomeFeedAdapter homeFeedAdapter;
    JSONArray matches;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        ParseUser.getCurrentUser().fetchInBackground();
        matches = ParseUser.getCurrentUser().getJSONArray("matches");
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        homeFeedAdapter = new HomeFeedAdapter(getContext(), matches);
        binding.rvHomeMatchProfiles.setAdapter(homeFeedAdapter);
        binding.rvHomeMatchProfiles.setLayoutManager(new LinearLayoutManager(getContext()));
//        ParseUser user = ParseUser.getCurrentUser();
//        JSONArray matches = user.getJSONArray("matches");
//        if(matches == null){
//            matches = new JSONArray();
//        }
//        matches.put(user);
//        user.put("matches", matches);
//        user.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if(e != null){
//                    Log.e(TAG, "Error while saving",e);
//                    Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_LONG).show();
//                }
//            }
//        });

        return binding.getRoot();
    }

}