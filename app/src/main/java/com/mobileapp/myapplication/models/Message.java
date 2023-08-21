package com.mobileapp.myapplication.models;

public class Message {
    public String msg, timestamp, senderId, receiverId;
    public boolean isImg;

    public Message(String msg, String timestamp, String senderId, String receiverId, boolean isImg) {
        this.msg = msg;
        this.timestamp = timestamp;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.isImg = isImg;
    }

    public Message() {
    }
}
