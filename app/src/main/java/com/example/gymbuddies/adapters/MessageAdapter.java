package com.example.gymbuddies.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gymbuddies.R;
import com.example.gymbuddies.databinding.UserMessageBinding;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_USER = 0;
    public static final int MSG_TYPE_MATCH = 1;

    public static final String TAG = "MessageAdapter";

    Context context;
    JSONArray messages;

    public MessageAdapter(Context context, JSONArray messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if(viewType == MSG_TYPE_USER){
            view = LayoutInflater.from(context).inflate(R.layout.user_message, parent, false);
        }
        else{
            view = LayoutInflater.from(context).inflate(R.layout.match_message, parent, false);
        }
        return new MessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JSONObject message = null;

        try {
            message = messages.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            holder.bind(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        if(messages != null){
            return messages.length();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        ParseUser user = ParseUser.getCurrentUser();
        try {
            if(messages.getJSONObject(position).getString("user_id").equals(user.getObjectId())){
                return MSG_TYPE_USER;
            }
            return MSG_TYPE_MATCH;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvMessage;
        ImageView ivProfileImage;
        Boolean isUserMessage = true;

        @SuppressLint("ResourceType")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            ivProfileImage = itemView.findViewById(R.id.ivMessageProfileImage);
        }

        @SuppressLint("CheckResult")
        public void bind(JSONObject message) throws JSONException {

            Glide.with(context).load(message.getString("profileImageUrl")).circleCrop().into(ivProfileImage);
            tvMessage.setText(message.getString("message"));
        }

    }

    public void clear() {
        messages = new JSONArray();
//        notifyDataSetChanged();
    }

    public void addAll(JSONArray newMessages) throws JSONException {
        for(int i = 0; i < newMessages.length(); i++){
            messages.put(newMessages.getJSONObject(i));
        }
        notifyDataSetChanged();
    }
}
