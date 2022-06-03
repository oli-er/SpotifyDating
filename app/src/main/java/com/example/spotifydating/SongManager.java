package com.example.spotifydating;

import java.util.ArrayList;
import java.util.List;

// Manager til brugerens swipede sange.
public class SongManager {

    private List<SongItem> playlistSongs;
    private SongsCallBack songsCallBack;

    public SongManager() {
        this.playlistSongs = new ArrayList<>();
    }

    // Setter til callback.
    public void setSongsCallBack(SongsCallBack songsCallBack) {
        this.songsCallBack = songsCallBack;
        songsChanged();
    }

    public void addSong(SongItem track) {
        playlistSongs.add(track);
        songsChanged();
    }

    public void clearSongs() {
        playlistSongs.clear();
        songsChanged();
    }

    // Funktion, som køres når sangene er blevet redigeret.
    private void songsChanged() {

        // Hvis callback er sat.
        if (songsCallBack != null) {

            // Kør callback funktion.
            songsCallBack.onSongsChanged(playlistSongs);
        }
    }


    public List<SongItem> getSongs() {
        return playlistSongs;
    }

    public interface SongsCallBack {
        void onSongsChanged(List<SongItem> songs);
    }
}
