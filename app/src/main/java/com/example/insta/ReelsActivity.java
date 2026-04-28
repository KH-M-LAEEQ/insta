package com.example.insta;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReelsActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private ReelsAdapter adapter;
    private LinearLayoutManager layoutManager;
    private BottomNavigationView bottomNavigationView;

    private List<String> reelUrls = new ArrayList<>();
    private int currentPage = 1;
    private final String PEXELS_API_KEY = "YOUR_PEXELS_API_KEY_HERE";

    private OkHttpClient client = new OkHttpClient(); // reuse client

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reels);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        recycler = findViewById(R.id.reelsRecycler);

        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recycler.setLayoutManager(layoutManager);

        new PagerSnapHelper().attachToRecyclerView(recycler);

        adapter = new ReelsAdapter(this, reelUrls);
        recycler.setAdapter(adapter);

        setupBottomNav();
        fetchPexelsVideos(currentPage);

        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView rv, int state) {
                if (state == RecyclerView.SCROLL_STATE_IDLE) {
                    int pos = layoutManager.findFirstCompletelyVisibleItemPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        adapter.playReel(pos);
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                int total = layoutManager.getItemCount();
                int lastVisible = layoutManager.findLastVisibleItemPosition();
                if (lastVisible >= total - 2) {
                    fetchPexelsVideos(++currentPage);
                }
            }
        });
    }

    private void fetchPexelsVideos(int page) {
        Request request = new Request.Builder()
                .url("https://api.pexels.com/videos/popular?per_page=10&page=" + page)
                .addHeader("Authorization", PEXELS_API_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    try {
                        JSONObject obj = new JSONObject(json);
                        JSONArray arr = obj.getJSONArray("videos");
                        if (arr.length() == 0) return;

                        List<String> urls = new ArrayList<>();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject video = arr.getJSONObject(i);
                            JSONArray videoFiles = video.getJSONArray("video_files");
                            for (int j = 0; j < videoFiles.length(); j++) {
                                JSONObject file = videoFiles.getJSONObject(j);
                                if (file.getString("file_type").equals("video/mp4")) {
                                    urls.add(file.getString("link"));
                                    break;
                                }
                            }
                        }

                        runOnUiThread(() -> {
                            int prevSize = reelUrls.size();
                            reelUrls.addAll(urls);
                            adapter.notifyItemRangeInserted(prevSize, urls.size());

                            if (prevSize == 0 && reelUrls.size() > 0) {
                                adapter.playReel(0);
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void setupBottomNav() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                return true;
            } else if (id == R.id.nav_search) {
                // Optional: implement refreshFeed() or remove
                return true;
            } else if (id == R.id.nav_add) {
                startActivity(new Intent(this, AddPostActivity.class));
                return true;
            } else if (id == R.id.nav_reels) {
                return true; // already here
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, AccountActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.releaseAll();
    }
}
