package com.example.insta;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> comments;
    private Activity activity;
    private String postId;

    public CommentAdapter(Activity activity, List<Comment> comments, String postId) {
        this.activity = activity;
        this.comments = comments;
        this.postId = postId;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comments_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.tvUsername.setText(comment.getUsername());
        holder.tvText.setText(comment.getText());

        // optional: click on comment icon for more actions

    }


    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvText;
        ImageView imgCommentIcon;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvCommentUsername);
            tvText = itemView.findViewById(R.id.tvCommentText);
            imgCommentIcon = itemView.findViewById(R.id.imgComment);
        }
    }
}
