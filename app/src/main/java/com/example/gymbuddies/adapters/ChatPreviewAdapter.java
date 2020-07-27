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
import com.example.gymbuddies.databinding.ItemChatPreviewBinding;
import com.example.gymbuddies.databinding.ItemHomeMatchProfileBinding;
import com.example.gymbuddies.models.Chat;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ChatPreviewAdapter extends RecyclerView.Adapter<ChatPreviewAdapter.ViewHolder>{

    public static final String TAG = "ChatPreviewAdapter";

    Context context;
    JSONArray chats;

    public ChatPreviewAdapter(Context context, JSONArray chats) {
        this.context = context;
        this.chats = chats;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChatPreviewBinding binding = ItemChatPreviewBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        JSONObject chat = null;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Clicked!");
                if(position != RecyclerView.NO_POSITION){
                    JSONObject clickedMatch = null;
                    try {
                        clickedMatch = chats.getJSONObject(position);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    holder.displayMatch(clickedMatch, false);


                }
            }
        });

        try {
            chat = chats.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.clear();
        try {
            holder.bind(chat);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        if(chats !=null) {
            return chats.length();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ItemChatPreviewBinding binding;

        public ViewHolder(@NonNull ItemChatPreviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(JSONObject chat) throws JSONException {

            //Finding the chat id
            String chatId = chat.getString("chat_id");

            //Querying Parse for that chat id
            ParseQuery<Chat> query = ParseQuery.getQuery(Chat.class);
            query.whereEqualTo("objectId", chatId);
            query.findInBackground(new FindCallback<Chat>() {
                @Override
                public void done(List<Chat> chats, ParseException e) {
                    if(e!=null){
                        Toast.makeText(context, "Trouble processing request", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, e.toString());
                    }

                    //chats should only contain one chat object matching the chat id from earlier
                    Chat chat = chats.get(0);
                    String matchId = null;
                    String lastMessage = null;

                    try {
                        //We want to find the user's match's id, so we check to make sure we don't pull up the user's own info when displaying
                        //a chat preview
                        matchId = chat.getUsers().getString(0);
                        if (matchId.equals(ParseUser.getCurrentUser().getObjectId())) {
                            matchId = chat.getUsers().getString(1);
                        }

                        //We want to find the last message and display it as a preview
                        lastMessage = chat.getMessages().getJSONObject(chat.getMessages().length()-1).getString("message");
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }

                    //Populate screen with last message
                    binding.tvChatScreenMessage.setText(lastMessage);

                    ParseQuery<ParseUser> newQuery = ParseUser.getQuery();
                    newQuery.whereEqualTo("objectId", matchId);
                    newQuery.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> matches, ParseException e) {
                            if(e!=null){
                                Toast.makeText(context, "Trouble processing request", Toast.LENGTH_SHORT).show();
                                Log.i(TAG, e.toString());
                            }

                            //Finding match's info and populating the view
                            ParseUser match = matches.get(0);
                            String matchName = match.getString("screenName");
                            String matchImageUrl = match.getParseFile("profileImage").getUrl();

                            binding.tvChatScreenName.setText(matchName);
                            Glide.with(context).load(matchImageUrl).into(binding.ivChatProfileImage);
                        }
                    });
                }
            });
        }
        public void clear(){
            Glide.with(context).clear(binding.ivChatProfileImage);
        }
    }
}
