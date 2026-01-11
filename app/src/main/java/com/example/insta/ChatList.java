package com.example.insta;

public class ChatList {

    private String otherUserId;
    private String lastMessage;
    private long timestamp;

    // REQUIRED for Firebase
    public ChatList() {}

    public ChatList(String otherUserId, String lastMessage, long timestamp) {
        this.otherUserId = otherUserId;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    public String getOtherUserId() {
        return otherUserId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
