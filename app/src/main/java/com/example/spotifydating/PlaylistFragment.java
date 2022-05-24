package com.example.spotifydating;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Trace;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Artist;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.Track;

import java.util.ArrayList;
import java.util.List;

public class PlaylistFragment extends Fragment {

    private RecyclerView recyclerView;
    private PlaylistAdapter recyclerAdapter;
    private SpotifyPlaylistHelper spotifyPlaylistHelper;
    private MainActivity.SongManager songManager;
    private ImageButton btnAdd, btnRemove;

    public PlaylistFragment() {
        // Required empty public constructor
    }

    public PlaylistFragment(MainActivity.SongManager songManager) {
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

    private void addSongsToPlaylist() {

        List<String> songIDs = new ArrayList<>();

        List<SongItem> songItems = songManager.getSongs();
        for (SongItem songItem: songItems) {
            songIDs.add(songItem.getId());
        }

        spotifyPlaylistHelper.makePlaylist(
                "SwipeMix",
                "Mix genenereret ved at bruge swipe featuren.",
                false, new VolleyCallBack<String>() {
                    @Override
                    public void onSuccess(String data) {
                        spotifyPlaylistHelper.addSongsToPlaylist(data, songIDs);
                        Toast.makeText(getContext(), "Lavede en playlist.", Toast.LENGTH_SHORT);
                        songManager.clearSongs();
                    }
                });
    }

    private void loadSongs() {

        songManager.setSongsCallBack(new SongsChangedHandler());
    }

    private class SongsChangedHandler implements MainActivity.SongsCallBack {

        @Override
        public void onSongsChanged(List<SongItem> songs) {
            recyclerAdapter.setItemList(songs);
            recyclerAdapter.notifyDataSetChanged();
        }
    }
}