package com.example.spotifydating;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CardStackAdapter extends RecyclerView.Adapter<CardStackAdapter.ViewHolder> {

    private List<SongItem> items;

    public CardStackAdapter(List<SongItem> items) {
        this.items = items;
    }

    // Funktion, som køres når kortets view skal laves.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        // Vi danner vores view ud fra song_card (xml layoutet med den generelle struktur).
        View view = inflater.inflate(R.layout.song_card, parent, false);

        // Nu giver vi viewet til vores viewholder, som fylder dataen ud.
        return new ViewHolder(view);
    }

    // Funktion, der køres når viewet (albumbilledet) kommer ind på skærmen.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // Brug setData funktionen til at udfylde dataen i viewet (sæt billedet).
        holder.setData(items.get(position));

        // Vi laver et fade ind på 1,5 sekunder.
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(1500);

        // Vi tilføjer animationen til vores view.
        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(fadeIn);
        holder.itemView.startAnimation(fadeIn);
    }

    // Viewholderen udfylder data i vores view.
    class ViewHolder extends RecyclerView.ViewHolder {

        // Billedet til albumkortet.
        private ImageView albumImage;

        public ViewHolder(View view) {
            super(view);

            // Vi hender albummets view fra resource ids.
            albumImage = (ImageView) view.findViewById(R.id.album_image);
        }

        public void setData(SongItem data) {

            // Sæt billedet med bitmap field.
            albumImage.setImageBitmap(data.getImage());
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
