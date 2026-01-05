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

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    private Context context;
    private ArrayList<Story> stories = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public StoryAdapter(Context context) {
        this.context = context;
        loadStories();
    }

    // Load stories from Firestore
    private void loadStories() {
        db.collection("stories").addSnapshotListener((value, error) -> {
            if (error != null) {
                error.printStackTrace();
                return;
            }

            if (value != null) {
                stories.clear();
                for (QueryDocumentSnapshot doc : value) {
                    Story story = doc.toObject(Story.class);
                    stories.add(story);
                }
                notifyDataSetChanged();
            }
        });
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.story_item, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        Story story = stories.get(position);
        holder.tvStoryName.setText(story.getUsername());

        // Load profile image with circular crop
        Glide.with(context)
                .load(story.getProfileImage())
                .placeholder(R.drawable.skeleton_circle) // optional placeholder
                .circleCrop()
                .into(holder.imgStory);
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
