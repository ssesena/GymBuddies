package com.example.gymbuddies;

import android.app.Application;

import com.example.gymbuddies.models.Chat;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Chat.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("sams-gymbuddies") // should correspond to APP_ID env variable
                .clientKey("SamsGymBuddiesKey")  // set explicitly unless clientKey is explicitly configured on Parse server
                .server("https://sams-gymbuddies.herokuapp.com/parse/").build());
    }
}
