package com.example.spotifydating;

import com.spotify.protocol.types.ImageUri;

public class PlaylistSong {

    private String uri;
    private String name;
    private String artist;
    private String album;

    public PlaylistSong(String uri, String name, String artist, String album) {
        this.uri = uri;
        this.name = name;
        this.artist = artist;
        this.album = album;
    }

    public String getUri() {
        return uri;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }
}
