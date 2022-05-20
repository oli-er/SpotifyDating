package com.example.spotifydating;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CardStackAdapter extends RecyclerView.Adapter<CardStackAdapter.ViewHolder> {

    private List<SongItem> items;

    public CardStackAdapter(List<SongItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        // Vi danner vores view ud fra song_card (xml layoutet med den generelle struktur).
        View view = inflater.inflate(R.layout.song_card, parent, false);

        // Nu giver vi viewet til vores viewholder, som fylder dataen ud.
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(items.get(position));
    }

    // Viewholderen udfylder data i vores view.
    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView albumImage;
        private TextView songNameTV, artistNameTV, albumNameTV;
        public ViewHolder(View view) {
            super(view);

            songNameTV = (TextView) view.findViewById(R.id.song_name);
            artistNameTV = (TextView) view.findViewById(R.id.artist_name);
            albumNameTV = (TextView) view.findViewById(R.id.album_name);

            albumImage = (ImageView) view.findViewById(R.id.album_image);

            // Animation
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
            fadeIn.setDuration(1500);

            AnimationSet animation = new AnimationSet(false);
            animation.addAnimation(fadeIn);
            view.startAnimation(fadeIn);
        }

        public void setData(SongItem data) {
            // Load the album picture.
            albumImage.setImageBitmap(data.getImage());
            /*
            Picasso.get()
                    .load(data.getImage())
                    .fit()
                    .centerCrop()
                    .into(albumImage);
            */

            // Set textviews.
            songNameTV.setText(data.getSong());
            artistNameTV.setText(data.getArtist());
            albumNameTV.setText(data.getAlbum());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public List<SongItem> getItems() {
        return items;
    }

    public void setItems(List<SongItem> items) {
        this.items = items;
    }
}
