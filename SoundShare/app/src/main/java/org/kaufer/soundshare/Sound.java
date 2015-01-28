package org.kaufer.soundshare;

/**
 * Created by matth_000 on 1/28/2015.
 */
public class Sound {

    private String song, artist, genre;

    private Sound(){

    }

    Sound(String s, String a, String g){//song, artist, genre
        song = s;
        artist = a;
        genre = g;
    }

    public String getSong(){
        return song;
    }

    public String getArtist(){
        return artist;
    }

    public String getGenre(){
        return genre;
    }
}
