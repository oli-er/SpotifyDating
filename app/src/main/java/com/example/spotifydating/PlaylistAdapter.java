package com.example.spotifydating;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.ImageUri;

import java.io.InputStream;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    List<PlaylistSong> itemList;
    SpotifyAPIHelper spotifyAPIHelper;

    public PlaylistAdapter(List<PlaylistSong> itemList, SpotifyAPIHelper spotifyAPIHelper) {
        this.itemList = itemList;
        this.spotifyAPIHelper = spotifyAPIHelper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.song_playlist_card, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(itemList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView albumImage;
        private TextView songNameTV, artistNameTV, albumNameTV;
        public ViewHolder(View view) {
            super(view);

            albumImage = (ImageView) view.findViewById(R.id.playlist_song_image);
            songNameTV = view.findViewById(R.id.playlist_song_name);
        }

        public void setData(PlaylistSong data) {
            // Load the album picture.

            //albumImage.setImageBitmap(data.getImage());
            songNameTV.setText(data.getName());

            if (spotifyAPIHelper.mSpotifyAppRemote.getImagesApi() == null) {
                return;
            }
            spotifyAPIHelper.mSpotifyAppRemote
                    .getImagesApi()
                    .getImage(new ImageUri(""), Image.Dimension.LARGE)
                    .setResultCallback(new CallResult.ResultCallback<Bitmap>() {
                        @Override
                        public void onResult(Bitmap data) {
                            albumImage.setImageBitmap(data);
                        }
                    });
        }
    }
}
