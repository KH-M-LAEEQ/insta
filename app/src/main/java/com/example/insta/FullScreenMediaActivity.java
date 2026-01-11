package com.example.insta;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;

public class FullScreenMediaActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private ArrayList<ExploreItem> mediaList;
    private int startIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_media);

        viewPager = findViewById(R.id.fullscreenViewPager);

        mediaList = (ArrayList<ExploreItem>) getIntent().getSerializableExtra("mediaList");
        startIndex = getIntent().getIntExtra("startIndex", 0);

        FullScreenMediaAdapter adapter = new FullScreenMediaAdapter(this, mediaList);
        viewPager.setAdapter(adapter);

        // Set vertical orientation
        viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);

        // Start from clicked index
        viewPager.setCurrentItem(startIndex, false);
    }
}
