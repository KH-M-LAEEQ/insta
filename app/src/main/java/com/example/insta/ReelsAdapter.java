package com.example.insta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import androidx.media3.common.Player;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import java.util.List;

public class ReelsAdapter extends RecyclerView.Adapter<ReelsAdapter.ReelHolder> {

    private final List<String> reels;
    private final Context context;
    private ReelHolder currentPlayingHolder = null;

    public ReelsAdapter(Context context, List<String> reels) {
        this.context = context;
        this.reels = reels;
    }

    /** Called by Activity to play a specific reel */
    public void playReel(int pos) {
        // Stop currently playing
        if (currentPlayingHolder != null) {
            currentPlayingHolder.release();
        }

        // Get holder at pos from RecyclerView
        RecyclerView recyclerView = ((ReelsActivity) context).findViewById(R.id.reelsRecycler);
        RecyclerView.ViewHolder vh = recyclerView.findViewHolderForAdapterPosition(pos);

        if (vh instanceof ReelHolder) {
            currentPlayingHolder = (ReelHolder) vh;
            currentPlayingHolder.play(reels.get(pos));
        }
    }

    static class ReelHolder extends RecyclerView.ViewHolder {
        PlayerView playerView;
        ExoPlayer player;

        ReelHolder(View v, Context context) {
            super(v);
            playerView = v.findViewById(R.id.playerView);
            player = new ExoPlayer.Builder(context).build();
            player.setRepeatMode(Player.REPEAT_MODE_ONE); // ✅ fixed
            playerView.setPlayer(player);
        }

        void play(String url) {
            player.setMediaItem(MediaItem.fromUri(url));
            player.prepare();
            player.play();
        }

        void release() {
            if (player != null) {
                player.release();
                player = new ExoPlayer.Builder(itemView.getContext()).build();
                player.setRepeatMode(Player.REPEAT_MODE_ONE); // ✅ fixed
                playerView.setPlayer(player);
            }
        }
    }

    @NonNull
    @Override
    public ReelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reel_item_layout, parent, false);
        return new ReelHolder(v, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ReelHolder holder, int position) {
        // Do not auto-play here; handled by Activity scroll listener
    }

    @Override
    public int getItemCount() {
        return reels.size();
    }

    public void releaseAll() {
        if (currentPlayingHolder != null) {
            currentPlayingHolder.release();
        }
    }
}
