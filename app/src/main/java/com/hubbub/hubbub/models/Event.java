package com.hubbub.hubbub.models;

/**
 * Created by sgoldblatt on 5/1/17.
 */

public class Event {
    public long endAt;
    public String id;
    public String state;
    public String location;
    public String name;
    public long startAt;

    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    }

}
