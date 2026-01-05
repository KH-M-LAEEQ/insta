package com.example.insta;

public class Post {
    private String username;
    private String profileImage;
    private String postImage;
    private String caption;
    private int likes;
    private long timestamp; // for sorting feed

    public Post() {} // required for Firestore

    // Getters
    public String getUsername() { return username; }
    public String getProfileImage() { return profileImage; }
    public String getPostImage() { return postImage; }
    public String getCaption() { return caption; }
    public int getLikes() { return likes; }
    public long getTimestamp() { return timestamp; }

    // Setters
    public void setUsername(String username) { this.username = username; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
    public void setPostImage(String postImage) { this.postImage = postImage; }
    public void setCaption(String caption) { this.caption = caption; }
    public void setLikes(int likes) { this.likes = likes; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
