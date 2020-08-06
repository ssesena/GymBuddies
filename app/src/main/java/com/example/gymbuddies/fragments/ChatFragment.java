package com.example.gymbuddies.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gymbuddies.PrivateChatActivity;
import com.example.gymbuddies.R;
import com.example.gymbuddies.adapters.ChatPreviewAdapter;
import com.example.gymbuddies.adapters.MessageAdapter;
import com.example.gymbuddies.databinding.FragmentChatBinding;
import com.example.gymbuddies.models.Chat;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.parse.Parse.getApplicationContext;


public class ChatFragment extends Fragment {
    ChatPreviewAdapter chatPreviewAdapter;
    List<Chat> chats;
    public static final int REQUEST_CODE = 40;

    RecyclerView rvChats;

    FragmentChatBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(getLayoutInflater());

        try {
            chats = findChats();
        } catch (ParseException | JSONException e) {
            e.printStackTrace();
        }


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.rvChatPreviews.setLayoutManager(linearLayoutManager);

        chatPreviewAdapter = new ChatPreviewAdapter(getContext(), chats);
        binding.rvChatPreviews.setAdapter(chatPreviewAdapter);

        return binding.getRoot();
    }

    private List<Chat> findChats() throws ParseException, JSONException {

        //Get userId and find chats user is part of
        ArrayList<Chat> usersChats = new ArrayList<>();
        ParseUser user = ParseUser.getCurrentUser();
        String userId = user.getObjectId();

        //Find all current chats between all users in order of most recent
        ParseQuery<Chat> query = ParseQuery.getQuery(Chat.class);
        query.addDescendingOrder(Chat.KEY_UPDATED_AT);
        List<Chat> allChats = query.find();

        for(Chat chat:allChats){
            JSONArray chatUsers = chat.getJSONArray("users");
            if(userId.equals(chatUsers.getString(0)) || userId.equals(chatUsers.getString(1))){
                usersChats.add(chat);
            }
        }

        return usersChats;
    }

}