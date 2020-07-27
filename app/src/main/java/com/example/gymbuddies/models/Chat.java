package com.example.gymbuddies.models;

import android.text.format.DateUtils;

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

    public Date getUpdatedAt(){
        return getUpdatedAt();
    }

    public void setUsers(ParseUser user, ParseUser match){
        JSONArray users = new JSONArray();
        users.put(user.getObjectId());
        users.put(match.getObjectId());
        put(KEY_USERS, users);
    }

    public void addMessage(ParseUser user, String message) throws JSONException {
        JSONArray allMessages = getMessages();
        JSONObject messageObject = new JSONObject();
        messageObject.put("user_id", user.getObjectId());
        messageObject.put("message", message);

        //Finding the current time and adding that to message object to display messages in correct order
        Date date = new Date(System.currentTimeMillis());
        messageObject.put("createdAt", date);
        allMessages.put(messageObject);
    }

}
