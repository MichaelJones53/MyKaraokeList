package com.mikejones.mykaraokelist;

import android.content.Context;
import android.icu.lang.UScript;
import android.os.AsyncTask;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by MikeJones on 7/23/17.
 */

public class SongListviewAdapter extends RecyclerView.Adapter<SongListviewAdapter.SongViewHolder>{

    public static final String TAG = "SongListViewAdapter";

    private Context context;
    private static DatabaseReference databaseRef;
    private ChildEventListener childEventListener;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private static DialogFragment dialog;


    private List<String> idList = new ArrayList<>();
    private List<Song> songList = new ArrayList<>();

    public SongListviewAdapter(@NonNull final Context context, DatabaseReference ref) {

        Log.d(TAG, "constructor called");
        this.context = context;
        databaseRef = ref;


        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Log.d(TAG, "onChildAdded called: "+ dataSnapshot.getKey());

                    Song newSong = dataSnapshot.getValue(Song.class);

                    songList.add(newSong);
                    idList.add(dataSnapshot.getKey());


                notifyItemInserted(songList.size()-1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {



                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A song has been changed, use the key to determine if we are displaying this
                // song and if so display the changed song.
                Song newSong = dataSnapshot.getValue(Song.class);
                String songKey = dataSnapshot.getKey();

                // [START_EXCLUDE]
                int songIndex = idList.indexOf(songKey);
                if (songIndex > -1) {
                    // Replace with the new data
                    songList.set(songIndex, newSong);

                    // Update the RecyclerView
                    notifyItemChanged(songIndex);
                } else {
                    Log.w(TAG, "onChildChanged:unknown_child:" + songKey);
                }
                // [END_EXCLUDE]
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A song has been removed, use the key to determine if we are displaying this
                // song and if so remove it.
                String songKey = dataSnapshot.getKey();

                // [START_EXCLUDE]
                int songIndex = idList.indexOf(songKey);
                if (songIndex > -1) {
                    // Remove data from the list
                    idList.remove(songIndex);
                    songList.remove(songIndex);


                    // Update the RecyclerView
                    notifyItemRemoved(songIndex);
                    notifyItemRangeChanged(songIndex, getItemCount()-songIndex);
                } else {
                    Log.w(TAG, "onChildRemoved:unknown_child:" + songKey);
                }
                // [END_EXCLUDE]
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                // A song has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                Song movedSong = dataSnapshot.getValue(Song.class);
                String songKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                Toast.makeText(context, "Failed to load song.",
                        Toast.LENGTH_SHORT).show();
            }
        };

        ref.addChildEventListener(childEventListener);



    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        Log.d(TAG, "onCreateView called");
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_item, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SongViewHolder holder, final int position) {

        Log.d(TAG, "onBindViewHolder called.  position: " +position);
        final Song song = songList.get(position);
        holder.songTitleTextView.setText(song.getTitle());
        holder.artistTextView.setText(song.getArtist());



        holder.lyricsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = ((ListActivity) context).getSupportFragmentManager();
                LyricDialog dialog = LyricDialog.newInstance(song);
                dialog.show(fm, "something");


            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(holder.swipeLayout.isOpened()) {
                    System.out.println("idList: "+idList.size()+  "   position: "+position);
//                    idList.remove(position);
//                    songList.remove(position);
                    databaseRef.child(idList.get(position)).removeValue();
                    notifyItemRemoved(position);




                }
            }
        });

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.swipeLayout.isOpened()) {
                    showUpdateSongDialog(idList.get(position));
                    holder.swipeLayout.close(true);
                }
            }
        });

        viewBinderHelper.bind(holder.swipeLayout, idList.get(position));
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount called");
        return songList.size();
    }

    public void cleanupListener() {
        if (childEventListener != null) {
            databaseRef.removeEventListener(childEventListener);
        }
    }




    private void showUpdateSongDialog(String key) {
        FragmentManager fm = ((FragmentActivity)context).getSupportFragmentManager();
        dialog = UpdateSongDialog.newInstance(key);
        dialog.show(fm, "fragment_edit_name");
    }



    public static class SongViewHolder extends RecyclerView.ViewHolder
    {
        public static final String TAG = "SongViewHolder";
        private SwipeRevealLayout swipeLayout;
        private View deleteLayout;

        TextView songTitleTextView;
        TextView artistTextView;
        ImageButton lyricsButton;
        ImageView editButton;
        ImageView deleteButton;

        public SongViewHolder(View itemView) {
            super(itemView);

            Log.d(TAG, "constructor called");

            swipeLayout = (SwipeRevealLayout) itemView.findViewById(R.id.swipe_layout);
            deleteLayout = itemView.findViewById(R.id.delete_layout);


            songTitleTextView = (TextView) itemView.findViewById(R.id.songTitleTextView);
            artistTextView = (TextView) itemView.findViewById(R.id.artistTextView);
            lyricsButton = (ImageButton) itemView.findViewById(R.id.lyrics_button);

            editButton = (ImageView) itemView.findViewById(R.id.editImageView);
            deleteButton = (ImageView) itemView.findViewById(R.id.deleteImageView);


        }

    }


    public static class UpdateEntry extends AsyncTask<String, Void, String>{


        private String artist;
        private String songName;
        private String lyrics = null;
        private String key;



        @Override
        protected String doInBackground(String... params) {
            key = params[0];
            artist = params[1];
            songName = params[2];

            System.out.print("key: "+key+ "   artist: "+artist+"  title: "+songName);
            lyrics = AZLyrics.fromMetaData(songName, artist);

            return lyrics;
        }

        @Override
        protected void onPostExecute(String lyrics) {
            // TODO: do something with the feed

            Song song = new Song(artist, songName);
            System.out.println("lyrics: "+lyrics);
            song.setLyrics(lyrics);
            databaseRef.child(key).setValue(song).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    dialog.dismiss();
                }
            });


        }
    }

}
