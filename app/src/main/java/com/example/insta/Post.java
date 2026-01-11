package com.example.insta;

import java.util.ArrayList;
import java.util.List;

public class Post {
    private String postId;          // unique ID for the post
    private String username;
    private String profileImage;    // profile picture of the poster
    private String caption;

    private String postImage;       // URL for photo
    private String postVideo;       // URL for video (null if photo)
    private String videoThumbnail;  // thumbnail for video

    private int likes;
    private boolean isLiked = false; // whether current user liked the post

    private List<Comment> comments = new ArrayList<>(); // list of comments
    private long timestamp; // for sorting feed

    // Empty constructor required for Firestore
    public Post() {}

    // Getters
    public String getPostId() { return postId; }
    public String getUsername() { return username; }
    public String getProfileImage() { return profileImage; }
    public String getCaption() { return caption; }
    public String getPostImage() { return postImage; }
    public String getPostVideo() { return postVideo; }
    public String getVideoThumbnail() { return videoThumbnail; }
    public int getLikes() { return likes; }
    public boolean isLiked() { return isLiked; }
    public List<Comment> getComments() { return comments; }
    public long getTimestamp() { return timestamp; }

    // Setters
    public void setPostId(String postId) { this.postId = postId; }
    public void setUsername(String username) { this.username = username; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
    public void setCaption(String caption) { this.caption = caption; }
    public void setPostImage(String postImage) { this.postImage = postImage; }
    public void setPostVideo(String postVideo) { this.postVideo = postVideo; }
    public void setVideoThumbnail(String videoThumbnail) { this.videoThumbnail = videoThumbnail; }
    public void setLikes(int likes) { this.likes = likes; }
    public void setLiked(boolean liked) { this.isLiked = liked; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    // Helper
    public boolean isVideo() {
        return postVideo != null && !postVideo.isEmpty();
    }
}
