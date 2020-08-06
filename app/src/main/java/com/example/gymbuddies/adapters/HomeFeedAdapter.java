package com.example.gymbuddies.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.gymbuddies.ViewProfileActivity;
import com.example.gymbuddies.databinding.ItemHomeMatchProfileBinding;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class HomeFeedAdapter extends RecyclerView.Adapter<HomeFeedAdapter.ViewHolder> {

    public static final String TAG = "HomeFeedAdapter";
    Context context;
    JSONArray matches;

    public HomeFeedAdapter(Context context, JSONArray matches) {
        this.context = context;
        this.matches = matches;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHomeMatchProfileBinding binding = ItemHomeMatchProfileBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        JSONObject match = null;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Clicked!");
                if(position != RecyclerView.NO_POSITION){
                    JSONObject clickedMatch = null;
                    try {
                        clickedMatch = matches.getJSONObject(position);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    holder.displayMatch(clickedMatch, false);


                }
            }
        });
        
        try {
            match = matches.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.clear();
        try {
            holder.bind(match);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        if(matches != null) {
            return matches.length();
        }
        return 0;
    }

    public void clear() {
        matches = new JSONArray();
        notifyDataSetChanged();
    }

    public void addAll(JSONArray newMatches) throws JSONException {
        for(int i = 0; i < newMatches.length(); i++){
            matches.put(newMatches.getJSONObject(i));
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ItemHomeMatchProfileBinding binding;


        public ViewHolder(@NonNull ItemHomeMatchProfileBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(JSONObject match) throws JSONException {
            Boolean forHomeFeed = true;
            displayMatch(match, forHomeFeed);
        }

        public void displayMatch(JSONObject match, final Boolean forHomeFeed){
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            try {
                query.whereEqualTo("objectId", match.getString("objectId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> userMatches, ParseException e) {
                    if(e==null && forHomeFeed){
                        ParseUser userMatch = userMatches.get(0);
                        String matchImageUrl = userMatch.getParseFile("profileImage").getUrl();
                        String matchBio = userMatch.getString("biography");
                        String matchName = userMatch.getString("screenName");
                        String matchExperience = userMatch.getString("user_experience");

                        Glide.with(context).load(matchImageUrl).circleCrop().into(binding.ivHomeProfileImage);
                        binding.tvHomeBiography.setText(matchBio);
                        binding.tvHomeScreenName.setText(matchName);
                        binding.tvHomeExperience.setText(matchExperience);
                    }
                    else if(e==null && !forHomeFeed){
                        ParseUser userMatch = userMatches.get(0);
                        String matchImageUrl = null;
                        matchImageUrl = userMatch.getParseFile("profileImage").getUrl();
                        String matchBio = userMatch.getString("biography");
                        String matchName = userMatch.getString("screenName");
                        String matchWorkout = userMatch.getString("workout_preference");
                        String matchExperiencePreference = userMatch.getString("experience_preference");
                        String matchExperience = userMatch.getString("user_experience");
                        String matchGallery = userMatch.getJSONArray("gallery").toString();
                        String matchId = userMatch.getObjectId();
                        Log.i(TAG, matchId);

                        Intent intent = new Intent(context, ViewProfileActivity.class);
                        intent.putExtra("screenName",matchName);
                        intent.putExtra("biography",matchBio);
                        intent.putExtra("profileImage", matchImageUrl);
                        intent.putExtra("workout_preference", matchWorkout);
                        intent.putExtra("experience_preference", matchExperiencePreference);
                        intent.putExtra("user_experience", matchExperience);
                        intent.putExtra("gallery", matchGallery);
                        intent.putExtra("matchId", matchId);
                        intent.putExtra(HomeFeedAdapter.class.getSimpleName(), Parcels.wrap(userMatch));
                        context.startActivity(intent);
                    }
                    else{
                        Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        public void clear(){
            Glide.with(context).clear(binding.ivHomeProfileImage);
        }
    }
}
