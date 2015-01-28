package org.kaufer.soundshare;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Query;

/**
 * @author greg
 * @since 6/21/13
 *
 * This class is an example of how to use FirebaseListAdapter. It uses the <code>Chat</code> class to encapsulate the
 * data for each individual chat message
 */
public class SoundShareListAdapter extends FirebaseListAdapter<Sound> {

    public SoundShareListAdapter(Query ref, Activity activity, int layout) {
        super(ref, Sound.class, layout, activity);
    }

    @Override
    protected void populateView(View view, Sound sound) {

        // Map a Chat object to an entry in our listview

        String song = sound.getSong();
        TextView songText = (TextView) view.findViewById(R.id.song);
        songText.setText(song);

        String artist = sound.getArtist();
        String genre = sound.getGenre();

        TextView artistText = (TextView) view.findViewById(R.id.artist);
        artistText.setText(artist + " (" + genre + ")");

//        TextView genreText = (TextView) view.findViewById(R.id.genre);
//        genreText.setText(genre + "");
    }

}
