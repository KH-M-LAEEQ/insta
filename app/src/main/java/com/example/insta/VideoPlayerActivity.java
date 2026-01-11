package com.example.insta;

import android.net.Uri;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;


import java.util.ArrayList;

public class VideoPlayerActivity extends AppCompatActivity {

    private PlayerView playerView;
    private ExoPlayer player;
    private ImageView btnClose;

    private ArrayList<String> videoUrls;
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        playerView = findViewById(R.id.playerView);
        btnClose = findViewById(R.id.btnClose);

        btnClose.setOnClickListener(v -> finish());

        // Get video playlist from Intent
        videoUrls = getIntent().getStringArrayListExtra("videoUrls");
        if (videoUrls == null || videoUrls.isEmpty()) {
            finish();
            return;
        }

        initPlayer();
    }


    private void playVideo(int index) {
        MediaItem item = MediaItem.fromUri(Uri.parse(videoUrls.get(index)));
        player.setMediaItem(item);
        player.prepare();
        player.play();
    }

    @OptIn(markerClass = UnstableApi.class)
    private void initPlayer() {
        // Build Media3 ExoPlayer
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        // Hide controller completely (like Instagram reels)
        playerView.setControllerShowTimeoutMs(0);
        playerView.hideController();

        // Play first video
        playVideo(currentIndex);

        // Listen for end of playback to loop playlist
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_ENDED) {
                    currentIndex++;
                    if (currentIndex >= videoUrls.size()) currentIndex = 0; // loop playlist
                    playVideo(currentIndex);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) player.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) player.play();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
