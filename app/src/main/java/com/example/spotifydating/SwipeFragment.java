package com.example.spotifydating;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.LinearGradient;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SwipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SwipeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = SwipeFragment.class.getSimpleName();

    private List<SongItem> songItems;
    private CardStackLayoutManager cardStackLayoutManager;
    private CardStackAdapter cardStackAdapter;

    private final ErrorCallback mErrorCallback = this::logError;
    private Subscription<PlayerState> mPlayerStateSubscription;

    private SpotifyAPIHelper spotifyHelper;

    private List<String> songsSwipedRight;

    private TextView songNameTV, songArtistTV, songAlbumTV;

    private ImageButton btnSkip, btnAdd, btnRewind;

    private SharedPreferences sharedPreferences;

    private MainActivity.SongManager songManager;

    private SongItem currentSong;

    private CardStackView cardStackView;

    public SwipeFragment() {
        // Required empty public constructor
    }

    public SwipeFragment(MainActivity.SongManager songManager) {
        this.songManager = songManager;
    }

    public static SwipeFragment newInstance(String param1, String param2) {
        SwipeFragment fragment = new SwipeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_swipe, container, false);

        songNameTV = view.findViewById(R.id.song_name);
        songArtistTV = view.findViewById(R.id.song_artist);
        songAlbumTV = view.findViewById(R.id.song_album);
        btnSkip = view.findViewById(R.id.btn_skip);
        btnAdd   = view.findViewById(R.id.btn_add);
        btnRewind = view.findViewById(R.id.btn_rewind);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipe(Direction.Left);
            }
        });

        btnRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spotifyHelper.skipToPreviousTrack();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipe(Direction.Right);
            }
        });

        songItems = new ArrayList<SongItem>();
        Bitmap placeholderBitMap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.ic_launcher_foreground);
        songItems.add(
                new SongItem(placeholderBitMap, "song", "artist", "album", ""));


        return view;
    }

    private void swipe(Direction direction) {
        SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                .setDirection(direction)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(new AccelerateInterpolator())
                .build();

        cardStackLayoutManager.setSwipeAnimationSetting(setting);
        cardStackView.swipe();
    }

    public void onSpotifyConnected(SpotifyAPIHelper spotifyAPIHelper) {
        spotifyHelper = spotifyAPIHelper;
        Toast.makeText(getContext(), "Connected!", Toast.LENGTH_LONG);

        mPlayerStateSubscription = spotifyHelper.subscribeToPlayerState(new SwipeFragment.mySubscriptionEventCallback());

        cardStackView = getView().findViewById(R.id.card_stack_view);

        cardStackLayoutManager = new CardStackLayoutManager(getContext(), new SwipeFragment.myCardStackListener());

        cardStackAdapter = new CardStackAdapter(songItems);
        cardStackView.setLayoutManager(cardStackLayoutManager);
        cardStackView.setAdapter(cardStackAdapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
    }


    private void logError(Throwable throwable) {
        Toast.makeText(getActivity(), R.string.err_generic_toast, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "", throwable);
    }


    private void loadTrack(Track track) {
        spotifyHelper.mSpotifyAppRemote
                .getImagesApi()
                .getImage(track.imageUri, Image.Dimension.LARGE)
                .setResultCallback(
                        bitmap -> {
                            songNameTV.setText(track.name);
                            songArtistTV.setText(track.artist.name);
                            songAlbumTV.setText(track.album.name);

                            currentSong = new SongItem(bitmap, track.name, track.artist.name, track.album.name, track.uri);
                            cardStackAdapter.getItems().set(0, currentSong);

                            cardStackAdapter.notifyItemRemoved(0);
                            cardStackAdapter.notifyItemChanged(0);
                            setBackgroundColor(bitmap);
                        });
    }

    class mySubscriptionEventCallback implements Subscription.EventCallback<PlayerState> {

        @Override
        public void onEvent(PlayerState playerState) {
            if (currentSong == null || !Objects.equals(playerState.track.uri, currentSong.getId())) {
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

            if (direction == Direction.Right) {
                Toast.makeText(getActivity(), "Swiped Right", Toast.LENGTH_LONG).show();

                SongItem song = cardStackAdapter.getItems().get(0);
                if (!songManager.getSongs().contains(song)) {
                    songManager.addSong(cardStackAdapter.getItems().get(0));
                }
            }
            spotifyHelper.skipTrack().setErrorCallback(new ErrorCallback() {
                @Override
                public void onError(Throwable t) {
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

    public void setBackgroundColor(Bitmap bitmap) {

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette p) {
                int h = getView().getHeight();
                int w = getView().getWidth();

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

                //ShapeDrawable mDrawable = new ShapeDrawable(new RectShape());

                GradientDrawable gradiantDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                        new int[] {color, getResources().getColor(R.color.spotify_black)});

                //mDrawable.getPaint().setShader(new LinearGradient(w/2, h/2, w, color, getResources().getColor(R.color.spotify_black), Shader.TileMode.CLAMP));
                getView().setBackground(gradiantDrawable);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}