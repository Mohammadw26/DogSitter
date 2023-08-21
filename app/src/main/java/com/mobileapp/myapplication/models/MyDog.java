package com.mobileapp.myapplication.models;

public class MyDog {
    public String ownerId, dogName, dogDetails, reminders;

    public MyDog(String ownerId, String dogName, String dogDetails, String reminders) {
        this.ownerId = ownerId;
        this.dogName = dogName;
        this.dogDetails = dogDetails;
        this.reminders = reminders;
    }

    public MyDog() {
    }

    public String getInfo(){
        return "\nDog name: " + dogName
                +"\nDetails: " + dogDetails
                +"\nReminders: " + reminders;
    }
}
