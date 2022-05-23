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
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black));

        navigationView = findViewById(R.id.bottom_navigation);

        SwipeFragment swipeFragment = new SwipeFragment();
        PlaylistFragment playlistFragment = new PlaylistFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.body_container, swipeFragment)
                .commit();

        SharedPreferences.Editor editor = getSharedPreferences("SPOTIFY", 0).edit();

        SpotifyPlaylistHelper spotifyPlaylistHelper = new SpotifyPlaylistHelper(this);

        spotifyPlaylistHelper.makePlaylist("SwipeMix", "Swipemix", false, new VolleyCallBack<String>() {
            @Override
            public void onSuccess(String data) {
                editor.putString("playlist", data);
            }
        });

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
                playlistFragment.onSpotifyConnected(spotifyAPIHelper);
            }
        });

        /*
        spotifyHelper = new SpotifyAPIHelper(this, new SpotifyAPIHelper.ConnectionCallback() {
            @Override
            public void onConnected() {
                onSpotifyConnected();
            }
        });

        songItems = new ArrayList<SongItem>();
        Bitmap placeholderBitMap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.ic_launcher_foreground);
        songItems.add(
                new SongItem(placeholderBitMap, "song", "artist", "album", ""));

        songsSwipedRight = new ArrayList<>();
         */
    }
/*
    private void onSpotifyConnected() {
        Toast.makeText(this, "Connected!", Toast.LENGTH_LONG);

        spotifyHelper.makePlaylist("test", "test", false, songsSwipedRight, new VolleyCallBack<String>() {
            @Override
            public void onSuccess(String data) {
                Toast.makeText(MainActivity.this, "WOW", Toast.LENGTH_LONG);
            }
        });

        mPlayerStateSubscription = spotifyHelper.subscribeToPlayerState(new mySubscriptionEventCallback());

        CardStackView cardStackView = findViewById(R.id.card_stack_view);

        cardStackLayoutManager = new CardStackLayoutManager(this, new myCardStackListener());

        // Stack view
        cardStackAdapter = new CardStackAdapter(songItems);
        cardStackView.setLayoutManager(cardStackLayoutManager);
        cardStackView.setAdapter(cardStackAdapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
    }


    private void logError(Throwable throwable) {
        Toast.makeText(this, R.string.err_generic_toast, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "", throwable);
    }


    private void loadTrack(Track track) {
        spotifyHelper.mSpotifyAppRemote
            .getImagesApi()
            .getImage(track.imageUri, Image.Dimension.LARGE)
            .setResultCallback(
                bitmap -> {
                    SongItem songItem = new SongItem(bitmap, track.name, track.artist.name, track.album.name, track.uri);
                    cardStackAdapter.getItems().set(0, songItem);

                    // Vi lader som om en sang blev fjernet, så cardStackAdapteren ikke går til næste
                    // element.
                    cardStackAdapter.notifyItemRemoved(0);
                    cardStackAdapter.notifyItemChanged(0);
                });
    }

    class mySubscriptionEventCallback implements Subscription.EventCallback<PlayerState> {

        @Override
        public void onEvent(PlayerState playerState) {

            if (playerState.track != null) {
                // Load track onto screen.
                loadTrack(playerState.track);
            }
        }
    }

    class myCardStackListener implements CardStackListener {

        @Override
        public void onCardDragging(Direction direction, float ratio) {

        }

        @Override
        public void onCardSwiped(Direction direction) {
            Toast.makeText(MainActivity.this, "Swiped", Toast.LENGTH_LONG).show();

            if (direction == Direction.Right) {
                songsSwipedRight.add(cardStackAdapter.getItems().get(0).getId());
            }
            if (direction == Direction.Left) {
                spotifyHelper.addSongsToPlaylist("4GrVxxovGcP5FbV3qiA2th", songsSwipedRight);
            }
            spotifyHelper.skipTrack();
        }



        @Override
        public void onCardRewound() {

        }

        @Override
        public void onCardCanceled() {

        }

        @Override
        public void onCardAppeared(View view, int position) {

        }

        @Override
        public void onCardDisappeared(View view, int position) {

        }
    }
    */
}


