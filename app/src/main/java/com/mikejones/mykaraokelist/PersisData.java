package com.mikejones.mykaraokelist;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by MikeJones on 9/14/17.
 */

public class PersisData extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

}