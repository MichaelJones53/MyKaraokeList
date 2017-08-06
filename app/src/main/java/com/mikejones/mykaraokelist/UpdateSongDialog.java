package com.mikejones.mykaraokelist;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
 * Created by MikeJones on 8/4/17.
 */

public class UpdateSongDialog extends DialogFragment {

    private EditText songTitleTextview;
    private EditText artistNameTextview;
    private Button cancelButton;
    private Button okButton;
    private String key;
    private static SongListviewAdapter adapter;

//TODO: add spinner during website scape for lyrics.  seems fast enough currently that i dont need to bother right now


    public UpdateSongDialog(){

    }

    public static UpdateSongDialog newInstance(String key){
        UpdateSongDialog dialog = new UpdateSongDialog();
        Bundle args = new Bundle();
        args.putString("SONG_KEY", key);
        dialog.setArguments(args);
        dialog.setCancelable(false);
        return dialog;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.add_song_dialog_layout, container);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        key = getArguments().getString("SONG_KEY");

        // Get field from view
        songTitleTextview = (EditText) view.findViewById(R.id.song_title_textview);
        artistNameTextview = (EditText) view.findViewById(R.id.artist_name_textview);
        cancelButton = (Button) view.findViewById(R.id.cancel_button);
        okButton = (Button) view.findViewById(R.id.add_button);


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String songTitle = songTitleTextview.getText().toString().trim();
                String artistName = artistNameTextview.getText().toString().trim();

                if(songTitle.equals("")){
                    Toast.makeText(getActivity(), "song title is empty", Toast.LENGTH_SHORT).show();
                } else if(artistName.equals("")){
                    Toast.makeText(getActivity(), "artist is empty", Toast.LENGTH_SHORT).show();
                }else{
                    SongListviewAdapter.UpdateEntry updateEntry = new SongListviewAdapter.UpdateEntry();
                    updateEntry.execute(key, artistName, songTitle);
                }
            }
        });

    }


}