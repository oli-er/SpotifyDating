package com.example.spotifydating;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Trace;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Artist;
import com.spotify.protocol.types.Track;

import java.util.ArrayList;
import java.util.List;

public class PlaylistFragment extends Fragment {

    private RecyclerView recyclerView;
    private PlaylistAdapter recyclerAdapter;
    private SpotifyPlaylistHelper spotifyPlaylistHelper;
    private SpotifyAPIHelper spotifyAPIHelper;
    private MainActivity.SongManager songManager;


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

        return view;
    }

    public void onSpotifyConnected(SpotifyAPIHelper spotifyAPIHelper) {
        this.spotifyAPIHelper = spotifyAPIHelper;
    }


    private void loadSongs() {

        songManager.setSongsCallBack(new SongsChangedHandler());
        /*
        spotifyPlaylistHelper.get("2njXG7LJ8fIe25eiCy1CST", new VolleyCallBack<List<Track>>() {
            @Override
            public void onSuccess(List<Track> data) {
                songItems.clear();
                for (Track track : data) {
                    String artist = "";
                    if (track.artist == null) {
                        for (Artist artist1 : track.artists) {
                            artist += artist1.name + ", ";
                        }
                        artist = artist.substring(0, artist.length() - 2);
                    } else {
                        artist = track.artist.name;
                    }
                    songItems.add(new PlaylistSong(track.uri, track.name, artist, track.album.name));
                }

                recyclerAdapter.notifyDataSetChanged();
            }
        });
        */
    }

    private class SongsChangedHandler implements MainActivity.SongsCallBack {

        @Override
        public void onSongsChanged(List<SongItem> songs) {
            recyclerAdapter.setItemList(songs);
            recyclerAdapter.notifyDataSetChanged();
        }
    }
}