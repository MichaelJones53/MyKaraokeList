package com.mikejones.mykaraokelist;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by MikeJones on 7/26/17.
 */

public class DatabaseUtils {
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            //mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }

    public static void signOut(){
        FirebaseAuth.getInstance().signOut();
    }

}