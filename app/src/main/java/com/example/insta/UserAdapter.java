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

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private Context context;
    private List<User> users;

    public UserAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.username.setText(user.getUsername());

        // Load profile pic as circular
        Glide.with(context)
                .load(user.getProfileUrl())
                .circleCrop()  // <- This makes it circular
                .into(holder.profile);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("userId", user.getUid());
            intent.putExtra("username", user.getUsername());
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() { return users.size(); }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        ImageView profile;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.tvUsername);
            profile = itemView.findViewById(R.id.ivProfile);
        }
    }
}
