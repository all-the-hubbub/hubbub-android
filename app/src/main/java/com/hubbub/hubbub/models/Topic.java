package com.hubbub.hubbub.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by sgoldblatt on 5/14/17.
 */

@IgnoreExtraProperties
public class Topic {
    public String name;
    public String id;

    public Topic(){
        // Default constructor required for calls to DataSnapshot.getValue(Topic.class)
    }

    public Topic(String name, String id) {
        this.name = name;
        this.id = id;
    }
}
