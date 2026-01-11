package com.example.insta;

import android.content.Context;
import android.content.Intent;
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

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    private static final int TYPE_PHOTO = 1;
    private static final int TYPE_VIDEO = 2;

    private Context context;
    private ArrayList<Post> posts = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FeedAdapter(Context context) {
        this.context = context;
        loadPosts();
    }

    // Load posts in real-time from Firestore
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

    @Override
    public int getItemViewType(int position) {
        return posts.get(position).isVideo() ? TYPE_VIDEO : TYPE_PHOTO;
    }


    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_PHOTO) {
            view = LayoutInflater.from(context).inflate(R.layout.feed_item_photo, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.feed_item_video, parent, false);
        }
        return new FeedViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.tvUsername.setText(post.getUsername());
        holder.tvCaption.setText(post.getUsername() + " " + post.getCaption());
        holder.tvLikes.setText(post.getLikes() + " likes");
        Glide.with(context).load(post.getProfileImage()).into(holder.imgProfile);

        if(post.isVideo()) {
            // show video thumbnail + play icon
            Glide.with(context).load(post.getVideoThumbnail()).centerCrop().into(holder.imgPost);
            holder.imgPlay.setVisibility(View.VISIBLE);

            holder.imgPost.setOnClickListener(v -> {
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtra("videoUrl", post.getPostVideo());
                context.startActivity(intent);
            });
        } else {
            // normal photo
            Glide.with(context).load(post.getPostImage()).centerCrop().into(holder.imgPost);
            holder.imgPlay.setVisibility(View.GONE);

            holder.imgPost.setOnClickListener(null); // optional
        }
    }


    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class FeedViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProfile, imgPost, imgLike, imgComment, imgShare, imgSave, imgPlay;
        TextView tvUsername, tvLikes, tvCaption, tvLikeCount, tvComments, tvShare;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            imgPost = itemView.findViewById(R.id.imgPost);
            imgPlay = itemView.findViewById(R.id.imgPlay); // new
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            tvCaption = itemView.findViewById(R.id.tvCaption);
            imgLike = itemView.findViewById(R.id.imgLike);
            imgComment = itemView.findViewById(R.id.imgComment);
            imgShare = itemView.findViewById(R.id.imgShare);
            imgSave = itemView.findViewById(R.id.imgSave);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvComments = itemView.findViewById(R.id.tvComments);
            tvShare = itemView.findViewById(R.id.tvShare);
        }
    }

}
