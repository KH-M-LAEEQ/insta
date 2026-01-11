package com.example.insta;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import java.util.List;

public class FullScreenMediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<ExploreItem> mediaList;

    public FullScreenMediaAdapter(Context context, List<ExploreItem> mediaList) {
        this.context = context;
        this.mediaList = mediaList;
    }

    @Override
    public int getItemViewType(int position) {
        return mediaList.get(position).isVideo() ? 1 : 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(context).inflate(R.layout.fullscreen_video_item, parent, false);
            return new VideoViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.fullscreen_image_item, parent, false);
            return new ImageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ExploreItem item = mediaList.get(position);

        if (holder instanceof ImageViewHolder) {
            Glide.with(context)
                    .load(item.getThumbnailUrl())
                    .into(((ImageViewHolder) holder).imageView);
        } else if (holder instanceof VideoViewHolder) {
            VideoViewHolder videoHolder = (VideoViewHolder) holder;
            ExoPlayer player = new ExoPlayer.Builder(context).build();
            videoHolder.playerView.setPlayer(player);
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(item.getVideoUrl()));
            player.setMediaItem(mediaItem);
            player.prepare();
            player.setRepeatMode(ExoPlayer.REPEAT_MODE_ONE); // loop video
            player.play();
        }
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.fullscreenImage);
        }
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        PlayerView playerView;
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            playerView = itemView.findViewById(R.id.fullscreenVideo);
        }
    }
}
