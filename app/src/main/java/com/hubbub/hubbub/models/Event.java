package com.hubbub.hubbub.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

/**
 * Created by sgoldblatt on 5/1/17.
 */

@IgnoreExtraProperties
public class Event {
    public long endAt;
    public String id;
    public String state;
    public String location;
    public String name;
    public long startAt;
    public HashMap<String, String> topic;


    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    }

}
