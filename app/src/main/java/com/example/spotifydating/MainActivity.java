package com.example.spotifydating;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.DefaultItemAnimator;

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
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Capabilities;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private List<SongItem> songItems;
    private CardStackLayoutManager cardStackLayoutManager;
    private CardStackAdapter cardStackAdapter;

    private static SpotifyAppRemote mSpotifyAppRemote;
    private static final String CLIENT_ID = "f9a4127598ed4dd4aed26e20f8d30374";
    private static final String REDIRECT_URI = "http://com.example.spotifydating/callback";

    private final ErrorCallback mErrorCallback = this::logError;
    private Subscription<PlayerState> mPlayerStateSubscription;


    private final Subscription.EventCallback<PlayerState> mPlayerStateEventCallback =
            new Subscription.EventCallback<PlayerState>() {
                @Override
                public void onEvent(PlayerState playerState) {

                    if (playerState.track != null) {
                        // Load track onto screen.
                        loadTrack(playerState.track);
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        // Define list of songs.
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.ic_launcher_foreground);

        songItems = new ArrayList<SongItem>();
        Bitmap placeholderBitMap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.ic_launcher_foreground);
        songItems.add(
                new SongItem(placeholderBitMap, "song", "artist", "album"));

        //SpotifyAppRemote
        // Swiping viewet.
        CardStackView cardStackView = findViewById(R.id.card_stack_view);

        cardStackLayoutManager = new CardStackLayoutManager(this, new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {

            }

            @Override
            public void onCardSwiped(Direction direction) {
                Toast.makeText(MainActivity.this, "Swiped", Toast.LENGTH_LONG).show();
                mSpotifyAppRemote.getPlayerApi().skipNext();
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
        });


        // Stack view
        cardStackAdapter = new CardStackAdapter(songItems);
        cardStackView.setLayoutManager(cardStackLayoutManager);
        cardStackView.setAdapter(cardStackAdapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());

        // Spot
        //Capabilities.
        connect(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // Connects the SpotifyAppRemote class to spotify.
    private void connect(boolean showAuthView) {

        SpotifyAppRemote.disconnect(mSpotifyAppRemote);

        SpotifyAppRemote.connect(
            getApplicationContext(),
            new ConnectionParams.Builder(CLIENT_ID)
                    .setRedirectUri(REDIRECT_URI)
                    .showAuthView(showAuthView)
                    .build(),
            new Connector.ConnectionListener() {
                @Override
                public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                    mSpotifyAppRemote = spotifyAppRemote;
                    MainActivity.this.onConnected();
                }

                @Override
                public void onFailure(Throwable error) {
                    logError(error);
                    MainActivity.this.onDisconnected();
                }
            });
    }

    private void onConnected() {
        Toast.makeText(this, "Connected!", Toast.LENGTH_LONG);
        subscribeToPlayerState();
        loadNextSong();
    }


    private void subscribeToPlayerState() {
        if (mPlayerStateSubscription != null && !mPlayerStateSubscription.isCanceled()) {
            mPlayerStateSubscription.cancel();
            mPlayerStateSubscription = null;
        }

        mPlayerStateSubscription = (Subscription<PlayerState>) mSpotifyAppRemote
                    .getPlayerApi()
                    .subscribeToPlayerState()
                    .setEventCallback(mPlayerStateEventCallback);
    }

    private void loadNextSong() {
        mSpotifyAppRemote
                .getContentApi()
                .getRecommendedContentItems(ContentApi.ContentType.NAVIGATION)
                .setResultCallback(new CallResult.ResultCallback<ListItems>() {
                    @Override
                    public void onResult(ListItems data) {
                        ListItem listItem = data.items[1];
                        mSpotifyAppRemote
                                .getContentApi()
                                .playContentItem(listItem)
                                .setResultCallback(new CallResult.ResultCallback<Empty>() {
                                    @Override
                                    public void onResult(Empty data) {
                                        Toast.makeText(MainActivity.this, "test", Toast.LENGTH_LONG);
                                    }
                                });
                    }
                });
    }

    private void onDisconnected() {
        Toast.makeText(this, "Not connected...", Toast.LENGTH_LONG);
    }

    private void logError(Throwable throwable) {
        Toast.makeText(this, R.string.err_generic_toast, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "", throwable);
    }

    public void onSkip(View view) {
        mSpotifyAppRemote
                .getPlayerApi()
                .skipNext()
                .setErrorCallback(mErrorCallback);
    }

    private void loadTrack(Track track) {
        // Find billede

        mSpotifyAppRemote
            .getImagesApi()
            .getImage(track.imageUri, Image.Dimension.LARGE)
            .setResultCallback(
                bitmap -> {
                    SongItem songItem = new SongItem(bitmap, track.name, track.artist.name, track.album.name);
                    cardStackAdapter.getItems().set(0, songItem);

                    // Vi lader som om en sang blev fjernet, så cardStackAdapteren ikke går til næste
                    // element.
                    cardStackAdapter.notifyItemRemoved(0);
                    cardStackAdapter.notifyItemChanged(0);

                });
    }
}


