package com.example.insta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

        // Set text
        holder.tvUsername.setText(post.getUsername());
        holder.tvCaption.setText(post.getCaption());
        holder.tvLikes.setText(post.getLikes() + " likes");

        // Load images
        Glide.with(context).load(post.getPostImage()).into(holder.imgPost);
        if (post.getProfileImage() != null && !post.getProfileImage().isEmpty()) {
            Glide.with(context).load(post.getProfileImage()).circleCrop().into(holder.imgProfile);
        }

        // Like button click
        holder.imgLike.setOnClickListener(v -> {
            boolean isLiked = post.isLiked();
            post.setLiked(!isLiked);

            int likes = post.getLikes();
            post.setLikes(isLiked ? likes - 1 : likes + 1);
            holder.tvLikes.setText(post.getLikes() + " likes");

            if (post.getPostId() != null) {
                DocumentReference postRef = db.collection("posts").document(post.getPostId());
                postRef.update("likes", post.getLikes());
            }
        });

        // Comment button click → open CommentsActivity
        holder.imgComment.setOnClickListener(v -> {
            if (post.getPostId() != null) {
                CommentsActivity.open(context, post.getPostId());
            } else {
                Toast.makeText(context, "Post ID not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProfile, imgPost, imgLike, imgComment;
        TextView tvUsername, tvCaption, tvLikes;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            imgPost = itemView.findViewById(R.id.imgPost);
            imgLike = itemView.findViewById(R.id.imgLike);
            imgComment = itemView.findViewById(R.id.imgComment);

            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvCaption = itemView.findViewById(R.id.tvCaption);
            tvLikes = itemView.findViewById(R.id.tvLikes);
        }
    }
}
