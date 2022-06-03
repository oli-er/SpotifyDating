package com.example.spotifydating;

import android.graphics.Bitmap;

// Objekt, der representerer en sang.
public class SongItem {

    private Bitmap image;
    private String song, artist, album, id;

    public SongItem(Bitmap image, String song, String artist, String album, String id) {
        this.image = image;
        this.song = song;
        this.artist = artist;
        this.album = album;
        this.id = id;
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

    public String getId() {
        return id;
    }
}
