package com.example.insta;

public class Story {
    private String username;
    private String profileImage;

    // Firestore requires public no-arg constructor
    public Story() {}

    public Story(String username, String profileImage) {
        this.username = username;
        this.profileImage = profileImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
