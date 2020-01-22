package com.example.pestone.conference.Models;


import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class RoomModel {

    private String roomName, roomDescription,
            roomDate, senderUID, reciverUID, senderEmail, reciverEmail, pushKey, Token, Session;

    public RoomModel() {
    }


    public RoomModel(String roomName, String roomDescription,
                     String roomDate, String senderUID, String reciverUID,
                     String senderEmail, String reciverEmail, String pushKey, String Token,String Session) {


        this.roomName = roomName;
        this.roomDescription = roomDescription;
        this.roomDate = roomDate;
        this.senderUID = senderUID;
        this.reciverUID = reciverUID;
        this.senderEmail = senderEmail;
        this.reciverEmail = reciverEmail;
        this.pushKey = pushKey;
        this.Token = Token;
        this.Session = Session;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("roomName", roomName);
        result.put("roomDescription", roomDescription);
        result.put("roomDate", roomDate);
        result.put("senderUID", senderUID);
        result.put("reciverUID", reciverUID);
        result.put("senderEmail", senderEmail);
        result.put("reciverEmail", reciverEmail);
        result.put("pushKey", pushKey);

        return result;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomDescription() {
        return roomDescription;
    }

    public void setRoomDescription(String roomDescription) {
        this.roomDescription = roomDescription;
    }

    public String getRoomDate() {
        return roomDate;
    }

    public void setRoomDate(String roomDate) {
        this.roomDate = roomDate;
    }

    public String getSenderUID() {
        return senderUID;
    }

    public void setSenderUID(String senderUID) {
        this.senderUID = senderUID;
    }

    public String getReciverUID() {
        return reciverUID;
    }

    public void setReciverUID(String reciverUID) {
        this.reciverUID = reciverUID;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getReciverEmail() {
        return reciverEmail;
    }

    public void setReciverEmail(String reciverEmail) {
        this.reciverEmail = reciverEmail;
    }

    public String getPushKey() {
        return pushKey;
    }

    public void setPushKey(String pushKey) {
        this.pushKey = pushKey;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public String getSession() {
        return Session;
    }

    public void setSession(String session) {
        Session = session;
    }
}