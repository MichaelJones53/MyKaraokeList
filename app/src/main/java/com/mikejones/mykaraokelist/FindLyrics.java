package com.mikejones.mykaraokelist;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by MikeJones on 8/4/17.
 */

public class FindLyrics extends AsyncTask<String, Void, String> {
    private String artist;
    private String songName;
    private String lyrics = null;
    private Activity activity = null;
    //TODO: add spinner during website scape for lyrics.  seems fast enough currently that i dont need to bother right now

    public FindLyrics(Activity activity){
        this.activity = activity;
    }
    @Override
    protected String doInBackground(String... params) {
        artist = params[0];
        songName = params[1];
        lyrics = AZLyrics.fromMetaData(artist, songName);
        return lyrics;
    }

    @Override
    protected void onPostExecute(String lyrics) {
        // TODO: do something with the feed

        AddSongDialog.AddSongDialogListener activity = (AddSongDialog.AddSongDialogListener) this.activity;
        Song song = new Song(songName, artist);
        song.setLyrics(lyrics+"");
        Log.d("AddSongDialog", lyrics+"");
        activity.onReturnNewSong(song);


    }
}