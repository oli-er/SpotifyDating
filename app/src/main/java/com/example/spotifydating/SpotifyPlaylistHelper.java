package com.example.spotifydating;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Hjælperklasse til interaktion med spotify playlist API'en.
public class SpotifyPlaylistHelper {

    private SharedPreferences sharedPreferences;

    // Queue til web requests.
    private RequestQueue queue;

    public SpotifyPlaylistHelper (Context context) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
    }

    // Laver en playliste på spotifykontoen ved at sende en post request tit API'en.
    public void makePlaylist (String name, String description, boolean isPublic, final RequestCallBack<String> callBack) {

        // Lav en payload, som indeholder playlistens navn, beskrivelse og om den er offentlig.
        JSONObject payload = new JSONObject();
        try {
            payload.put("name", name);
            payload.put("description", description);
            payload.put("public", isPublic);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // Generer en requesten ved at give JsonObjectRequest konstruktøren info
        // om requesttypen (POST), endpoint (spotify api link) og payload.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                EndPoint.PLAYLIST.toString(),
                payload, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Spotify API'en giver i dens respons playlisten id.
                // Vi henter dette.
                String responseString = response.optString("id");
                try {
                    responseString = response.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Kør callback parameterets onSuccess funktion.
                callBack.onSuccess(responseString);
            }
        }, (error) -> {
            Log.d("SpotifyAPI", "Kunne ikke lave playlisten");
        }) {
            // Headers til requesten.
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return generateHeaders();
            }
        };

        // Tilfjøj requesten til queuen.
        queue.add(jsonObjectRequest);
    }

    // Tilføjer  sange til en playliste ved at sende en post request til API'en.
    public void addSongsToPlaylist(String playlistID, List<String> songIDs) {

        // Lav et jsonarray med sangenes uri's.
        JSONArray uriArray = new JSONArray();
        for (String songID: songIDs) {
            uriArray.put(songID);
        }

        // Generer et JSON objekt, der holder sangenes uri's
        JSONObject uris = new JSONObject();
        try {
            uris.put("uris", uriArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Dan API url ved at inkorperere playlistens id i playlist endpointet.
        String url = String.format(EndPoint.PLAYLISTME.toString(), playlistID);

        // Lav web request objektet.
        JsonObjectRequest jsonObjectRequest = preparePutRequest(uris, Request.Method.POST, url);

        // Tilføj requesten til queuen.
        queue.add(jsonObjectRequest);
    }

    // Laver en put request.
    private JsonObjectRequest preparePutRequest(JSONObject payload, int method, String URL) {
        return new JsonObjectRequest(method, URL, payload, response -> {
        }, error -> {
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return generateHeaders();
            }
        };
    }

    // Gererer headers for en web request.
    private Map<String, String> generateHeaders() {

        // Mappet, der holder vores headers.
        Map<String, String> headers = new HashMap<>();

        // Hent brugerens autoriseringstoken.
        String token = sharedPreferences.getString("token", "");

        String auth = "Bearer " + token;
        headers.put("Authorization", auth); // Tilføj autoriseringstoken
        headers.put("Content-Type", "application/json"); // Tilføj datatype

        return headers;
    }
}
