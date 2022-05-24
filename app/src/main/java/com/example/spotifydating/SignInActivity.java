package com.example.spotifydating;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

// Klasse til signin activitien, som loades når appen starter.
// Her bliver der logget ind i spotify.
public class SignInActivity extends AppCompatActivity {

    // Requestcode til når spotify authenticates.
    private static final int REQUEST_CODE = 1337;

    // Id til vores spotify API profil.
    private static final String CLIENT_ID = "f9a4127598ed4dd4aed26e20f8d30374";

    // Redirect URI, som bruges når appen er godkendt.
    private static final String REDIRECT_URI = "com.example.spotifydating://callback";

    // SharedPreferences editor, som redigerer appens data (bruges til at lagre login token).
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Kør godkendelse.
        authorize();
    }

    // Funktion, der køres når brugeren er autoriseret.
    private void onAuthorized() {
        // Start hovedsiden.
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void authorize() {
        // Brug spotify AuthorizationRequest objektet til at lave en anmodning om verificering.
        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);

        // Sæt tilladelser, som gives til appen. Vi skal bruge tilladelse til at
        // styre playeren og modificere playlister.
        builder.setScopes(new String[]{"streaming", "playlist-modify-private"});

        AuthorizationRequest request = builder.build();

        // Åben acitivitien (siden) med godkendelse.
        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    // Funktion, som køres når folk kommer tilbage til activitien (siden).
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Tjek om brugeren kommer fra spotify siden.
        if (requestCode == REQUEST_CODE) {

            // Hvis ja, hentes responsen fra AuthorizationResponse objektet.
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);

            // Switch over responstypen (token eller error).
            switch (response.getType()) {

                // Responsen er en token.
                case TOKEN:

                    // Lager token i appens preferences (data).
                    editor = getSharedPreferences("SPOTIFY", 0).edit();
                    editor.putString("token", response.getAccessToken());
                    editor.apply();

                    // Kør godkendelsesfunktion.
                    onAuthorized();
                    break;

                case ERROR:
                    Toast.makeText(this, "Kunne ikke logge ind.", Toast.LENGTH_LONG);
                    break;
            }
        }
    }
}