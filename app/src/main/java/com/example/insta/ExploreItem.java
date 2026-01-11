package com.example.insta;

import java.io.Serializable;

public class ExploreItem implements Serializable {
    private String thumbnailUrl;
    private boolean isVideo;
    private String videoUrl; // only for videos

    public ExploreItem(String thumbnailUrl, boolean isVideo) {
        this.thumbnailUrl = thumbnailUrl;
        this.isVideo = isVideo;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
