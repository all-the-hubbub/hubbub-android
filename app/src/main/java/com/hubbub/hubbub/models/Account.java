package com.hubbub.hubbub.models;

import java.util.HashMap;

/**
 * Created by sgoldblatt on 5/1/17.
 */

public class Account {
    public String email;
    public String githubToken;
    public long updatedAt;
    public HashMap<String, Event> events;

    public Account(){
        // Default constructor required for calls to DataSnapshot.getValue(Account.class)
    };

    public Account(String email, String githubToken, int updatedAt, HashMap<String, Event> events) {
        this.email = email;
        this.githubToken = githubToken;
        this.updatedAt = updatedAt;
        this.events = events;
    }

}
