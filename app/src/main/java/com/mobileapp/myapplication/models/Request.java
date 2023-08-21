package com.mobileapp.myapplication.models;

public class Request {
    public String requesterId, ownerId, serviceId, fromDate, toDate, details, status, requestId, vaccinationName, vaccinationTime, foodName, foodTime;

    public Request(String requestId, String requesterId, String ownerId, String serviceId, String fromDate, String toDate, String details, String status,
                   String vaccinationName,String  vaccinationTime,String  foodName,String  foodTime) {
        this.requestId = requestId;
        this.requesterId = requesterId;
        this.ownerId = ownerId;
        this.serviceId = serviceId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.details = details;
        this.status = status;
        this.vaccinationName = vaccinationName;
        this.vaccinationTime = vaccinationTime;
        this.foodName = foodName;
        this.foodTime = foodTime;
    }

    public Request() {
    }

    public String getDetailInfo(){
        String detailsString = "";
        if(vaccinationName!="")
            detailsString+="\nVaccination Name: " + vaccinationName;
        if(vaccinationTime!="")
            detailsString+="\nVaccination Time: " + vaccinationTime;
        if(foodName!="")
            detailsString+="\n\nFood Name: " + foodName;
        if(foodTime!="")
            detailsString+="\nFood Time: " + foodTime;
        if(details!="")
            detailsString+="\n\nDetails: " + details;
        return detailsString;
    }
}
