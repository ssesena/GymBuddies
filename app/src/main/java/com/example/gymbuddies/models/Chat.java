package com.example.gymbuddies.models;

import android.text.format.DateUtils;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.Date;


@ParseClassName("Chat")
@Parcel(analyze={Chat.class})
public class Chat extends ParseObject {
    public static final String KEY_USERS = "users";
    public static final String KEY_MESSAGES = "messages";

    public Chat(){
    }

    public JSONArray getUsers(){
        return getJSONArray(KEY_USERS);
    }

    public JSONArray getMessages(){
        return getJSONArray(KEY_MESSAGES);
    }

    public void setUsers(String matchId){
        JSONArray users = new JSONArray();
        ParseUser user =ParseUser.getCurrentUser();
        users.put(user.getObjectId());
        users.put(matchId);
        put(KEY_USERS, users);
    }

    public void addMessage(ParseUser user, String message) throws JSONException {

        //Need to create a JSON object to keep track of message, the author, and the author's profileIamge for displaying to the user if possible
        JSONArray allMessages = getMessages();
        if(allMessages ==  null){
            allMessages = new JSONArray();
        }

        JSONObject messageObject = new JSONObject();
        messageObject.put("user_id", user.getObjectId());
        messageObject.put("message", message);
        messageObject.put("profileImageUrl",user.getParseFile("profileImage").getUrl());

        //Finding the current time and adding that to message object to display messages in correct order
        Date date = new Date(System.currentTimeMillis());
        messageObject.put("createdAt", date);
        allMessages.put(messageObject);
        this.put(KEY_MESSAGES, allMessages);
    }

}
