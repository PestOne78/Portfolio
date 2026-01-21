package com.example.lenmiriotmessage.Models;

public class MessageModel {
    private String TextMess;
    private String Sid;
    private String Sname;
    private String Resid;
    private String TimeMess;

    public MessageModel() {
    }

    public MessageModel(String TextMess, String Sid, String Sname,
                        String Resid, String TimeMess) {
        this.TextMess = TextMess;
        this.Sid = Sid;
        this.Sname = Sname;
        this.Resid = Resid;
        this.TimeMess = TimeMess;
    }

    public String getTextMess() {
        return TextMess;
    }

    public void setTextMess(String textMess) {
        TextMess = textMess;
    }

    public String getSid() {
        return Sid;
    }

    public void setSid(String sid) {
        Sid = sid;
    }

    public String getSname() {
        return Sname;
    }

    public void setSname(String sname) {
        Sname = sname;
    }

    public String getResid() {
        return Resid;
    }

    public void setResid(String rid) {
        Resid = rid;
    }

    public String getTimeMess() {
        return TimeMess;
    }

    public void setTimeMess(String timeMess) {
        TimeMess = timeMess;
    }
}
