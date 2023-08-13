package com.mobileapp.myapplication.models;

import androidx.annotation.NonNull;

public class User {
    public String email, username;
    public float rating;

    public User(String email, String username, float rating) {
        this.email = email;
        this.username = username;
        this.rating = rating;
    }

    @NonNull
    @Override
    public String toString() {
        return "Username: " + username
                +"\nEmail: " + email
                +"\nRating: " + rating;
    }

    public User() {
    }
}
