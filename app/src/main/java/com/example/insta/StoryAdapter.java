package com.example.insta;
import com.example.insta.Story;
import com.example.insta.StoryAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    private Context context;
    private List<Story> stories;

    public StoryAdapter(Context context, List<Story> stories) {
        this.context = context;
        this.stories = stories;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.story_item, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        Story story = stories.get(position);
        holder.tvStoryName.setText(story.getUsername());

        Glide.with(context)
                .load(story.getProfileImage())
                .placeholder(R.drawable.skeleton_circle)
                .circleCrop()
                .into(holder.imgStory);

        // Optional: click to open story
        holder.imgStory.setOnClickListener(v -> {
            // TODO: open full story view using story.getStoryImage()
        });
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    static class StoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imgStory;
        TextView tvStoryName;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imgStory = itemView.findViewById(R.id.imgStory);
            tvStoryName = itemView.findViewById(R.id.tvStoryName);
        }
    }
}
