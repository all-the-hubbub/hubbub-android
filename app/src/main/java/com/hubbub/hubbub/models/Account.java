package com.hubbub.hubbub.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

/**
 * Created by sgoldblatt on 5/1/17.
 */

@IgnoreExtraProperties
public class Account {
    public String handle;
    public String githubCreatedAt;
    public String githubToken;
    public long updatedAt;
    public HashMap<String, Event> events;

    public Account(){
        // Default constructor required for calls to DataSnapshot.getValue(Account.class)
    };

    public Account(String handle, String githubToken, int updatedAt, HashMap<String, Event> events) {
        this.handle = handle;
        this.githubToken = githubToken;
        this.updatedAt = updatedAt;
        this.events = events;
    }

}
