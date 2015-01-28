package org.kaufer.soundshare;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.Date;
import java.util.HashMap;


public class Share extends Activity {

    Button share;
    Spinner genre;
    EditText artist, song;
    Firebase ref = new Firebase("https://kaufersoundshare.firebaseio.com/");
    Firebase soundRef = ref.child("sounds");
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Firebase.setAndroidContext(this);

        share = (Button)findViewById(R.id.share);
        artist = (EditText)findViewById(R.id.artist);
        song = (EditText)findViewById(R.id.song);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //make a new Sound, send to firebase
            CharSequence genreErr = "Please choose a genre";
            if(genre.getSelectedItem().toString().equals("Genre"))
                Toast.makeText(getApplicationContext(), "Please choose a genre", Toast.LENGTH_SHORT).show();
            else if(song.getText().toString().length() == 0)
                Toast.makeText(getApplicationContext(), "Please enter a song", Toast.LENGTH_SHORT).show();
            else if(artist.getText().toString().length() == 0)
                Toast.makeText(getApplicationContext(), "Please enter an artist", Toast.LENGTH_SHORT).show();
            else{//everything checks out
                Sound share = new Sound(song.getText().toString(), artist.getText().toString(), genre.getSelectedItem().toString());//change to string values
                HashMap<String, Sound> sounds = new HashMap<String, Sound>();
                sounds.put(new Date().getTime() + "", share);
                soundRef.push().setValue(sounds);//push to FB
                Toast.makeText(getApplicationContext(), "Sound shared!", Toast.LENGTH_SHORT).show();

                artist.setText("");
                song.setText("");

            }


            }
        });
        genre = (Spinner)findViewById(R.id.genre);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.genres, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genre.setAdapter(adapter);
        genre.setPrompt("Genre");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.share, menu);
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
}
