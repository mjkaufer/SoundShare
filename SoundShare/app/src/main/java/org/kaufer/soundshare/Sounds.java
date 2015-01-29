package org.kaufer.soundshare;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
//import com.google.api.services.youtube.YouTube;
//
import com.google.android.youtube.player.YouTubeIntents;


public class Sounds extends ListActivity {

    Firebase ref;
    Firebase soundRef;
    ValueEventListener connectionListener;
    Spinner genre;
    SoundShareListAdapter soundShareListAdapter;
    Activity thisActivity = this;
    String lastG = "all";
    ListView l;
    boolean alreadyConnected = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sounds);
        Firebase.setAndroidContext(this);

        ref = new Firebase("https://kaufersoundshare.firebaseio.com/");
        soundRef = ref.child("sounds");


        l = getListView();
        soundShareListAdapter = new SoundShareListAdapter(soundRef.limitToLast(20), thisActivity, R.layout.shared_sound_list);
        l.setAdapter(soundShareListAdapter);
//        l.setScrollY(0);
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                String artist = ((TextView)adapterView.findViewById(R.id.artist)).getText().toString();
//                System.out.println(artist);
                Sound sound = ((Sound)soundShareListAdapter.getItem(i));
                String search = sound.getSong() + ", by " + sound.getArtist();
                System.out.println(search);

                Intent intent = YouTubeIntents.createSearchIntent(getApplicationContext(), search);
                startActivity(intent);

            }
        });

        genre = (Spinner)findViewById(R.id.genres);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.genresWithAll, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genre.setAdapter(adapter);
        genre.setPrompt("Genre");

        genre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String g = genre.getSelectedItem().toString();
                if(g.equals(lastG))
                    return;
                lastG = g;
                soundRef = ref.child("sounds");
                if (g.equals("All"))
                    soundShareListAdapter = new SoundShareListAdapter(soundRef.limitToLast(20), thisActivity, R.layout.shared_sound_list);
                else
                    soundShareListAdapter = new SoundShareListAdapter(soundRef.orderByChild("genre").equalTo(g).limitToLast(20), thisActivity, R.layout.shared_sound_list);
                soundShareListAdapter.notifyDataSetChanged();
                getListView().setAdapter(soundShareListAdapter);

//                getListView().setAdapter(soundShareListAdapter);
//                soundShareListAdapter.notifyDataSetChanged();
//                soundShareListAdapter.
//                l.setAdapter(soundShareListAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        soundShareListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                l.setSelection(soundShareListAdapter.getCount() - 1);
            }
        });

        connectionListener = soundRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    Toast.makeText(Sounds.this, "Online", Toast.LENGTH_SHORT).show();
                } else if(!alreadyConnected) {
                    Toast.makeText(Sounds.this, "Connecting...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Sounds.this, "Offline", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sounds, menu);
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
        try {
            soundRef = ref.child("sounds");
            if (lastG.equals("All"))
                soundShareListAdapter = new SoundShareListAdapter(soundRef.limitToLast(20), thisActivity, R.layout.shared_sound_list);
            else
                soundShareListAdapter = new SoundShareListAdapter(soundRef.orderByChild("genre").equalTo(lastG).limitToLast(20), thisActivity, R.layout.shared_sound_list);
            soundShareListAdapter.notifyDataSetChanged();
            getListView().setAdapter(soundShareListAdapter);
        } catch(Exception e){}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
        soundRef.getRoot().child(".info/connected").removeEventListener(connectionListener);
        soundShareListAdapter.cleanup();
    }
}
