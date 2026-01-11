package com.example.insta;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 100;

    private boolean isFeedLoaded = false;
    private boolean isStoriesLoaded = false;

    private ShimmerFrameLayout shimmerFeed;
    private RecyclerView rvFeed, rvStories;
    private BottomNavigationView bottomNavigationView;

    private static final String PEXELS_API_KEY =
            "IRod3n1KsMFCb57g0um9OVhIXreezqDHl2UhZV7qe3tQPtRZOIivDzaK";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        // Views
        bottomNavigationView = findViewById(R.id.bottomNav);
        shimmerFeed = findViewById(R.id.shimmerFeed);
        rvFeed = findViewById(R.id.rvFeed);
        rvStories = findViewById(R.id.rvStories);

        rvFeed.setLayoutManager(new LinearLayoutManager(this));
        rvStories.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        setupBottomNav();
        checkStoragePermission();

        // DM icon → PeopleActivity
        ImageView ivDm = findViewById(R.id.ivDm);
        ivDm.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, PeopleActivity.class);
            startActivity(intent);
        });

        // 🔥 Chatbot icon → ChatBotActivity
        ImageView icChat = findViewById(R.id.ic_chat);
        icChat.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ChatbotActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isFeedLoaded) fetchPexelsPosts();
        if (!isStoriesLoaded) fetchPexelsStories();
    }

    // ---------------- BOTTOM NAV ----------------

    private void setupBottomNav() {
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) return true;
            if (id == R.id.nav_search)
                startActivity(new Intent(this, SearchActivity.class));
            if (id == R.id.nav_add)
                startActivity(new Intent(this, AddPostActivity.class));
            if (id == R.id.nav_reels)
                startActivity(new Intent(this, ReelsActivity.class));
            if (id == R.id.nav_profile)
                startActivity(new Intent(this, activity_account.class));

            return true;
        });
    }

    // ---------------- PERMISSIONS ----------------

    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        STORAGE_PERMISSION_CODE
                );
            } else {
                startUploadWorker();
            }
        } else {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_CODE
                );
            } else {
                startUploadWorker();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startUploadWorker();
        }
    }

    // ---------------- SHIMMER ----------------

    private void startShimmer() {
        shimmerFeed.setVisibility(View.VISIBLE);
        shimmerFeed.startShimmer();
        rvFeed.setVisibility(View.GONE);
    }

    private void stopShimmer() {
        shimmerFeed.stopShimmer();
        shimmerFeed.setVisibility(View.GONE);
        rvFeed.setVisibility(View.VISIBLE);
    }

    // ---------------- STORIES ----------------

    private void fetchPexelsStories() {
        if (isStoriesLoaded) return;

        new Thread(() -> {
            try {
                URL url = new URL("https://api.pexels.com/v1/curated?per_page=10");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Authorization", PEXELS_API_KEY);

                InputStream in = conn.getInputStream();
                String json = new String(readStream(in));

                JSONObject root = new JSONObject(json);
                JSONArray photos = root.getJSONArray("photos");

                List<Story> stories = new ArrayList<>();
                for (int i = 0; i < photos.length(); i++) {
                    JSONObject src = photos.getJSONObject(i)
                            .getJSONObject("src");

                    Story story = new Story();
                    story.setUsername("user" + (i + 1));
                    story.setProfileImage(src.getString("tiny"));
                    story.setStoryImage(src.getString("large"));
                    stories.add(story);
                }

                runOnUiThread(() -> {
                    rvStories.setAdapter(new StoryAdapter(this, stories));
                    isStoriesLoaded = true;
                });

            } catch (Exception e) {
                Log.e("PEXELS", "Stories error", e);
            }
        }).start();
    }

    // ---------------- FEED ----------------

    private void fetchPexelsPosts() {
        if (isFeedLoaded) return;

        startShimmer();

        new Thread(() -> {
            try {
                int randomPage = new Random().nextInt(50) + 1;

                URL url = new URL(
                        "https://api.pexels.com/v1/curated?per_page=20&page=" + randomPage
                );
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Authorization", PEXELS_API_KEY);

                InputStream in = conn.getInputStream();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buffer = new byte[8192];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }

                JSONObject root = new JSONObject(out.toString());
                JSONArray photos = root.getJSONArray("photos");

                List<Post> posts = new ArrayList<>();
                for (int i = 0; i < photos.length(); i++) {
                    JSONObject src = photos.getJSONObject(i)
                            .getJSONObject("src");

                    Post post = new Post();
                    post.setPostId("pexels_" + randomPage + "_" + i);
                    post.setUsername("pexels_user_" + (i + 1));
                    post.setPostImage(src.getString("large"));
                    post.setCaption("Beautiful photo from Pexels ✨");
                    post.setLikes((int) (Math.random() * 5000));

                    posts.add(post);
                }

                runOnUiThread(() -> {
                    rvFeed.setAdapter(new PostAdapter(posts));
                    stopShimmer();
                    isFeedLoaded = true;
                });

            } catch (Exception e) {
                Log.e("PEXELS", "Feed error", e);
                runOnUiThread(this::stopShimmer);
            }
        }).start();
    }

    // ---------------- WORK MANAGER ----------------

    private void startUploadWorker() {
        OneTimeWorkRequest request =
                new OneTimeWorkRequest.Builder(ImageUploadWorker.class).build();
        WorkManager.getInstance(this).enqueue(request);
    }

    public static class ImageUploadWorker extends Worker {
        public ImageUploadWorker(@NonNull Context context,
                                 @NonNull WorkerParameters params) {
            super(context, params);
        }

        @NonNull
        @Override
        public Result doWork() {
            return Result.success();
        }
    }

    // ---------------- UTILS ----------------

    private byte[] readStream(InputStream in) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int len;
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
        return out.toByteArray();
    }
}
