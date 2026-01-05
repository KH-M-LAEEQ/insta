package com.example.insta;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ShimmerAdapter adapter;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        searchEditText = findViewById(R.id.searchEditText);
        recyclerView = findViewById(R.id.searchRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        // show shimmer placeholders
        adapter = new ShimmerAdapter();
        recyclerView.setAdapter(adapter);

        // Cancel button
        TextView cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEditText.getText().clear();
            }
        });

        // optional: text watcher
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
    }

    // ------------------- Shimmer Adapter -------------------
    public static class ShimmerAdapter extends Adapter<ShimmerAdapter.ShimmerViewHolder> {

        private int shimmerItemCount = 30; // number of shimmer squares

        public static class ShimmerViewHolder extends ViewHolder {
            ShimmerFrameLayout shimmer;
            public ShimmerViewHolder(View itemView) {
                super(itemView);
                shimmer = itemView.findViewById(R.id.shimmerLayout);
                shimmer.startShimmer();
            }
        }

        @NonNull
        @Override
        public ShimmerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.grid_item_shimmer, parent, false);
            return new ShimmerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ShimmerViewHolder holder, int position) {
            // nothing to bind, shimmer animates automatically
        }

        @Override
        public int getItemCount() {
            return shimmerItemCount;
        }
    }
}
