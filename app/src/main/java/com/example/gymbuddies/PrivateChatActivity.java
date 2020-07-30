package com.example.gymbuddies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.example.gymbuddies.adapters.ChatPreviewAdapter;
import com.example.gymbuddies.adapters.MessageAdapter;
import com.example.gymbuddies.databinding.ActivityPrivateChatBinding;
import com.example.gymbuddies.models.Chat;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.Date;

public class PrivateChatActivity extends AppCompatActivity {

    public static final String TAG = "PrivateChatActivity";

    MessageAdapter messageAdapter;
    JSONArray messages;

    ActivityPrivateChatBinding binding;

    ParseUser user = ParseUser.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrivateChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();

        //Retrieving info from previous activity or fragment
        final String matchId = intent.getStringExtra("matchId");
//        Log.i(TAG, matchId);
        final Boolean isNewChat = intent.getBooleanExtra("isNewChat", false);
        Chat chat = null;
        String chatId = null;

        if(isNewChat){
            Log.i(TAG, "Chat did not exist");
        }
        else{
            Log.i(TAG, "Chat already exists");
            chat = (Chat) Parcels.unwrap(intent.getParcelableExtra(ChatPreviewAdapter.class.getSimpleName()));
            chatId = chat.getObjectId();
            messages = chat.getMessages();
        }

        binding.rvMessages.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        binding.rvMessages.setLayoutManager(linearLayoutManager);

        messageAdapter = new MessageAdapter(PrivateChatActivity.this, messages);
        binding.rvMessages.setAdapter(messageAdapter);

        //Setting listener on button to send message
        final String finalChatId = chatId;
        binding.btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = binding.etComposeMessage.getText().toString();
                try {
                    sendMessage(message, matchId, isNewChat, finalChatId);
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Chat findChat(String chatId) throws ParseException {
        ParseQuery<Chat> query = ParseQuery.getQuery(Chat.class);
        query.whereEqualTo("objectId", chatId);
        return query.find().get(0);
    }

    private ParseUser findMatch(String matchId) throws ParseException{
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        Log.i(TAG, matchId);
        query.whereEqualTo("objectId", matchId);
        return query.find().get(0);
    }

    private void sendMessage(String message, String matchId, Boolean isNewChat, String chatId) throws JSONException, ParseException {
        //First Check if a chat between the user an their match already exists

        //If it does not exist, I need to create a new Chat Object, add that chat object to the user and match's chats, and finally add the message
//        ParseUser match = findMatch(matchId);
        Chat chat = null;
        if(isNewChat){
            chat = new Chat();
            chat.setUsers(matchId);
//            chat.save();
//            chatId = chat.getObjectId();
//
//            Log.i(TAG, "ChatId: "+ chatId);
//
//            JSONObject newChat = new JSONObject();

//            //Adding the new chat to the user's list of chats
//            JSONArray userChats = user.getJSONArray("chats");
//            newChat.put("chatId", chatId);
//            newChat.put("matchId", matchId);
//            userChats.put(newChat);
//            user.put("chats", userChats);
//            Log.i(TAG,"User's Chats: " + userChats.toString());
//
//
//            //Adding the new chat to the match's list of chats
//            JSONArray matchChats = match.getJSONArray("chats");
//            newChat.put("chatId", chatId);
//            newChat.put("matchId", user.getObjectId());
//            matchChats.put(newChat);
//            match.put("chats", matchChats);
//            Log.i(TAG,"Match Chats: "+matchChats.toString());

        }
        //If It does exist all I need to do is add the message using the method addMessage and save it in the background
        else{
            chat = findChat(chatId);
        }

        //Adding and saving the new message
        chat.addMessage(user, message);
        Log.i(TAG, message);
        chat.save();

        //I also have to make sure to update the order of the user's current chats
//        orderChats(user, chat);
//        orderChats(match, chat);
    }

    private void orderChats(ParseUser newUser, Chat chat) throws JSONException, ParseException {
        JSONArray chats = newUser.getJSONArray("chats");
        Log.i(TAG,newUser.getUsername() + chats.toString());

        for(int i = 0; i < chats.length(); i++){
            JSONObject newChat = chats.getJSONObject(i);
            if(newChat.getString("chatId").equals(chat.getObjectId())){
                newChat.put("updatedAt", chat.getUpdatedAt().toString());
                chats.remove(i);
                chats.put(newChat);
                Log.i(TAG, String.valueOf(chat.getUpdatedAt()));
                break;
            }
        }
        newUser.put("chats", chats);
        Log.i(TAG,"final: "+newUser.getUsername() + chats.toString());
        newUser.save();

    }
}