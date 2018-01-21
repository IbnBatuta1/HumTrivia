package com.example.yasaad.humtrivia.UI;

import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

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

import java.io.IOException;
import java.util.ArrayList;

public class MyTunes extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private ListView main_listview;
    private ImageButton imageButton;
    private FirebaseDatabase database;
    private ArrayList songs;
    private ArrayList songID;
    private StorageReference storageRef;
    private FirebaseStorage storage;
    private Uri downloadUrl;
    private MediaPlayer player;
    private String userID = FirebaseAuth.getInstance().getUid();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tunes);

        main_listview = (ListView) findViewById(R.id.main_listview);
        main_listview.setBackground(getDrawable(R.drawable.background));
        imageButton = (ImageButton) findViewById(R.id.imageButton);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        progressDialog = new ProgressDialog(this);

        songs = new ArrayList<String>();
        songID = new ArrayList<String>();
        main_listview.setOnItemClickListener(this);
        imageButton.setOnClickListener(MyTunes.this);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                R.layout.cell, songs);

        database.getReference().addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                songs.clear();
                songID.clear();
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    if ((user.getKey().equals(FirebaseAuth.getInstance().getUid()))) {
                        for (DataSnapshot song : user.child("Songs").getChildren()) {
                            if (song.child("Name").getValue() != null
                                    && !(song.child("Name").getValue().equals(""))) {
                                songs.add(song.child("Name").getValue());
                                songID.add(song.getKey());
                            }
                        }
                    }
                }
                arrayAdapter.notifyDataSetChanged();
            }

            ;

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        main_listview.setAdapter(arrayAdapter);
    }

    private void pause() {
        player.stop();
        try {
            player.prepare();
        } catch (IOException e) {
        }
    }

    private void play() {
        try {
            player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(downloadUrl.toString());
            player.prepare();
            player.start();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                }
            });
        } catch (Exception e) {
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
        imageButton.setVisibility(View.VISIBLE);
        try {
            player.stop();
        } catch (Exception e) {
        }
        ;
        getSong(userID, (String) songID.get(i));
        progressDialog.setMessage("Getting Tune...");
        progressDialog.show();
    }


    private void getSong(String randUserID, String randSongID) {
        storageRef = storage.getReference(randUserID);
        storageRef.child(randSongID + ".3pg")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                downloadUrl = uri;
                play();
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view == imageButton) {
            try {
                player.stop();
            } catch (Exception e) {
            }
            ;
        }
        imageButton.setVisibility(View.GONE);
    }
}
