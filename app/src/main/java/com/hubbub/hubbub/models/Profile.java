package com.hubbub.hubbub.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Profile {

    public String handle;
    public String name;
    public String photo;

    public Profile() {
        // Default constructor required for calls to DataSnapshot.getValue(Profile.class)
    }

    public Profile(String handle, String name, String photo) {
        this.name = name;
        this.handle = handle;
        this.photo = photo;
    }

}
