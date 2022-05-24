package com.example.spotifydating;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.DefaultItemAnimator;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.spotify.protocol.client.ErrorCallback;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SwipeFragment extends Fragment {


    private static final String TAG = SwipeFragment.class.getSimpleName();

    // Liste med sangene som vises (kun den nuværende sang).
    private List<SongItem> songItems;

    // Manager af hendholdsvis aktion og indhold til cardStack
    private CardStackLayoutManager cardStackLayoutManager;
    private CardStackAdapter cardStackAdapter;

    // Views
    private TextView songNameTV, songArtistTV, songAlbumTV;
    private ImageButton btnSkip, btnAdd, btnRewind;
    private CardStackView cardStackView;

    // Hjælperklasse til spotify API'en.
    private SpotifyRemoteHelper spotifyRemoteHelper;

    // Manager til sangene som er swipet til højre.
    private SongManager songManager;

    // Sangen som er på skærmen lige nu.
    private SongItem currentSong;

    // Subscription til playerstaten af spotify playeren.
    Subscription mPlayerStateSubscription;

    public SwipeFragment() {
        // Required empty public constructor
    }

    public SwipeFragment(SongManager songManager) {
        this.songManager = songManager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_swipe, container, false);

        // Hent views.
        songNameTV = view.findViewById(R.id.song_name);
        songArtistTV = view.findViewById(R.id.song_artist);
        songAlbumTV = view.findViewById(R.id.song_album);
        btnSkip = view.findViewById(R.id.btn_skip);
        btnAdd   = view.findViewById(R.id.btn_add);
        btnRewind = view.findViewById(R.id.btn_rewind);

        // Sæt onclicklisteners (funktioner, der køres når knappe trykkes).
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipe(Direction.Left);
            }
        });

        btnRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spotifyRemoteHelper.skipToPreviousTrack();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipe(Direction.Right);
            }
        });

        // Instanciate listen med vores sange.
        songItems = new ArrayList<SongItem>();

        // Placeholder billede
        Bitmap placeholderBitMap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.ic_launcher_foreground);

        // Tilføj placeholder sang.
        songItems.add(
                new SongItem(placeholderBitMap, "song", "artist", "album", ""));

        return view;
    }

    // Funktion, der laver et automatisk swipe af albumbilledet.
    private void swipe(Direction direction) {

        // Lav animaitonen til swipet.
        SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                .setDirection(direction)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(new AccelerateInterpolator())
                .build();

        // Set animationen og swipe.
        cardStackLayoutManager.setSwipeAnimationSetting(setting);
        cardStackView.swipe();
    }

    // Funktion, der køres når spotify er forbundet.
    public void onSpotifyConnected(SpotifyRemoteHelper spotifyRemoteHelper) {

        // Gem spotifyRemoteHelper klassen, som blev dannet.
        this.spotifyRemoteHelper = spotifyRemoteHelper;
        Toast.makeText(getContext(), "Forbandt til spotify.", Toast.LENGTH_LONG);

        // Subscribe til playerstaten.
        mPlayerStateSubscription = this.spotifyRemoteHelper.subscribeToPlayerState(new SwipeFragment.mySubscriptionEventCallback());

        // Dan vores cardstackview.
        cardStackView = getView().findViewById(R.id.card_stack_view);
        cardStackLayoutManager = new CardStackLayoutManager(getContext(), new SwipeFragment.myCardStackListener());
        cardStackAdapter = new CardStackAdapter(songItems);
        cardStackView.setLayoutManager(cardStackLayoutManager);
        cardStackView.setAdapter(cardStackAdapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
    }

    // Load en sangs data ind i swiperen.
    private void loadTrack(Track track) {
        spotifyRemoteHelper.mSpotifyAppRemote
                .getImagesApi()
                .getImage(track.imageUri, Image.Dimension.LARGE) // Hent album billedet.
                .setResultCallback(
                        bitmap -> {
                            songNameTV.setText(track.name);
                            songArtistTV.setText(track.artist.name);
                            songAlbumTV.setText(track.album.name);

                            currentSong = new SongItem(bitmap, track.name, track.artist.name, track.album.name, track.uri);

                            // Sæt sangen til objektet i toppen af cardstacken (swiperen).
                            cardStackAdapter.getItems().set(0, currentSong);

                            cardStackAdapter.notifyItemRemoved(0);
                            cardStackAdapter.notifyItemChanged(0);

                            // Skift baggrundsfarve.
                            setBackgroundColor(bitmap);
                        });
    }

    // Callback klasse til spotify player subscription.
    class mySubscriptionEventCallback implements Subscription.EventCallback<PlayerState> {

        @Override
        public void onEvent(PlayerState playerState) {
            // Hvis sangen er skiftet.
            if (currentSong == null || !Objects.equals(playerState.track.uri, currentSong.getId())) {
                // Load sangen på skærmen.
                loadTrack(playerState.track);
            }
        }
    }

    // Manager til swiping af sange.
    class myCardStackListener implements CardStackListener {

        @Override
        public void onCardDragging(Direction direction, float ratio) {

        }

        // Køres hvis en sang blev swipet på.
        @Override
        public void onCardSwiped(Direction direction) {

            // Hvis den blev swipet til højre.
            if (direction == Direction.Right) {

                Toast.makeText(getActivity(), "Tilføjede sangen.", Toast.LENGTH_LONG).show();

                SongItem song = cardStackAdapter.getItems().get(0);

                // Hvis songmangeren ikke har sangen.
                if (!songManager.getSongs().contains(song)) {
                    // Tilføj sangen.
                    songManager.addSong(cardStackAdapter.getItems().get(0));
                }
            }

            // Skip sangen.
            spotifyRemoteHelper.skipTrack().setErrorCallback(new ErrorCallback() {
                @Override
                public void onError(Throwable t) {
                    // Hvis brugeren ikke kunne skippe, giver vi notifikation.
                    Toast.makeText(getContext(), "Du kan ikke skippe flere.", Toast.LENGTH_LONG).show();
                    cardStackAdapter.getItems().set(0, currentSong);
                    cardStackAdapter.notifyItemRemoved(0);
                    cardStackAdapter.notifyItemInserted(0);
                }
            });
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

    // Sæt baggrundsfarven udfra en sangs billede.
    public void setBackgroundColor(Bitmap bitmap) {

        // Generer en palette med farver udfra sangen.
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette p) {

                // Hent farven for billedet.
                int color = p.getLightVibrantColor(0);
                if (color == 0) {
                    color = p.getVibrantColor(0);
                    if (color == 0) {
                        color = p.getDarkVibrantColor(0);
                        if (color == 0) {
                            color = getResources().getColor(R.color.spotify_black);
                        }
                    }
                }

                // Lav en gradiant udfra farven.
                GradientDrawable gradiantDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                        new int[] {color, getResources().getColor(R.color.spotify_black)});

                // Sæt gradienten til baggrunden.
                getView().setBackground(gradiantDrawable);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}