package com.mikejones.mykaraokelist;

import android.app.ProgressDialog;
import android.content.Context;
import android.icu.lang.UScript;
import android.os.AsyncTask;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.google.firebase.database.Query;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by MikeJones on 7/23/17.
 */

public class SongListviewAdapter extends RecyclerView.Adapter<SongListviewAdapter.SongViewHolder> implements ItemTouchHelperAdapter{

    public static final String TAG = "SongListViewAdapter";

    private Context context;
    private static DatabaseReference databaseRef;
    private ChildEventListener childEventListener;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private static DialogFragment dialog;


    private List<String> idList = new ArrayList<>();
    private List<Song> songList = new ArrayList<>();
    private boolean skipUpdate = false;
    private int index = 0;

    public SongListviewAdapter(@NonNull final Context context, DatabaseReference ref) {

        Log.d(TAG, "constructor called");
        this.context = context;
        databaseRef = ref;
        final boolean isMoved = false;

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Log.d(TAG, "onChildAdded called: "+ dataSnapshot.getKey()+"    value: "+dataSnapshot.getPriority());

                Song newSong = dataSnapshot.getValue(Song.class);

                songList.add(newSong);
                idList.add(dataSnapshot.getKey());


                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(dataSnapshot.getKey()+"/index", index);
                databaseRef.updateChildren(childUpdates);

                index++;

                notifyItemInserted(songList.size()-1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {



                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A song has been changed, use the key to determine if we are displaying this
                // song and if so display the changed song.
                Song newSong = dataSnapshot.getValue(Song.class);
                String songKey = dataSnapshot.getKey();

                if(!skipUpdate) {
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
                skipUpdate = true;
                Song movedSong = dataSnapshot.getValue(Song.class);
                String songKey = dataSnapshot.getKey();
                Log.w(TAG, "onChildMoved:" + songKey);
                // A song has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                int indexA = idList.indexOf(dataSnapshot.getKey());
                int indexB = idList.indexOf(s);

                Log.d(TAG, "indexA: "+ indexA+ "   Key: "+dataSnapshot.getKey());
                Log.d(TAG, "indexB: "+ indexA+ "   Key: "+s);

                if(indexB == -1){


                }else{

                    Collections.swap(idList, indexA, indexB);
                    Collections.swap(songList, indexA, indexB);
                }


                // ...




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                Toast.makeText(context, "Failed to load song.",
                        Toast.LENGTH_SHORT).show();
            }
        };

        Query items = databaseRef.orderByChild("index");
        items.addChildEventListener(childEventListener);



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

        if(song.getLyrics() == null){
            Log.d(TAG, song.getTitle()+" is null");
            holder.lyricsButton.setVisibility(View.INVISIBLE);
            holder.lyricsButton.setEnabled(false);
        }else{
            holder.lyricsButton.setVisibility(View.VISIBLE);
            holder.lyricsButton.setEnabled(true);
        }




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



                    System.out.println("idList: "+idList.size()+  "   position: "+position);
                    databaseRef.child(idList.get(position)).removeValue();
                    notifyItemRemoved(position);





            }
        });

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    showUpdateSongDialog(idList.get(position));
                    holder.swipeLayout.close(true);

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



    @Override
    public void onItemMove(int fromPosition, int toPosition) {

        Log.d(TAG, "initial onItemMove called: from: "+fromPosition+"  toPosition"+toPosition);

        databaseRef.child(idList.get(fromPosition)).child("index").setValue(toPosition);
        databaseRef.child(idList.get(toPosition)).child("index").setValue(fromPosition);

//        if (fromPosition < toPosition) {
//            for (int i = fromPosition; i < toPosition; i++) {
//
//
//
//                Collections.swap(idList, i, i + 1);
//                Collections.swap(songList, i, i + 1);
//
//            }
//        } else {
//            for (int i = fromPosition; i > toPosition; i--) {
//
//                Collections.swap(idList, i, i - 1);
//                Collections.swap(songList, i, i - 1);
//            }
//        }
        notifyItemMoved(fromPosition, toPosition);

    }

    @Override
    public void onItemDismiss(int position) {

    }








    public static class SongViewHolder extends RecyclerView.ViewHolder
    {
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


        }

    }


    public static class UpdateEntry extends AsyncTask<String, Void, String>{


        private String artist;
        private String songName;
        private String lyrics = null;
        private String key;
        private ProgressDialog loadingDialog;

        public UpdateEntry(ProgressDialog dialog){
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

            System.out.print("key: "+key+ "   artist: "+artist+"  title: "+songName);
            lyrics = AZLyrics.fromMetaData(songName, artist);

            return lyrics;
        }

        @Override
        protected void onPostExecute(String lyrics) {
            // TODO: do something with the feed


            Song song = new Song(songName, artist);
            song.setLyrics(lyrics);
            loadingDialog.dismiss();
            databaseRef.child(key).setValue(song).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    dialog.dismiss();
                }
            });




        }
    }

}
