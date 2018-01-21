package com.example.yasaad.humtrivia.UI;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.yasaad.humtrivia.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyTunes extends AppCompatActivity {

    private ListView main_listview;
    private FirebaseDatabase database;
    private ArrayList songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tunes);

        main_listview = (ListView) findViewById(R.id.main_listview);
        int redcolor = Color.parseColor("#FF0000");
        main_listview.setBackgroundColor(redcolor);

        database = FirebaseDatabase.getInstance();

        database.getReference().addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                songs = new ArrayList<String>();
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    if ((user.getKey().equals(FirebaseAuth.getInstance().getUid()))) {
                        for (DataSnapshot song : user.child("Songs").getChildren()) {
                            songs.add(song.getValue());
                        }
                    }
                }

            }

            ;

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        ListAdapter listAdapter = new ArrayAdapter<String>(this
                , android.R.layout.simple_list_item_1, songs);

    }

    private class MyCustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return null;
        }
    }
}
