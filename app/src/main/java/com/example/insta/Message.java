package com.example.insta;

public class Message implements Comparable<Message> {

    private String senderId;
    private String receiverId;
    private String text;
    private long timestamp;

    // REQUIRED empty constructor for Firebase
    public Message() {}

    public Message(String senderId, String receiverId, String text, long timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.timestamp = timestamp;
    }

    public Message(String senderId, String receiverId, String text) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.timestamp = System.currentTimeMillis();
    }

    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }
    public String getText() { return text; }
    public long getTimestamp() { return timestamp; }

    public void setSenderId(String senderId) { this.senderId = senderId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
    public void setText(String text) { this.text = text; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    @Override
    public int compareTo(Message other) {
        return Long.compare(this.timestamp, other.timestamp);
    }
}
