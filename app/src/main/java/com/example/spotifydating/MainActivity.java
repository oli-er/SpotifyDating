package com.example.spotifydating;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.viewpager2.widget.ViewPager2;

import com.example.spotifydating.databinding.ActivityMainBinding;
import com.spotify.android.appremote.api.AppRemote;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.ContentApi;
import com.spotify.android.appremote.api.PlayerApi;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.ErrorCallback;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Empty;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.ListItem;
import com.spotify.protocol.types.ListItems;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;

import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Capabilities;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;


public class MainActivity extends AppCompatActivity {

    // BottomNavigationView
    BottomNavigationView navigationView;
    SpotifyAPIHelper spotifyAPIHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set statusbar color
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        navigationView = findViewById(R.id.bottom_navigation);

        SongManager songManager = new SongManager();

        SwipeFragment swipeFragment = new SwipeFragment(songManager);
        PlaylistFragment playlistFragment = new PlaylistFragment(songManager);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.body_container, swipeFragment)
                .commit();

        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.nav_swipe:
                        fragment = swipeFragment;
                        getSupportFragmentManager().beginTransaction().hide(playlistFragment).show(swipeFragment).commit();
                        break;
                    case R.id.nav_playlist:
                        fragment = playlistFragment;
                        if (playlistFragment.isAdded()) {
                            getSupportFragmentManager().beginTransaction().show(playlistFragment).hide(swipeFragment).commit();
                        } else {
                            getSupportFragmentManager().beginTransaction().add(R.id.body_container, playlistFragment).hide(swipeFragment).commit();
                        }
                        break;
                }

                return true;
            }
        });

        spotifyAPIHelper = new SpotifyAPIHelper(this, new SpotifyAPIHelper.ConnectionCallback() {
            @Override
            public void onConnected() {
                swipeFragment.onSpotifyConnected(spotifyAPIHelper);
            }
        });
    }

    public class SongManager {
        private List<SongItem> playlistSongs;
        private SongsCallBack songsCallBack;

        public SongManager() {
            this.playlistSongs = new ArrayList<>();
        }

        public SongManager(List<SongItem> playlistSongs) {
            this.playlistSongs = playlistSongs;
        }

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

        private void songsChanged() {
            if (songsCallBack != null) {
                songsCallBack.onSongsChanged(playlistSongs);
            }
        }


        public List<SongItem> getSongs() {
            return playlistSongs;
        }
    }

    public interface SongsCallBack {
        void onSongsChanged(List<SongItem> songs);
    }
}


