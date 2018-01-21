package com.example.yasaad.humtrivia.SpotifyApi;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.yasaad.humtrivia.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GuessingActivity extends AppCompatActivity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback, View.OnClickListener
            {

                private static final String CLIENT_ID = "a4b3c57e84044600b0ed48bbf97f9c88";
            private static final String REDIRECT_URI = "humtrivia-login://callback";
                private static final int REQUEST_CODE = 1337;
                private Button newSong;
                private Button submitTitle;
                private ImageButton play;
                private ImageButton pause;
                private EditText songSuggestion;
                private FirebaseDatabase database;
                private FirebaseStorage storage;
                private StorageReference storageRef;
                private Player mPlayer;
                private ArrayList<String> audioFiles;
                private String splitter = "parentClassKey";
                //private String mFileName = null;
                private Uri downloadUrl;
                private MediaPlayer player;
                private String randUserID;
                private String randSongID;

                @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_guessing);

                    newSong = (Button) findViewById(R.id.newSong);
                    submitTitle = (Button) findViewById(R.id.submitTitle);
                    songSuggestion = (EditText) findViewById(R.id.songSuggestion);
                    play = (ImageButton) findViewById(R.id.play);
                    pause = (ImageButton) findViewById(R.id.pause);

                    newSong.setOnClickListener(this);
                    submitTitle.setOnClickListener(this);
                    play.setOnClickListener(this);
                    pause.setOnClickListener(this);
                    storage = FirebaseStorage.getInstance();

                    play.setVisibility(View.GONE);
                    pause.setVisibility(View.GONE);

                    //mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
                    // mFileName += "/recorded_tune.3gp";


                    database = FirebaseDatabase.getInstance();

                    database.getReference().addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            audioFiles = new ArrayList<String>();
                            for (DataSnapshot user : dataSnapshot.getChildren()) {
                                if (!(user.getKey().equals(FirebaseAuth.getInstance().getUid()))) {
                                    for (DataSnapshot song : user.child("Songs").getChildren()) {
                                        audioFiles.add(song.getKey() + splitter
                                                + user.getKey());
                                    }
                                }
                            }
                            getRandomID();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });

                    AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                            AuthenticationResponse.Type.TOKEN,
                            REDIRECT_URI);
                builder.setScopes(new String[]{"user-read-private", "streaming"});
                AuthenticationRequest request = builder.build();

                AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

            }


            @Override
            protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
                super.onActivityResult(requestCode, resultCode, intent);

                // Check if result comes from the correct activity
                if (requestCode == REQUEST_CODE) {
                    AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
                    if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                        Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                        Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                            @Override
                            public void onInitialized(SpotifyPlayer spotifyPlayer) {
                                mPlayer = spotifyPlayer;
                                mPlayer.addConnectionStateCallback(GuessingActivity.this);
                                mPlayer.addNotificationCallback(GuessingActivity.this);
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            protected void onDestroy() {
                Spotify.destroyPlayer(this);
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
                mPlayer.playUri(null, "spotify:track:2TpxZ7JUBn3uw46aR7qd6V", 0, 0);
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

                @Override
                public void onClick(View view) {
                    if (view == newSong) {
                        getRandomID();
                        Toast.makeText(GuessingActivity.this, "New Song Loaded"
                                , Toast.LENGTH_SHORT).show();
                    }
                    if (view == submitTitle) {
                        database.getReference().child(randUserID).child("Songs")
                                .child(randSongID).child("Names")
                                .setValue(songSuggestion.getText().toString());
                        songSuggestion.setText("");
                    }
                    if (view == play) {
                        play.setVisibility(View.GONE);
                        pause.setVisibility(View.VISIBLE);
                        try {
                            player = new MediaPlayer();
                            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            player.setDataSource(downloadUrl.toString());
                            player.prepare();
                            player.start();
                            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    pause.setVisibility(View.GONE);
                                    play.setVisibility(View.VISIBLE);
                                }
                            });
                        } catch (Exception e) {
                        }
                    }
                    if (view == pause) {
                        pause.setVisibility(View.GONE);
                        play.setVisibility(View.VISIBLE);
                        player.stop();
                        try {
                            player.prepare();
                        } catch (IOException e) {
                        }
                    }
                }

                private void getSong(String randUserID, String randSongID) {
                    storageRef = storage.getReference(randUserID);
                    storageRef.child(randSongID + ".3pg")
                            .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            play.setVisibility(View.VISIBLE);
                            System.out.println(downloadUrl = uri);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });


                }

                public void getRandomID() {
                    String result = audioFiles.get(new Random().nextInt(audioFiles.size()));
                    randUserID = result.split(splitter)[1];
                    randSongID = result.split(splitter)[0];
                    getSong(randUserID, randSongID);
                }
            }
