package com.example.spotifydating;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.ContextCompat;

import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;


public class MainActivity extends AppCompatActivity {

    // Navigation viewet.
    private BottomNavigationView navigationView;

    // Vores API helper class, som snakker med mobilens spotify applikation.
    private SpotifyRemoteHelper spotifyRemoteHelper;

    private SongManager songManager;

    private PlaylistFragment playlistFragment;
    private SwipeFragment swipeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sæt statusbarens farve til sort (bar i toppen af skærmen).
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        // Hent navigationsviewet.
        navigationView = findViewById(R.id.bottom_navigation);

        songManager = new SongManager();

        // De to fragments (sider) i appen swipingsiden og playlistsiden.
        prepareFragments();

        spotifyRemoteHelper = new SpotifyRemoteHelper(this, new SpotifyRemoteHelper.ConnectionCallback() {
            @Override
            public void onConnected() {
                swipeFragment.onSpotifyConnected(spotifyRemoteHelper);
            }
        });
    }
    @Override
    public void onBackPressed() {
        return;
    }

    public void prepareFragments() {
        swipeFragment = new SwipeFragment(songManager);
        playlistFragment = new PlaylistFragment(songManager);

        // Vi tilføjer begge fragments og viser swipefragmentet
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.body_container, swipeFragment)
                .add(R.id.body_container, playlistFragment)
                .hide(playlistFragment)
                .commit();

        // Sæt en listener på navigationsviewet, som sørger for at skifte imellem de to sider.
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                // Switch over knappens id i navigationsviewet.
                switch (item.getItemId()) {

                    // Id'et tilhører swipesiden
                    case R.id.nav_swipe:
                        // Vis swipefragment og gem playlistfragment.
                        getSupportFragmentManager()
                                .beginTransaction()
                                .hide(playlistFragment)
                                .show(swipeFragment).commit();
                        break;
                    case R.id.nav_playlist:

                        //Vis playlistfragment og gem swipefragment.
                        getSupportFragmentManager()
                                .beginTransaction()
                                .show(playlistFragment)
                                .hide(swipeFragment).commit();
                        break;
                }
                return true;
            }
        });
    }

}


