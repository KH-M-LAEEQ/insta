package com.example.insta;

public class User {
    private String uid;
    private String username;
    private String profileUrl;

    public User() {} // Needed for Firebase

    public User(String uid, String username, String profileUrl) {
        this.uid = uid;
        this.username = username;
        this.profileUrl = profileUrl;
    }

    public String getUid() { return uid; }
    public String getUsername() { return username; }
    public String getProfileUrl() { return profileUrl; }

    public void setUid(String uid) { this.uid = uid; }
    public void setUsername(String username) { this.username = username; }
    public void setProfileUrl(String profileUrl) { this.profileUrl = profileUrl; }
}
