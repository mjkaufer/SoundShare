package org.kaufer.soundshare;

import android.app.Activity;
import android.app.ListActivity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


public class Sounds extends ListActivity {

    Firebase ref;
    Firebase soundRef;
    ValueEventListener connectionListener;
    Spinner genre;
    SoundShareListAdapter soundShareListAdapter;
    Activity thisActivity = this;
    String lastG = "all";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sounds);
        Firebase.setAndroidContext(this);

        ref = new Firebase("https://kaufersoundshare.firebaseio.com/");
        soundRef = ref.child("sounds");

        final ListView l = getListView();
        soundShareListAdapter = new SoundShareListAdapter(soundRef.limitToLast(20), thisActivity, R.layout.shared_sound_list);
        l.setAdapter(soundShareListAdapter);

        genre = (Spinner)findViewById(R.id.genres);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.genresWithAll, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genre.setAdapter(adapter);
        genre.setPrompt("Genre");

        genre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String g = genre.getSelectedItem().toString().toLowerCase();
                if(g.equals(lastG))
                    return;
                lastG = g;
                if (g.equals("all"))
                    soundShareListAdapter = new SoundShareListAdapter(soundRef.limitToLast(20), thisActivity, R.layout.shared_sound_list);
                else
                    soundShareListAdapter = new SoundShareListAdapter(soundRef.startAt(g, "genre").endAt(g + "~", "genre").limitToLast(20), thisActivity, R.layout.shared_sound_list);
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
                    Toast.makeText(Sounds.this, "Connected to Firebase", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Sounds.this, "Disconnected from Firebase", Toast.LENGTH_SHORT).show();
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
