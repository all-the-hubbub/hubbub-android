package com.hubbub.hubbub.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Profile {

    public String handle;
    public String name;

    public Profile() {
        // Default constructor required for calls to DataSnapshot.getValue(Profile.class)
    }

    public Profile(String handle, String name) {
        this.name = name;
        this.handle = handle;
    }

}
