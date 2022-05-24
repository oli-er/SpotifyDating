package com.example.spotifydating;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

// Adapter (indholdsmanager) til playlist recycler viewet (listen med sange i playlisten).
public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    // Listen med sange i playlisten.
    private List<SongItem> itemList;

    public PlaylistAdapter(List<SongItem> itemList) {
        this.itemList = itemList;
    }

    // Funktion, som køres når sangens view skal laves.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        // Inflate (dan) vores sangs viewtemplate.
        View view = inflater.inflate(R.layout.song_playlist_card, parent, false);

        return new ViewHolder(view);
    }

    // Funktion, der køres når viewet vises i recyclerviewet.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // Udfyld data med setdata funktionen.
        holder.setData(itemList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // Vores viewholder til sangen.
    class ViewHolder extends RecyclerView.ViewHolder {

        // Views
        private ImageView albumImage;
        private TextView songNameTV, artistNameTV, albumNameTV;

        public ViewHolder(View view) {
            super(view);

            // Sæt views med resource id'erne
            albumImage = (ImageView) view.findViewById(R.id.playlist_song_image);
            songNameTV = view.findViewById(R.id.playlist_song_name);
            artistNameTV = view.findViewById(R.id.playlist_song_artist);
        }

        // Funktion, der udfylder dataen i viewet.
        public void setData(SongItem data) {

            // Sæt sangens navn, kunstners navn og billede.
            songNameTV.setText(data.getSong());
            artistNameTV.setText(data.getArtist());
            albumImage.setImageBitmap(data.getImage());
        }
    }

    public void setItemList(List<SongItem> itemList) {
        this.itemList = itemList;
    }
}
