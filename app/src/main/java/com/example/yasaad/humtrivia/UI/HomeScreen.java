package com.example.yasaad.humtrivia.UI;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.yasaad.humtrivia.R;
import com.example.yasaad.humtrivia.SpotifyApi.GuessingActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.UUID;


public class HomeScreen extends AppCompatActivity implements View.OnClickListener {


    private static final String LOG_TAG = "Record_log";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private StorageReference mStorage;

    private ImageButton recordingButton;
    private ImageButton gameStart;
    private ImageButton myTunes;
    private ImageButton stop;
    private ImageButton delete;
    private ImageButton upload;
    private ImageButton play;
    private MediaRecorder mRecorder;
    private ProgressBar progressBar;
    private String mFileName = null;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private Uri uri;
    private MediaPlayer audio;

    private ProgressDialog mProgress;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {android.Manifest.permission.RECORD_AUDIO
            , android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "User logged Out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginScreen.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        recordingButton = (ImageButton) findViewById(R.id.recordingButton);
        delete = (ImageButton) findViewById(R.id.delete);
        upload = (ImageButton) findViewById(R.id.upload);
        progressBar = (ProgressBar) findViewById(R.id.recordingInSession);
        play = (ImageButton) findViewById(R.id.play);
        stop = (ImageButton) findViewById(R.id.stop);
        gameStart = (ImageButton) findViewById(R.id.gameStart);
        myTunes = (ImageButton) findViewById(R.id.myTunes);


        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        mStorage = FirebaseStorage.getInstance().getReference();
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/recorded_tune.3gp";
        uri = Uri.fromFile(new File(mFileName));
        mProgress = new ProgressDialog(this);
        progressBar.setVisibility(View.GONE);
        play.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);
        stop.setVisibility(View.GONE);

        delete.setOnClickListener(this);
        play.setOnClickListener(this);
        stop.setOnClickListener(this);
        upload.setOnClickListener(this);
        gameStart.setOnClickListener(this);
        myTunes.setOnClickListener(this);

        recordingButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    // record button depressed
                    startRecording();
                    play.setVisibility(View.GONE);
                    delete.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);

                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    // record button released
                    stopRecording();
                    progressBar.setVisibility(View.GONE);
                    delete.setVisibility(View.VISIBLE);
                    play.setVisibility(View.VISIBLE);

                }

                return false;
            }
        });
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
            e.printStackTrace();
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        upload.setVisibility(View.VISIBLE);
        audio = MediaPlayer.create(HomeScreen.this, uri);
    }


    @Override
    public void onClick(View view) {
        if (view == delete) {
            play.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            upload.setVisibility(View.GONE);
            stop.setVisibility(View.GONE);
        }
        if (view == play) {
            //play button is pressed
            //WRITE CODE HERE
            play.setVisibility(View.GONE);
            stop.setVisibility(View.VISIBLE);
            audio.start();
            audio.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stop.setVisibility(View.GONE);
                    play.setVisibility(View.VISIBLE);
                }
            });

        }
        if (view == stop) {
            stop.setVisibility(View.GONE);
            play.setVisibility(View.VISIBLE);
            audio.stop();
            try {
                audio.prepare();
            } catch (IOException e) {
            }
        }
        if (view == upload) {
            uploadAudio();
        }
        if (view == gameStart) {
            startActivity(new Intent(HomeScreen.this, GuessingActivity.class));
        }
        if (view == myTunes) {
            startActivity(new Intent(HomeScreen.this, MyTunes.class));
        }
    }

    private void uploadAudio() {

        mProgress.setMessage("Uploading Audio...");
        mProgress.show();


        String audioName = UUID.randomUUID().toString();
        String userID = firebaseAuth.getUid();

        myRef = database.getReference(userID);

        StorageReference filepath = mStorage.child(userID).child(audioName + ".3pg");
        DatabaseReference newSong = myRef.child("Songs").child(audioName).push();
        newSong.setValue(audioName);
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mProgress.dismiss();
                Toast.makeText(HomeScreen.this, "Successfully Uploaded"
                        , Toast.LENGTH_SHORT).show();
            }
        });
        play.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);

    }
}
