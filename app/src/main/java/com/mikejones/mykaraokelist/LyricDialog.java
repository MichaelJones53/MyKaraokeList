package com.mikejones.mykaraokelist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by MikeJones on 7/27/17.
 */

public class LyricDialog extends DialogFragment {

    private EditText songTitleTextview;
    private EditText artistNameTextview;
    private EditText lyricsTextView;

    public static final String SONG_TITLE = "title";
    public static final String SONG_ARTIST = "artist";
    public static final String SONG_LYRICS = "lyrics";

    public LyricDialog(){

    }

    public static LyricDialog newInstance(Song song){
        LyricDialog dialog = new LyricDialog();
        Bundle args = new Bundle();
        args.putString(SONG_TITLE, song.getTitle());
        args.putString(SONG_ARTIST, song.getArtist());
        args.putString(SONG_LYRICS, song.getLyrics());
        dialog.setCancelable(true);
        dialog.setArguments(args);
        return dialog;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lyrics_dialog_layout, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = this.getArguments();

        // Get field from view
        songTitleTextview = (EditText) view.findViewById(R.id.lyric_song_title_textview);
        artistNameTextview = (EditText) view.findViewById(R.id.lyric_artist_textview);
        lyricsTextView = (EditText) view.findViewById(R.id.lyric_textview);


        if(args != null){
            songTitleTextview.setText(args.getString(SONG_TITLE));
            artistNameTextview.setText(args.getString(SONG_ARTIST));
            lyricsTextView.setText(args.getString(SONG_LYRICS));
        }





    }
}
