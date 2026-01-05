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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    private Context context;
    private ArrayList<Post> posts = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FeedAdapter(Context context) {
        this.context = context;
        loadPosts();
    }

    private void loadPosts() {
        db.collection("posts").addSnapshotListener((value, error) -> {
            if (value != null) {
                posts.clear();
                for (QueryDocumentSnapshot doc : value) {
                    Post post = doc.toObject(Post.class);
                    posts.add(post);
                }
                notifyDataSetChanged();
            }
        });
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.feed_item, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.tvUsername.setText(post.getUsername());
        holder.tvCaption.setText(post.getUsername() + " " + post.getCaption());
        holder.tvLikes.setText(post.getLikes() + " likes");
        Glide.with(context).load(post.getProfileImage()).into(holder.imgProfile);
        Glide.with(context).load(post.getPostImage()).into(holder.imgPost);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class FeedViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProfile, imgPost;
        TextView tvUsername, tvLikes, tvCaption;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            imgPost = itemView.findViewById(R.id.imgPost);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            tvCaption = itemView.findViewById(R.id.tvCaption);
        }
    }
}
