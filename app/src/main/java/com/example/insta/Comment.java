package com.example.insta;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Comment {
    private String commentId;
    private String username; // or userId if you prefer
    private String text;

    @ServerTimestamp
    private Date timestamp; // Firestore-friendly

    // No-arg constructor is required for Firestore
    public Comment() {}

    public Comment(String commentId, String username, String text) {
        this.commentId = commentId;
        this.username = username;
        this.text = text;
        // timestamp will be set by Firestore automatically
    }

    public String getCommentId() { return commentId; }
    public void setCommentId(String commentId) { this.commentId = commentId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}
