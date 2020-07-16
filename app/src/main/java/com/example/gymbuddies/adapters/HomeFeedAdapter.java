package com.example.gymbuddies.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gymbuddies.databinding.ItemHomeMatchProfileBinding;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        JSONObject match = null;
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                Log.i(TAG, "Clicked!");
////                if(position != RecyclerView.NO_POSITION){
////                    try {
////                        ParseUser user = matches.getJSONObject(position);
////                    } catch (JSONException e) {
////                        e.printStackTrace();
////                    }
////                }
////            }
////        });
        
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
        return matches.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ItemHomeMatchProfileBinding binding;


        public ViewHolder(@NonNull ItemHomeMatchProfileBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(JSONObject match) throws JSONException {
            getMatch(match);
        }

        public void getMatch(JSONObject match){
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            try {
                query.whereEqualTo("objectId", match.getString("objectId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> userMatches, ParseException e) {
                    if(e==null){
                        ParseUser userMatch = userMatches.get(0);
                        Log.i(TAG, userMatch.toString());
                        Toast.makeText(context, "Match found!", Toast.LENGTH_SHORT).show();
                        String matchImageUrl = null;
                        matchImageUrl = userMatch.getParseFile("profileImage").getUrl();
                        String matchBio = userMatch.getString("biography");
                        String matchName = userMatch.getString("screenName");

                        Glide.with(context).load(matchImageUrl).into(binding.ivHomeProfileImage);
                        binding.tvHomeBiography.setText(matchBio);
                        binding.tvHomeScreenName.setText(matchName);
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
