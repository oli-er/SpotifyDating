package com.example.spotifydating;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PlaylistFragment extends Fragment {

    // Views
    private RecyclerView recyclerView;
    private ImageButton btnAdd, btnRemove;


    private PlaylistAdapter recyclerAdapter;


    private SpotifyPlaylistHelper spotifyPlaylistHelper;
    private SongManager songManager;

    public PlaylistFragment() {
        // Required empty public constructor
    }

    public PlaylistFragment(SongManager songManager) {
        this.songManager = songManager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);



        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerAdapter = new PlaylistAdapter(new ArrayList<SongItem>());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(recyclerAdapter);

        spotifyPlaylistHelper = new SpotifyPlaylistHelper(getContext());
        loadSongs();

        btnAdd = view.findViewById(R.id.btn_add_playlist);
        btnRemove = view.findViewById(R.id.btn_remove_playlist);

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songManager.clearSongs();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSongsToPlaylist();
            }
        });

        return view;
    }

    // Funktion, der tilføjer brugerens swipede sange til
    // en playlist på deres spotifykonto.
    private void addSongsToPlaylist() {

        // Hent sangene fra songmanageren.
        List<SongItem> songItems = songManager.getSongs();

        // Liste, som vi udfylder med spotify id'erne for sangene.
        List<String> songIDs = new ArrayList<>();

        // Loop over sangene.
        for (SongItem songItem: songItems) {
            // Tilføj sangens id til id listen.
            songIDs.add(songItem.getId());
        }

        // Brug spotifyPlaylistHelper klassen til at lave en spotify playlist.
        spotifyPlaylistHelper.makePlaylist(
                "SwipeMix",
                "Mix genenereret ved at bruge swipe featuren.",
                false, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(String data) {

                        // Hvis playlisten blev lavet tilføjer vi sangene med spotifyPlaylistHelperen.
                        spotifyPlaylistHelper.addSongsToPlaylist(data, songIDs);

                        // Giv notifikation til brugeren.
                        Toast.makeText(getContext(), "Lavede en playlist.", Toast.LENGTH_SHORT);

                        // Ryd sangene.
                        songManager.clearSongs();
                    }
                });
    }

    // Funktion, der loader sangene ind i recyclerviewet (listen).
    private void loadSongs() {

        // Load sangene via. songschangedhandleren.
        songManager.setSongsCallBack(new SongsChangedHandler());
    }

    // Handler til når brugerens swipede snage ændres.
    private class SongsChangedHandler implements SongManager.SongsCallBack {

        // Funktion, der køres når sangene ændres.
        @Override
        public void onSongsChanged(List<SongItem> songs) {

            // Sæt listen med sange til den ændrede liste.
            recyclerAdapter.setItemList(songs);

            // Giv listen besked om at dataen er ændres.
            recyclerAdapter.notifyDataSetChanged();
        }
    }
}