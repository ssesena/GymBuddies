package com.example.gymbuddies;

import android.app.Application;

import com.parse.Parse;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("sams-gymbuddies") // should correspond to APP_ID env variable
                .clientKey("SamsGymBuddiesKey")  // set explicitly unless clientKey is explicitly configured on Parse server
                .server("https://sams-gymbuddies.herokuapp.com/parse/").build());
    }
}
