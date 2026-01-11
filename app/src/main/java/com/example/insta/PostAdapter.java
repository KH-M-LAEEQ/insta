package com.example.insta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> posts;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public PostAdapter(List<Post> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feed_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        Context context = holder.itemView.getContext();

        // Set texts
        holder.tvUsername.setText(post.getUsername());
        holder.tvCaption.setText(post.getUsername() + " " + post.getCaption());
        holder.tvLikes.setText(post.getLikes() + " likes");

        // Load post image
        Glide.with(context)
                .load(post.getPostImage())
                .placeholder(R.drawable.skeleton_rect)
                .into(holder.imgPost);

        // Load profile image
        if (post.getProfileImage() != null) {
            Glide.with(context)
                    .load(post.getProfileImage())
                    .placeholder(R.drawable.skeleton_circle)
                    .circleCrop()
                    .into(holder.imgProfile);
        } else {
            holder.imgProfile.setImageResource(R.drawable.skeleton_circle);
        }

        // Set initial like state
        holder.imgLike.setImageResource(post.isLiked() ? R.drawable.ic_like : R.drawable.ic_like);

        // Like button toggle
        holder.imgLike.setOnClickListener(v -> {
            boolean isLiked = post.isLiked();
            post.setLiked(!isLiked);

            // Update like count locally
            int likes = post.getLikes();
            post.setLikes(isLiked ? likes - 1 : likes + 1);
            holder.tvLikes.setText(post.getLikes() + " likes");

            // Animate heart if liked
            if (!isLiked) {
                holder.imgLike.animate().scaleX(1.5f).scaleY(1.5f).setDuration(150)
                        .withEndAction(() -> holder.imgLike.animate().scaleX(1f).scaleY(1f).setDuration(150))
                        .start();
            }

            // Update Firestore
            if (post.getPostId() != null) {
                DocumentReference postRef = db.collection("posts").document(post.getPostId());
                postRef.update("likes", post.getLikes());
            }

            // Update button drawable
            holder.imgLike.setImageResource(post.isLiked() ? R.drawable.ic_like : R.drawable.ic_like);
        });

        // ✅ Comment icon click → open CommentsActivity
        holder.imgComment.setOnClickListener(v -> {
            if (post.getPostId() != null) {
                // Ensure you pass context and postId correctly
                CommentsActivity.open(holder.itemView.getContext(), post.getPostId());
            }
        });


        // Optional: share/save buttons
        holder.imgShare.setOnClickListener(v -> {
            // Share functionality here
        });

        holder.imgSave.setOnClickListener(v -> {
            // Save functionality here
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProfile, imgPost, imgLike, imgComment, imgShare, imgSave;
        TextView tvUsername, tvCaption, tvLikes;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.imgProfile);
            imgPost = itemView.findViewById(R.id.imgPost);
            imgLike = itemView.findViewById(R.id.imgLike);
            imgComment = itemView.findViewById(R.id.imgComment);
            imgShare = itemView.findViewById(R.id.imgShare);
            imgSave = itemView.findViewById(R.id.imgSave);

            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvCaption = itemView.findViewById(R.id.tvCaption);
            tvLikes = itemView.findViewById(R.id.tvLikes);
        }
    }
}
