package com.mikejones.mykaraokelist;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;

/**
 * Created by MikeJones on 7/24/17.
 */

public class AddSongDialog extends DialogFragment {

    private EditText songTitleTextview;
    private EditText artistNameTextview;
    private Button cancelButton;
    private Button okButton;
    private Song newSong = null;
    private OnCompleteListener mListener;


    public AddSongDialog(){

    }

    public static AddSongDialog newInstance(){
        AddSongDialog dialog = new AddSongDialog();
        dialog.setCancelable(false);
        return dialog;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
                    AddSongDialogListener activity = (AddSongDialogListener) getActivity();
                    activity.onReturnNewSong(new Song(songTitle, artistName));

                }
            }
        });

    }



    public interface AddSongDialogListener {
        public void onReturnNewSong(Song song);
    }


}
