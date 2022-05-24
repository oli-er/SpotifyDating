package com.example.spotifydating;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Empty;
import com.spotify.protocol.types.PlayerState;
import com.android.volley.Response.Listener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Enum til de forskellige anvendte endpoints i spotify API'en
enum EndPoint {
    PLAYLIST("https://api.spotify.com/v1/me/playlists"),
    PLAYLISTME("https://api.spotify.com/v1/playlists/%s/tracks"),
    ;

    private final String endpoint;

    EndPoint(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public String toString(){
        return  endpoint;
    }
}

// Callback til en web request.
interface RequestCallBack<T> {

    // Funktion, der køres når requesten lykkedes.
    void onSuccess(T data);
}

// Hjælperclass til spotifys remote applikation API.
// Hjælper med interaktion med spotify appen.
public class SpotifyRemoteHelper {

    // Klient id til spotify projektet og callback uri.
    private static final String CLIENT_ID = "f9a4127598ed4dd4aed26e20f8d30374";
    private static final String REDIRECT_URI = "com.example.spotifydating://callback";

    // Spotify remote objektet (bruges til at interegere med spotify appen).
    public SpotifyAppRemote mSpotifyAppRemote;

    public Context mcontext;
    public boolean connnected = false;


    public SpotifyRemoteHelper(Context context, ConnectionCallback connectionCallback) {
        // Gem context.
        mcontext = context;

        // Forbind appremote objektet.
        connectAppRemote(connectionCallback);
    }

    // Funktion, som skipper nuværende sang.
    public CallResult<Empty> skipTrack() {
        return mSpotifyAppRemote.getPlayerApi().skipNext();
    }

    // Funktion, som går til forrige sang.
    public CallResult<Empty> skipToPreviousTrack() {
        return mSpotifyAppRemote.getPlayerApi().skipPrevious();
    }

    // Laver en subscription til playerstaten af spotify appen.
    // Subscriptionen opdateres f.eks. hvis sangen ændres.
    public Subscription<PlayerState> subscribeToPlayerState(Subscription.EventCallback<PlayerState> playerStateEventCallback) {

        // mpotifyAppRemoten til at danne en subscription og return den.
        return (Subscription<PlayerState>) mSpotifyAppRemote
                .getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerStateEventCallback);
    }

    // Forbind SpotifyRemote klassen.
    private void connectAppRemote(ConnectionCallback connectionCallback) {

        SpotifyAppRemote.disconnect(mSpotifyAppRemote);

        SpotifyAppRemote.connect(
                mcontext,
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true) // Vis en autoriseringsskærm.
                        .build(),
                new Connector.ConnectionListener() {
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote; // Gem det forbundede objekt.
                        if (!connnected) {
                            connectionCallback.onConnected(); // Kør callback funktion.
                            connnected = true;
                        }
                    }

                    // Hvis der ikke kunne forbindes.
                    @Override
                    public void onFailure(Throwable error) {
                        // Giv brugeren besked
                        Toast.makeText(mcontext, "Kunne ikke forbinde til spotify.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Callback funktion til når en forbindelse er lavet.
    interface ConnectionCallback {
        void onConnected();
    }
}
