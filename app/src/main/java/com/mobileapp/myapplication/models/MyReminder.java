package com.mobileapp.myapplication.models;

import java.util.List;

public class MyReminder {
    public String ownerId, reminderTitle, reminderDetails, reminders;
    public List<String> categoriesList;

    public MyReminder(String ownerId, String reminderTitle, String reminderDetails, String reminders, List<String> categoriesList) {
        this.ownerId = ownerId;
        this.reminderTitle = reminderTitle;
        this.reminderDetails = reminderDetails;
        this.reminders = reminders;
        this.categoriesList = categoriesList;
    }

    public MyReminder() {
    }

    public String getAllReminderDetail(){
        String allReminder = "Title: " + reminderTitle
                +"\nReminder: " + reminderDetails;
        return allReminder;
    }

    public String getInfo(){
        String info = "\nTitle: " + reminderTitle
                +"\nDetails: " + reminderDetails
                +"\nReminders: " + reminders
                +"\n\nCategories" ;

        for (String category: categoriesList) {
            info+="\n"+category;
        }

        return info;
    }
}
