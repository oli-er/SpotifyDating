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

enum EndPoint {
    PLAYLIST("https://api.spotify.com/v1/me/playlists"),
    SPECIFIC_PLAYLIST("https://api.spotify.com/v1/me/playlists/%s"),
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

interface VolleyCallBack<T> {
    void onSuccess(T data);
}

public class SpotifyAPIHelper {

    private static final String CLIENT_ID = "f9a4127598ed4dd4aed26e20f8d30374";
    private static final String REDIRECT_URI = "com.example.spotifydating://callback";

    private SharedPreferences sharedPreferences;
    private RequestQueue queue;

    public SpotifyAppRemote mSpotifyAppRemote;
    public Context mcontext;

    public boolean connnected = false;


    public SpotifyAPIHelper(Context context, ConnectionCallback connectionCallback) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
        mcontext = context;

        connectAppRemote(connectionCallback);
    }

    public CallResult<Empty> skipTrack() {
        return mSpotifyAppRemote.getPlayerApi().skipNext();
    }

    public CallResult<Empty> skipToPreviousTrack() {
        return mSpotifyAppRemote.getPlayerApi().skipPrevious();
    }


    public Subscription<PlayerState> subscribeToPlayerState(Subscription.EventCallback<PlayerState> playerStateEventCallback) {
        return (Subscription<PlayerState>) mSpotifyAppRemote
                .getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerStateEventCallback);
    }

    private void connectAppRemote(ConnectionCallback connectionCallback) {

        SpotifyAppRemote.disconnect(mSpotifyAppRemote);

        SpotifyAppRemote.connect(
                mcontext,
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build(),
                new Connector.ConnectionListener() {
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        if (!connnected) {
                            connectionCallback.onConnected();
                            connnected = true;
                        }
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        Log.d("AppRemote connection", error.toString());
                        Toast.makeText(mcontext, "Kunne ikke forbinde.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    interface ConnectionCallback {
        void onConnected();
    }


}
