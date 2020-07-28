package com.example.gymbuddies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.gymbuddies.adapters.MessageAdapter;
import com.example.gymbuddies.databinding.ActivityPrivateChatBinding;

import org.json.JSONArray;

public class PrivateChatActivity extends AppCompatActivity {

    MessageAdapter messageAdapter;
    JSONArray messages;

    ActivityPrivateChatBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrivateChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.rvMessages.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        binding.rvMessages.setLayoutManager(linearLayoutManager);

        messageAdapter = new MessageAdapter(PrivateChatActivity.this, messages);
        binding.rvMessages.setAdapter(messageAdapter);
    }
}