package com.mobileapp.myapplication.models;

public class Service {
    public String title, description, address, contact, startingPrice, endingPrice, latLng, startTime, endTime, startDate, endDate, serviceId, userId;

    public Service(String title, String description, String address, String contact, String startingPrice, String endingPrice, String latLng, String startTime, String endTime, String startDate, String endDate, String serviceId, String userId) {
        this.title = title;
        this.description = description;
        this.address = address;
        this.contact = contact;
        this.startingPrice = startingPrice;
        this.endingPrice = endingPrice;
        this.latLng = latLng;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startDate = startDate;
        this.endDate = endDate;
        this.serviceId = serviceId;
        this.userId = userId;
    }

    public Service() {
    }

    public String getServiceInfo(){
        return "Title: " + title
                +"\nDescription: " + description
                +"\nAddress: " + address;
    }
    public String getContactInfo(){
        return "Phone no: " + contact;
    }


    public String getAvailabilityInfo(){
        return "Dates: " + startDate + " - " + endDate
                +"\nTime: " + startTime + " - " + endTime;
    }
    public String getPricingInfo(){
        return "Price range: " + startingPrice + " - " + endingPrice;
    }
}
