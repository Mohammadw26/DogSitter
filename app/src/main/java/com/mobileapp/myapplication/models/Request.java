package com.mobileapp.myapplication.models;

import java.util.ArrayList;
import java.util.List;

public class Request {
    public String requesterName, requesterId, ownerId, serviceId, fromDate, toDate, details, status, requestId, allRequests;

    public List<String> reminders = new ArrayList<>();
    public Request(String requesterName, String requestId, String requesterId, String ownerId, String serviceId, String fromDate, String toDate, String details, String status,
                   String allRequests, List<String> reminders) {
        this.requesterName = requesterName;
        this.requestId = requestId;
        this.requesterId = requesterId;
        this.ownerId = ownerId;
        this.serviceId = serviceId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.details = details;
        this.status = status;
        this.allRequests = allRequests;
        this.reminders = reminders;
    }

    public Request() {
    }

    public String getDetailInfo(){
        String detailsString = "";
        if(requesterName!="")
            detailsString+="Requester Name: " + requesterName;
        if(details!="")
            detailsString+="\n\nDetails: " + details;
        if(allRequests!="")
            detailsString+="\n\nAll Requests\n" + allRequests;
        return detailsString;
    }
}
