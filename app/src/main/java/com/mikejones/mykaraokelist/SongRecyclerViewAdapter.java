package com.mikejones.mykaraokelist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import android.os.AsyncTask;

import android.support.annotation.NonNull;

import android.support.v4.app.FragmentManager;

import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by MikeJones on 7/23/17.
 */

public class SongRecyclerViewAdapter extends RecyclerView.Adapter<SongRecyclerViewAdapter.SongViewHolder> implements ItemTouchHelperAdapter {

    public static final String TAG = "SongListViewAdapter";

    private Context context;
    private FragmentManager fm;
    private ChildEventListener childEventListener;
    private static boolean isUpdate = true;
    private static DatabaseReference databaseRef;

    private static List<String> idList = new ArrayList<>();
    private static List<Song> songList = new ArrayList<>();





    public SongRecyclerViewAdapter(@NonNull Context context) {

        Log.d(TAG, "SongListViewAdapter constructor called");
        this.context = context;
        fm = ((ListActivity) context).getSupportFragmentManager();
        databaseRef = DatabaseUtils.getDatabase().getReference().child("users/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/songList");

        addListener();
    }




    public void deleteSong(int position){

        if(position >-1){
            idList.remove(position);
            songList.remove(position);
            Log.d(TAG, "REMOVED:    idSize: "+idList.size()+"   songSIze: "+songList.size());
            notifyItemRemoved(position);
           // notifyItemRangeChanged(position, idList.size() - position);


        }
    }




    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount called");
        return idList.size();
    }



    public static String getListKey(int pos){
        return idList.get(pos);
    }

    public static int getPositionFromKey(String key){
        return idList.indexOf(key);
    }



    public static Song getSongFromKey(String key){
        if(idList.contains(key)){
            return songList.get(idList.indexOf(key));
        }else{
            return null;
        }
    }

    public void addListener(){

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Log.d(TAG, "onChildAdded called: " + dataSnapshot.getKey() + "    value: " + dataSnapshot.getPriority());

                Song newSong = dataSnapshot.getValue(Song.class);

                if(!idList.contains(dataSnapshot.getKey())){
                    songList.add(newSong);
                    idList.add(dataSnapshot.getKey());

                    notifyItemInserted(songList.size() - 1);
                    ListActivity.getRecyclerView().smoothScrollToPosition(idList.size()-1);
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
                Log.d(TAG, "onChildChanged other string:" + s);

                // A song has been changed, use the key to determine if we are displaying this
                // song and if so display the changed song.
                Song newSong = dataSnapshot.getValue(Song.class);
                String songKey = dataSnapshot.getKey();


                if(isUpdate){
                    Log.d(TAG, "onChildChanged isUpdate called");
                    // [START_EXCLUDE]
                    int songIndex = idList.indexOf(songKey);
                    if (songIndex > -1) {
                        // Replace with the new data
                        songList.set(songIndex, newSong);

                        // Update the RecyclerView


                        notifyItemChanged(songIndex);


                    }
                        //Log.w(TAG, "onChildChanged:unknown_child:" + songKey);

                        // [END_EXCLUDE]

                }
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
                    notifyItemRangeChanged(songIndex, getItemCount() - songIndex);
                } else {
                    Log.w(TAG, "onChildRemoved:unknown_child:" + songKey);
                }
                // [END_EXCLUDE]
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                Log.w(TAG, "onChildMoved:" + dataSnapshot.getKey());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
            }
        };

        databaseRef.addChildEventListener(childEventListener);


    }



    @Override
    public void onItemMove(int fromPosition, int toPosition) {

        isUpdate = false;
        Log.d(TAG, "initial onItemMove called: from: " + fromPosition + "  toPosition: " + toPosition);

        Map<String, Object> childUpdates = new HashMap<>();


        if((fromPosition-toPosition)<-1){
            Log.d(TAG, "swaping down test: from: "+fromPosition+"  to:"+toPosition);
        }else if((fromPosition - toPosition) > 1) {
            Log.d(TAG, "swaping up test: from: "+fromPosition+"  to:"+toPosition);
        }

        if(fromPosition < toPosition){
            Log.d(TAG, "swappingtest 1");
            for(int i = fromPosition; i < toPosition; i++){
                Collections.swap(songList, i, i+1);
                childUpdates.put(idList.get(i), songList.get(i));
            }
            childUpdates.put(idList.get(toPosition), songList.get(toPosition));

        }else{
            Log.d(TAG, "swappingtest 2");
            for(int i = fromPosition; i > toPosition; i--){

                Collections.swap(songList, i, i-1);
                childUpdates.put(idList.get(i), songList.get(i));
            }
            childUpdates.put(idList.get(toPosition), songList.get(toPosition));

        }
        databaseRef.updateChildren(childUpdates);


       notifyItemMoved(fromPosition, toPosition);

    }

    public void cleanupListener(){
        if(childEventListener != null){
            databaseRef.removeEventListener(childEventListener);
            childEventListener = null;
        }

    }


    @Override
    public void onItemDismiss(int position) {
        Log.d(TAG, "onItemDismiss called: "+position);

        databaseRef.child(idList.get(position)).removeValue();

    }

    @Override
    public SongRecyclerViewAdapter.SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);

        Log.d(TAG, "onCreateViewHolder called");
        View songView = inflater.inflate(R.layout.song_list_item, parent, false);

        SongViewHolder viewHolder = new SongViewHolder(songView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {

        Song song = songList.get(position);

        Log.d(TAG, "onBindViewHolder called: "+position);
        Log.d(TAG, song.getLyrics()+"");

        holder.artistTextView.setText(song.getArtist());
        holder.titleTextView.setText(song.getTitle());

        if(song.getLyrics() != null){
            holder.lyricsButton.setVisibility(View.VISIBLE);
            holder.lyricsButton.setEnabled(true);
        }else{
            holder.lyricsButton.setVisibility(View.INVISIBLE);
            holder.lyricsButton.setEnabled(false);
        }
    }


    public class SongViewHolder extends RecyclerView.ViewHolder{

        public TextView artistTextView;
        public TextView titleTextView;
        public ImageButton lyricsButton;
        public Song song;

        public SongViewHolder(View itemView) {
            super(itemView);

            Log.d(TAG, "SongViewHolder created");

            artistTextView = (TextView) itemView.findViewById(R.id.artistTextView);
            titleTextView = (TextView) itemView.findViewById(R.id.songTitleTextView);
            lyricsButton = (ImageButton) itemView.findViewById(R.id.lyrics_button);

            lyricsButton.setImageBitmap(ImageUtil.decodeSampledBitmapFromResource(context.getResources(), R.drawable.microphone_angled, 60, 60));

            lyricsButton.setEnabled(false);
            lyricsButton.setVisibility(View.INVISIBLE);

            lyricsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //display lyrics
                    LyricDialog lyricDialog = LyricDialog.newInstance(songList.get(getAdapterPosition()));
                    lyricDialog.show(fm, "something");


                }
            });



        }



    }






    public static class UpdateEntry extends AsyncTask<String, Void, String> {


        private String artist;
        private String songName;
        private String lyrics = null;
        private String key;
        private ProgressDialog loadingDialog;

        public UpdateEntry(ProgressDialog dialog) {

            loadingDialog = dialog;
            loadingDialog.setCancelable(false);
            loadingDialog.setMessage("Searching for lyrics");
            loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loadingDialog.show();

            isUpdate = true;
        }


        @Override
        protected String doInBackground(String... params) {
            key = params[0];
            artist = params[1];
            songName = params[2];

            System.out.print("key: " + key + "   artist: " + artist + "  title: " + songName);
            lyrics = AZLyrics.fromMetaData(artist, songName);

            return lyrics;
        }

        @Override
        protected void onPostExecute(String lyrics) {
            // TODO: do something with the feed


            Log.d(TAG, "onPostExecute called");
            Log.d(TAG, "lyrics: "+lyrics);



            Song song = new Song(songName, artist);
            song.setLyrics(lyrics);
            Log.d(TAG, ""+lyrics);
            Map<String, Object> childUpdate = new HashMap<>();
            childUpdate.put(key, song);


            databaseRef.updateChildren(childUpdate);
            loadingDialog.dismiss();



        }
    }



}
