package com.example.yasaad.humtrivia.SpotifyApi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.example.yasaad.humtrivia.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

public class GuessingActivity extends AppCompatActivity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback
            {

            private static final String CLIENT_ID = " a4b3c57e84044600b0ed48bbf97f9c88 ";
            private static final String REDIRECT_URI = "humtrivia-login://callback";

            private Player mPlayer;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_guessing);
            }

            @Override
            protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
                super.onActivityResult(requestCode, resultCode, intent);
            }

            @Override
            protected void onDestroy() {
                super.onDestroy();
            }

            @Override
            public void onPlaybackEvent(PlayerEvent playerEvent) {
                Log.d("MainActivity", "Playback event received: " + playerEvent.name());
                switch (playerEvent) {
                    // Handle event type as necessary
                    default:
                        break;
                }
            }

            @Override
            public void onPlaybackError(Error error) {
                Log.d("MainActivity", "Playback error received: " + error.name());
                switch (error) {
                    // Handle error type as necessary
                    default:
                        break;
                }
            }

            @Override
            public void onLoggedIn() {
                Log.d("MainActivity", "User logged in");
            }

            @Override
            public void onLoggedOut() {
                Log.d("MainActivity", "User logged out");
            }

            @Override
            public void onLoginFailed(Error var1) {
                Log.d("MainActivity", "Login failed");
            }

            @Override
            public void onTemporaryError() {
                Log.d("MainActivity", "Temporary error occurred");
            }

            @Override
            public void onConnectionMessage(String message) {
                Log.d("MainActivity", "Received connection message: " + message);
            }

        }
