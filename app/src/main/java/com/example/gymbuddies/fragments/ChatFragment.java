package com.example.gymbuddies.fragments;

import android.os.Bundle;

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

import org.json.JSONArray;

import static com.parse.Parse.getApplicationContext;


public class ChatFragment extends Fragment {
    ChatPreviewAdapter chatPreviewAdapter;
    JSONArray chats;

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


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.rvChatPreviews.setLayoutManager(linearLayoutManager);

        chatPreviewAdapter = new ChatPreviewAdapter(getContext(), chats);
        binding.rvChatPreviews.setAdapter(chatPreviewAdapter);

        return binding.getRoot();
    }
}