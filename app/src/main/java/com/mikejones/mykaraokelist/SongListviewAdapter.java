package com.mikejones.mykaraokelist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import android.os.AsyncTask;

import android.support.annotation.NonNull;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by MikeJones on 7/23/17.
 */

public class SongListviewAdapter extends RecyclerView.Adapter<SongListviewAdapter.SongViewHolder> implements ItemTouchHelperAdapter {

    public static final String TAG = "SongListViewAdapter";

    private Context context;
    private static DatabaseReference databaseRef;
    private ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private static FragmentManager fm;
    private static List<String> idList = new ArrayList<>();
    private static List<Song> songList = new ArrayList<>();





    public SongListviewAdapter(@NonNull Context context) {

        Log.d(TAG, "SongListViewAdapter constructor called");
        this.context = context;
       databaseRef = DatabaseUtils.getDatabase().getReference().child("users/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/songList");
        fm = ((FragmentActivity) context).getSupportFragmentManager();

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    // TODO: handle the post
                    String key = postSnapshot.getKey();
                    Song song = postSnapshot.getValue(Song.class);

                    if(idList.indexOf(key) == -1){
                        idList.add(key);
                        songList.add(song);
                        Log.d(TAG, "ADDED key: "+key+"  song: " + song.getTitle());
                        notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



//
//    public void addListener(){
//
//        childEventListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//                Log.d(TAG, "onChildAdded called: " + dataSnapshot.getKey() + "    value: " + dataSnapshot.getPriority());
//
//                Song newSong = dataSnapshot.getValue(Song.class);
//
//                songList.add(newSong);
//                idList.add(dataSnapshot.getKey());
//
//                notifyItemInserted(songList.size() - 1);
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//
//                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
//                Log.d(TAG, "onChildChanged other string:" + s);
//
//                // A song has been changed, use the key to determine if we are displaying this
//                // song and if so display the changed song.
//                Song newSong = dataSnapshot.getValue(Song.class);
//                String songKey = dataSnapshot.getKey();
//
//                if (isUpdate) {
//                    Log.d(TAG, "onChildChanged isUpdate called");
//                    // [START_EXCLUDE]
//                    int songIndex = idList.indexOf(songKey);
//                    if (songIndex > -1) {
//                        // Replace with the new data
//                        songList.set(songIndex, newSong);
//
//                        // Update the RecyclerView
//
//
//                        notifyItemChanged(songIndex);
//
//
//                    } else {
//                        Log.w(TAG, "onChildChanged:unknown_child:" + songKey);
//
//                        // [END_EXCLUDE]
//                    }
//                }
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
//
//                // A song has been removed, use the key to determine if we are displaying this
//                // song and if so remove it.
//                String songKey = dataSnapshot.getKey();
//
//                // [START_EXCLUDE]
//                int songIndex = idList.indexOf(songKey);
//                if (songIndex > -1) {
//                    // Remove data from the list
//                    idList.remove(songIndex);
//                    songList.remove(songIndex);
//
//
//                    // Update the RecyclerView
//                    notifyItemRemoved(songIndex);
//                    notifyItemRangeChanged(songIndex, getItemCount() - songIndex);
//                } else {
//                    Log.w(TAG, "onChildRemoved:unknown_child:" + songKey);
//                }
//                // [END_EXCLUDE]
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                Log.w(TAG, "onChildMoved:" + dataSnapshot.getKey());
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
//            }
//        };
//
//        databaseRef.addChildEventListener(childEventListener);
//
//
//    }


    public void addSong(Song song){
        String key  = databaseRef.push().getKey();
        songList.add(song);
        idList.add(key);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, song);
        databaseRef.updateChildren(childUpdates);

        notifyItemInserted(songList.size() - 1);

    }

    public void updateSong(String key, Song song){
        int songIndex = idList.indexOf(key);

        songList.set(songIndex, song);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, song);
        databaseRef.updateChildren(childUpdates);

        notifyItemChanged(songIndex);

    }
    

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount called");
        return songList.size();
    }


    public void cleanupListener() {

    }





    @Override
    public void onItemMove(int fromPosition, int toPosition) {

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


    @Override
    public void onItemDismiss(int position) {

    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {

        Log.d(TAG, "onBindViewHolder called.  position: " + position);
//        final Song song = songList.get(position);
//        holder.songTitleTextView.setText(song.getTitle());
//        holder.artistTextView.setText(song.getArtist());

//        if (song.getLyrics() == null) {
//            Log.d(TAG, song.getTitle() + " is null");
//            holder.lyricsButton.setVisibility(View.INVISIBLE);
//            holder.lyricsButton.setEnabled(false);
//        } else {
//            holder.lyricsButton.setVisibility(View.VISIBLE);
//            holder.lyricsButton.setEnabled(true);
//        }


//        holder.lyricsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentManager fm = ((ListActivity) context).getSupportFragmentManager();
//                LyricDialog lyricDialog = LyricDialog.newInstance(song);
//                lyricDialog.show(fm, "something");
//
//
//            }
//        });
//
//
//        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                System.out.println("idList: " + idList.size() + "   position: " holder.getAdapterPosition());
//                databaseRef.child(idList.get(holder.getAdapterPosition())).removeValue();
//                holder.swipeLayout.close(true);
//                notifyItemRemoved(holder.getAdapterPosition());
//
//            }
//        });
//
//        holder.editButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                showUpdateSongDialog(idList.get(holder.getAdapterPosition()));
//                holder.swipeLayout.close(true);
//
//            }
//        });


        final Song song = songList.get(holder.getAdapterPosition());

        holder.songTitleTextView.setText(song.getTitle());
        holder.artistTextView.setText(song.getArtist());

        if (song.getLyrics() != null) {
            Log.d(TAG, song.getTitle() + " is null");
            holder.lyricsButton.setVisibility(View.VISIBLE);
            holder.lyricsButton.setEnabled(true);
        }

        holder.lyricsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LyricDialog lyricDialog = LyricDialog.newInstance(song);
                lyricDialog.show(fm, "something");


            }
        });

        viewBinderHelper.bind(holder.swipeLayout, songList.get(position).getTitle());
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        Log.d(TAG, "onCreateView called");
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_item, parent, false);


        return new SongViewHolder(view);

    }

    public class SongViewHolder extends RecyclerView.ViewHolder {
        public static final String TAG = "SongViewHolder";
        private SwipeRevealLayout swipeLayout;

        TextView songTitleTextView;
        TextView artistTextView;
        ImageButton lyricsButton;
        ImageView editButton;
        ImageView deleteButton;



        public SongViewHolder(View itemView) {
            super(itemView);

            Log.d(TAG, "constructor called");

            swipeLayout = (SwipeRevealLayout) itemView.findViewById(R.id.swipe_layout);


            swipeLayout.setSwipeListener(new SwipeRevealLayout.SwipeListener() {
                @Override
                public void onClosed(SwipeRevealLayout view) {
                    deleteButton.setEnabled(false);
                    editButton.setEnabled(false);
                }

                @Override
                public void onOpened(SwipeRevealLayout view) {

                    deleteButton.setEnabled(true);
                    editButton.setEnabled(true);
                }

                @Override
                public void onSlide(SwipeRevealLayout view, float slideOffset) {
                    deleteButton.setEnabled(false);
                    editButton.setEnabled(false);
                }
            });

            songTitleTextView = (TextView) itemView.findViewById(R.id.songTitleTextView);
            artistTextView = (TextView) itemView.findViewById(R.id.artistTextView);
            lyricsButton = (ImageButton) itemView.findViewById(R.id.lyrics_button);

            editButton = (ImageView) itemView.findViewById(R.id.editImageView);
            deleteButton = (ImageView) itemView.findViewById(R.id.deleteImageView);


            deleteButton.setEnabled(false);
            editButton.setEnabled(false);

            lyricsButton.setVisibility(View.INVISIBLE);
            lyricsButton.setEnabled(false);


            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    System.out.println("idList: " + idList.size() + "   position: " +getAdapterPosition());
                    databaseRef.child(idList.get(getAdapterPosition())).removeValue();
                    songList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    swipeLayout.close(true);


                }
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    UpdateSongDialog updateDialog = UpdateSongDialog.newInstance(idList.get(getAdapterPosition()));
                    updateDialog.show(fm, "fragment_edit_name");
                    swipeLayout.close(true);

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
        private Activity activity;

        public UpdateEntry(Activity activity, ProgressDialog dialog) {
            this.activity = activity;
            loadingDialog = dialog;
            loadingDialog.setCancelable(false);
            loadingDialog.setMessage("Searching for lyrics");
            loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loadingDialog.show();
        }


        @Override
        protected String doInBackground(String... params) {
            key = params[0];
            artist = params[1];
            songName = params[2];

            System.out.print("key: " + key + "   artist: " + artist + "  title: " + songName);
            lyrics = AZLyrics.fromMetaData(songName, artist);

            return lyrics;
        }

        @Override
        protected void onPostExecute(String lyrics) {
            // TODO: do something with the feed

            UpdateSongDialog.UpdateSongDialogListener act = (UpdateSongDialog.UpdateSongDialogListener) activity;



            Song song = new Song(songName, artist);
            song.setLyrics(lyrics);
            act.onReturnUpdatedSong(key, song);

            loadingDialog.dismiss();
            activity = null;
            act = null;


        }
    }



}
