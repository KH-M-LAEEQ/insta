package com.example.insta;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private static final String API_KEY = "IRod3n1KsMFCb57g0um9OVhIXreezqDHl2UhZV7qe3tQPtRZOIivDzaK";

    private RecyclerView recyclerView;
    private EditText searchEditText;
    private BottomNavigationView bottomNavigationView;

    private RequestQueue queue;
    private List<ExploreItem> exploreList = new ArrayList<>();
    private ExploreAdapter exploreAdapter;

    private GridLayoutManager layoutManager;

    private int imagePage = 1;
    private int videoPage = 1;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        recyclerView = findViewById(R.id.searchRecyclerView);
        searchEditText = findViewById(R.id.searchEditText);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        queue = Volley.newRequestQueue(this);

        // Setup RecyclerView
        exploreAdapter = new ExploreAdapter(this, exploreList);
        layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(exploreAdapter);

        // Bottom nav
        setupBottomNav();

        // Infinite scroll
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                super.onScrolled(rv, dx, dy);
                int total = layoutManager.getItemCount();
                int lastVisible = layoutManager.findLastVisibleItemPosition();

                if (!isLoading && lastVisible >= total - 5) {
                    loadNextPage();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Always refresh feed when coming to foreground
        refreshFeed();
    }

    private void refreshFeed() {
        exploreList.clear();
        exploreAdapter.notifyDataSetChanged();
        imagePage = 1;
        videoPage = 1;
        isLoading = false;

        loadImages(imagePage);
        loadVideos(videoPage);
    }

    private void setupBottomNav() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                return true;
            } else if (id == R.id.nav_search) {
                // Already on search, refresh feed
                refreshFeed();
                return true;
            } else if (id == R.id.nav_add) {
                startActivity(new Intent(this, AddPostActivity.class));
                return true;
            } else if (id == R.id.nav_reels) {
                startActivity(new Intent(this, ReelsActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, activity_account.class));
                return true;
            }
            return false;
        });
    }

    private void loadNextPage() {
        if (isLoading) return;
        isLoading = true;
        loadImages(imagePage + 1);
        loadVideos(videoPage + 1);
    }

    private void loadImages(int page) {
        String url = "https://api.pexels.com/v1/curated?per_page=30&page=" + page;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray arr = response.getJSONArray("photos");
                        for (int i = 0; i < arr.length(); i++) {
                            String imgUrl = arr.getJSONObject(i).getJSONObject("src").getString("medium");
                            exploreList.add(new ExploreItem(imgUrl, false));
                        }
                        imagePage = page;
                        exploreAdapter.notifyDataSetChanged();
                        isLoading = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                        isLoading = false;
                    }
                },
                error -> isLoading = false
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> headers = new HashMap<>();
                headers.put("Authorization", API_KEY);
                return headers;
            }
        };
        queue.add(request);
    }

    private void loadVideos(int page) {
        String url = "https://api.pexels.com/videos/popular?per_page=20&page=" + page;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray arr = response.getJSONArray("videos");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONArray files = arr.getJSONObject(i).getJSONArray("video_files");
                            String videoUrl = files.getJSONObject(0).getString("link");
                            String thumb = arr.getJSONObject(i).getString("image");

                            ExploreItem item = new ExploreItem(thumb, true);
                            item.setVideoUrl(videoUrl);
                            exploreList.add(item);
                        }
                        videoPage = page;
                        exploreAdapter.notifyDataSetChanged();
                        isLoading = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                        isLoading = false;
                    }
                },
                error -> isLoading = false
        ) {
            @Override
            public Map<String,String> getHeaders() {
                Map<String,String> headers = new HashMap<>();
                headers.put("Authorization", API_KEY);
                return headers;
            }
        };
        queue.add(request);
    }

    // ---------------- Adapter ----------------
    public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.ExploreViewHolder> {
        private Context context;
        private List<ExploreItem> exploreList;

        public ExploreAdapter(Context context, List<ExploreItem> exploreList) {
            this.context = context;
            this.exploreList = exploreList;
        }

        public class ExploreViewHolder extends RecyclerView.ViewHolder {
            ImageView imgPlay, imgThumbnail;
            public ExploreViewHolder(@NonNull android.view.View itemView) {
                super(itemView);
                imgPlay = itemView.findViewById(R.id.imgPlay);
                imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
            }
        }

        @NonNull
        @Override
        public ExploreViewHolder onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
            android.view.View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.explore_item, parent, false);
            return new ExploreViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ExploreViewHolder holder, int position) {
            ExploreItem item = exploreList.get(position);

            Glide.with(context)
                    .load(item.getThumbnailUrl())
                    .centerCrop()
                    .into(holder.imgThumbnail);

            holder.imgPlay.setVisibility(item.isVideo() ? android.view.View.VISIBLE : android.view.View.GONE);

            holder.itemView.setOnClickListener(v -> {
                ArrayList<ExploreItem> mediaList = new ArrayList<>(exploreList);
                Intent intent = new Intent(context, FullScreenMediaActivity.class);
                intent.putExtra("mediaList", mediaList);
                intent.putExtra("startIndex", position);
                context.startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return exploreList.size();
        }
    }
}
