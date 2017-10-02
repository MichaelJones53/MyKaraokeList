package com.mikejones.mykaraokelist;


import android.app.ProgressDialog;
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
 * Created by MikeJones on 7/24/17.
 */

public class AddSongDialog extends DialogFragment {

    private EditText songTitleTextview;
    private EditText artistNameTextview;
    private Button cancelButton;
    private Button okButton;


    /**
     * no argument constructor
     */
    public AddSongDialog(){

    }


    /**
     * static constructor
     * @return  instance of AddSongDialog Statically created
     */
    public static AddSongDialog newInstance(){
        AddSongDialog dialog = new AddSongDialog();
        dialog.setCancelable(false);
        return dialog;

    }

    /**
     * static constructor
     * @param title
     *      title of song
     * @param artist
     *      artist of song
     * @return  instance of AddSongDialog Statically created
     */
    public static AddSongDialog newInstanceWithSong(String title, String artist){
        AddSongDialog dialog = new AddSongDialog();
        Bundle args = new Bundle();
        args.putString("TITLE", title);
        args.putString("ARTIST", artist);
        dialog.setCancelable(false);
        dialog.setArguments(args);
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
        // Get field from view
        songTitleTextview = (EditText) view.findViewById(R.id.song_title_textview);
        artistNameTextview = (EditText) view.findViewById(R.id.artist_name_textview);
        cancelButton = (Button) view.findViewById(R.id.cancel_button);
        okButton = (Button) view.findViewById(R.id.add_button);

        Bundle args = this.getArguments();

        if(args != null){
                songTitleTextview.setText(args.getString("TITLE"));
                artistNameTextview.setText(args.getString("ARTIST"));
        }


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String songTitle = songTitleTextview.getText().toString().trim();
                String artistName = artistNameTextview.getText().toString().trim();

                //look for lyrics


                if(songTitle.equals("")){
                    Toast.makeText(getActivity(), "song title is empty", Toast.LENGTH_SHORT).show();
                } else if(artistName.equals("")){
                    Toast.makeText(getActivity(), "artist is empty", Toast.LENGTH_SHORT).show();
                }else{
                    FindLyrics findLyrics = new FindLyrics(getActivity(), new ProgressDialog(getContext()));
                    findLyrics.execute(songTitle, artistName);
                    dismiss();
                }
            }
        });

    }



    public interface AddSongDialogListener {
        /**
         * listener called when new song is created
         * @param song
         *      song that was created
         */
        public void onReturnNewSong(Song song);
    }



}
