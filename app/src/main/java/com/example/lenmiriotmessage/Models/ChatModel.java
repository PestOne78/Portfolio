package com.example.lenmiriotmessage.Models;


import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class ChatModel {

    private String Mnumber, Mdesc, LastSid, PushKey, Token, Session;
    private boolean IsOnline;

    public ChatModel() {
    }

    public ChatModel(String Mnumber, String Mdesc, String LastSid, boolean IsOnline,
                     String PushKey, String Token, String Session) {

        this.Mnumber = Mnumber;
        this.Mdesc = Mdesc;
        this.LastSid = LastSid;
        this.IsOnline = IsOnline;
        this.PushKey = PushKey;
        this.Token = Token;
        this.Session = Session;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Mnumber", Mnumber);
        result.put("Mdesc", Mdesc);
        result.put("LastSid", LastSid);
        result.put("pushKey", PushKey);

        return result;
    }

    public String getMnumber() {
        return Mnumber;
    }

    public void setMnumber(String Mnumber) {
        this.Mnumber = Mnumber;
    }

    public String getMdesc() {
        return Mdesc;
    }

    public void setMdesc(String Mdesc) {
        this.Mdesc = Mdesc;
    }

    public String getLastSid() {
        return LastSid;
    }

    public void setLastSid(String LastSid) {
        this.LastSid = LastSid;
    }

    public boolean getIsOnline() {
        return IsOnline;
    }

    public void setIsOnline(boolean isOnline) {
        this.IsOnline = isOnline;
    }

    public String getPushKey() {
        return PushKey;
    }

    public void setPushKey(String pushKey) {
        this.PushKey = pushKey;
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