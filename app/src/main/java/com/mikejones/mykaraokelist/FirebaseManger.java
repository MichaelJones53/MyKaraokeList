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

    public DatabaseReference getSongListReference(){
        return songlistReference;
    }

    public void addSong(Song song){
        songlistReference.push().setValue(song);

    }
}
