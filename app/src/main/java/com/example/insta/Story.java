package com.example.insta;

public class Story {
    private String username;
    private String profileImage; // story circle image
    private String storyImage;   // optional if you want full story view

    public Story() {} // Firestore needs no-arg constructor

    public Story(String username, String profileImage, String storyImage) {
        this.username = username;
        this.profileImage = profileImage;
        this.storyImage = storyImage;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public String getStoryImage() { return storyImage; }
    public void setStoryImage(String storyImage) { this.storyImage = storyImage; }
}
