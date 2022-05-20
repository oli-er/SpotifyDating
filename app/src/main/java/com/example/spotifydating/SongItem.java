package com.example.spotifydating;

import android.graphics.Bitmap;

public class SongItem {
    private Bitmap image;
    private String song, artist, album;

    public SongItem(Bitmap image, String song, String artist, String album) {
        this.image = image;
        this.song = song;
        this.artist = artist;
        this.album = album;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getSong() {
        return song;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }
}
