package com.mobileapp.myapplication.models;

import androidx.annotation.NonNull;

public class User {
    public String email, username;
    public float rating, ratingCounts;

    public String id;

    public User(String email, String username, float rating) {
        this.email = email;
        this.username = username;
        this.rating = rating;
    }

    @NonNull
    @Override
    public String toString() {
        if(rating!=0){
            return "Username: " + username
                    +"\nEmail: " + email
                    +"\nRating: " + String.format("%.1f", rating/ratingCounts);
        }else{
            return "Username: " + username
                    +"\nEmail: " + email
                    +"\nRating: 0";
        }

    }

    public User() {
    }
}
