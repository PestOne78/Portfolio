package com.example.pestone.conference.Models;

import java.util.Date;

public class MessageModel {

    private String TextOfMessage, SenderId, ReceiverId, SenderName, ReceiverName, MessageTime, pushKey;

    public MessageModel (String TextOfMessage, String SenderId, String ReceiverID, String SenderName,
                         String ReceiverName, String MessageTime, String pushKey){
        this.TextOfMessage = TextOfMessage;
        this.SenderId = SenderId;
        this.ReceiverId = ReceiverID;
        this.SenderName = SenderName;
        this.ReceiverName = ReceiverName;
        this.MessageTime = MessageTime;
        this.pushKey = pushKey;
    }

    public MessageModel(){

    }

    public String getPushKey() {
        return pushKey;
    }

    public void setPushKey(String pushKey) {
        this.pushKey = pushKey;
    }

    public String getTextOfMessage() {
        return TextOfMessage;
    }

    public void setTextOfMessage(String textOfMessage) {
        TextOfMessage = textOfMessage;
    }

    public String getSenderId() {
        return SenderId;
    }

    public void setSenderId(String senderId) {
        SenderId = senderId;
    }

    public String getReceiverId() {
        return ReceiverId;
    }

    public void setReceiverId(String receiverId) {
        ReceiverId = receiverId;
    }

    public String getSenderName() {
        return SenderName;
    }

    public void setSenderName(String senderName) {
        SenderName = senderName;
    }

    public String getReceiverName() {
        return ReceiverName;
    }

    public void setReceiverName(String receiverName) {
        ReceiverName = receiverName;
    }

    public String getMessageTime() {
        return MessageTime;
    }

    public void setMessageTime(String messageTime) {
        MessageTime = messageTime;
    }
}
