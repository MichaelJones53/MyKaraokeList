package com.mikejones.mykaraokelist;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by MikeJones on 7/26/17.
 */

public class FirebaseManger {
    public static final String TAG = "FirebaseManager";

    private DatabaseReference firebaseDatabase;
    private DatabaseReference songlistReference;
    private String currentUID = null;

    public FirebaseManger(){

        firebaseDatabase = DatabaseUtils.getDatabase().getReference();
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            songlistReference = firebaseDatabase.child("users/"+currentUID+"/songList");
        }

    }

    public DatabaseReference getUserDatabaseReference(){
        return firebaseDatabase.child("users").child(currentUID);
    }

    public DatabaseReference getSongListReference(){
        return songlistReference;
    }

    public void addSong(Song song){
        songlistReference.push().setValue(song);


    }

    public void removeSong(String ref){

        songlistReference.child(ref).removeValue();
    }

    public void writeNewUser(User user){

        firebaseDatabase.child("users").child(user.getUid()).setValue(user);

    }

    public void setSongIndex(String ref, int index){
      //  Log.d(TAG, "setting index value");
        songlistReference.child(ref).child("index").setValue(index);

    }

    public ArrayList<Song> getUserSongs(){
        return null;
    }

    public String getUID(){
        return currentUID;
    }
}
