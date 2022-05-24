package com.example.spotifydating;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.spotify.protocol.types.Track;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpotifyPlaylistHelper {

    private SharedPreferences sharedPreferences;
    private RequestQueue queue;

    public SpotifyPlaylistHelper (Context context) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
    }

    public void get(String playlistID, final VolleyCallBack<List<Track>> callBack) {

        String url = String.format(EndPoint.PLAYLISTME.toString(), playlistID);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                JSONArray jsonArray = response.optJSONArray("items");
                //String string = response.

                List<Track> playlistSongs = new ArrayList<>();

                for (int n = 0; n < jsonArray.length(); n++) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(n).optJSONObject("track");
                        Track track = gson.fromJson(jsonObject.toString(), Track.class);

                        playlistSongs.add(track);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                callBack.onSuccess(playlistSongs);
            }
        }, error -> {}) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }

    public void makePlaylist (String name, String description, boolean isPublic, final VolleyCallBack<String> callBack) {
        JSONObject payload = new JSONObject();
        try {
            payload.put("name", name);
            payload.put("description", description);
            payload.put("public", isPublic);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, EndPoint.PLAYLIST.toString(), payload, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                String responseString = response.optString("id");
                try {
                    responseString = response.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callBack.onSuccess(responseString);
            }
        }, (error) -> {
            makePlaylist(name, description, isPublic, callBack);
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }

    public void addSongsToPlaylist(String playlistID, List<String> songIDs) {
        JSONArray uriArray = new JSONArray();
        for (String songID: songIDs) {
            uriArray.put(songID);
        }

        JSONObject uris = new JSONObject();
        try {
            uris.put("uris", uriArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = String.format(EndPoint.PLAYLISTME.toString(), playlistID);
        JsonObjectRequest jsonObjectRequest = preparePutRequest(uris, Request.Method.POST, url);

        queue.add(jsonObjectRequest);
    }

    private JsonObjectRequest preparePutRequest(JSONObject payload, int method, String URL) {
        return new JsonObjectRequest(method, URL, payload, response -> {
        }, error -> {
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
    }
}
