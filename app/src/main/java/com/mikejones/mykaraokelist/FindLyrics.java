package com.mikejones.mykaraokelist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.util.Log;

/**
 * Created by MikeJones on 8/4/17.
 */

public class FindLyrics extends AsyncTask<String, Void, String> {
    public static final String TAG = "FindLyrics";
    private String artist;
    private String songName;
    private String lyrics = null;
    private Activity activity = null;
    private ProgressDialog dialog;

    //TODO: add spinner during website scape for lyrics.  seems fast enough currently that i dont need to bother right now

    public FindLyrics(Activity activity, ProgressDialog dialog){
        this.activity = activity;
        this.dialog = dialog;
        dialog.setCancelable(false);
        dialog.setMessage("Searching for lyrics");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
    }
    @Override
    protected String doInBackground(String... params) {


        songName = params[0];
        artist = params[1];

        Log.d(TAG, "artist: "+artist+"  title: "+songName);
        lyrics = AZLyrics.fromMetaData(artist, songName);
        return lyrics;
    }

    @Override
    protected void onPostExecute(String lyrics) {
        // TODO: do something with the feed

        AddSongDialog.AddSongDialogListener act = (AddSongDialog.AddSongDialogListener) this.activity;
        Song song = new Song(songName, artist);

        song.setLyrics(lyrics);


     //   Log.d("AddSongDialog", lyrics+"");
        act.onReturnNewSong(song);
        dialog.dismiss();

        activity = null;
        act = null;

        Log.d(TAG, "dialog dismissed from findlyrics");
        if(dialog == null){
            Log.d(TAG, "dialog is null from findlyrics");
        }
    }
}