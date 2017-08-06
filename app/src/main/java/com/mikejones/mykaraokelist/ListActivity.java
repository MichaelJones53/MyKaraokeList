package com.mikejones.mykaraokelist;



import android.content.Intent;


import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.FrameLayout;



import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


public class ListActivity extends AppCompatActivity implements AddSongDialog.AddSongDialogListener{
    public static final String TAG = "ListActivity";
    public static final int ITEM_SPACING = 40;

    private RecyclerView songListView;
    private SongListviewAdapter songListviewAdapter;
    private Toolbar toolbar;
    private FloatingActionButton mainFAB;
    private FloatingActionButton manualEntryFAB;
    private FloatingActionButton audioEntryFAB;
    private Animation showManualFABAnimation;
    private Animation showAudioFABAnimation;
    private Animation hideManualFABAnimation;
    private Animation hideAudioFABAnimation;
    private Animation rotateForwardMainFABAnimation;
    private Animation rotateBackwardMainFABAnimation;
    private DialogFragment dialog;
    private FirebaseManger db;




    private boolean buttonsShown = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        initalizeDatabase();
        initalizeAnimations();
        initalizeViews();


    }

    private void initalizeDatabase(){
        db = new FirebaseManger();
    }


    private void initalizeViews(){
        toolbar = (Toolbar) findViewById(R.id.bottomToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        songListView = (RecyclerView) findViewById(R.id.songListView);

        mainFAB = (FloatingActionButton) findViewById(R.id.mainFloatingActionButton);
        manualEntryFAB = (FloatingActionButton) findViewById(R.id.manualEntryFAB);
        audioEntryFAB = (FloatingActionButton) findViewById(R.id.audioEntryFAB);


        mainFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(buttonsShown){

                    hideFABs();
                    mainFAB.startAnimation(rotateBackwardMainFABAnimation);
                    buttonsShown = false;
                }else{
                    showFABs();
                    mainFAB.startAnimation(rotateForwardMainFABAnimation);
                    buttonsShown = true;
                }

            }
        });


        manualEntryFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show dialog box for user to enter song and artist name
                showNewSongDialog();
                if(buttonsShown){

                    hideFABs();
                    mainFAB.startAnimation(rotateBackwardMainFABAnimation);
                    buttonsShown = false;
                }

            }
        });

        audioEntryFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAudioFingerprintDialog();


            }
        });


    }

    private void initalizeAnimations(){
        rotateForwardMainFABAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_forward_main_fab);
        rotateBackwardMainFABAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_reverse_main_fab);

        hideManualFABAnimation = AnimationUtils.loadAnimation(this, R.anim.hide_manual_fab);
        showManualFABAnimation = AnimationUtils.loadAnimation(this, R.anim.show_manual_fab);

        hideAudioFABAnimation = AnimationUtils.loadAnimation(this, R.anim.hide_audio_fab);
        showAudioFABAnimation = AnimationUtils.loadAnimation(this, R.anim.show_audio_fab);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.sign_out:
                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                fAuth.signOut();
                songListviewAdapter.cleanupListener();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showFABs(){
        showManualEntryFAB();
        showAudioEntryFAB();
    }

    private void hideFABs(){
        hideManualEntryFAB();
        hideAudioEntryFAB();

    }

    public void showAudioEntryFAB(){
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) audioEntryFAB.getLayoutParams();
        layoutParams.rightMargin += (int) (audioEntryFAB.getWidth() * -.8);
        layoutParams.bottomMargin += (int) (audioEntryFAB.getHeight() * 1.2);
        audioEntryFAB.setLayoutParams(layoutParams);

        audioEntryFAB.startAnimation(showAudioFABAnimation);

        audioEntryFAB.setClickable(true);

    }


    private void showManualEntryFAB(){
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) manualEntryFAB.getLayoutParams();
        layoutParams.rightMargin += (int) (manualEntryFAB.getWidth() * .8);
        layoutParams.bottomMargin += (int) (manualEntryFAB.getHeight() * 1.2);
        manualEntryFAB.setLayoutParams(layoutParams);

        manualEntryFAB.startAnimation(showManualFABAnimation);

        manualEntryFAB.setClickable(true);
    }

    private void hideManualEntryFAB(){
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) manualEntryFAB.getLayoutParams();
        layoutParams.rightMargin += (int) (manualEntryFAB.getWidth() * -.8);
        layoutParams.bottomMargin += (int) (manualEntryFAB.getHeight() * -1.2);
        manualEntryFAB.setLayoutParams(layoutParams);

        manualEntryFAB.startAnimation(hideManualFABAnimation);
        manualEntryFAB.setClickable(false);
    }

    private void hideAudioEntryFAB(){
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) audioEntryFAB.getLayoutParams();
        layoutParams.rightMargin += (int) (audioEntryFAB.getWidth() * .8);
        layoutParams.bottomMargin += (int) (audioEntryFAB.getHeight() * -1.2);
        audioEntryFAB.setLayoutParams(layoutParams);

        audioEntryFAB.startAnimation(hideAudioFABAnimation);
        audioEntryFAB.setClickable(false);
    }


    private void showNewSongDialog() {
        FragmentManager fm = getSupportFragmentManager();
        dialog = AddSongDialog.newInstance();
        dialog.show(fm, "fragment_edit_name");
    }

    private void showAudioFingerprintDialog(){
        FragmentManager fm = getSupportFragmentManager();
        dialog = AudioFingerprintDialog.newInstance();
        dialog.show(fm, "fragment_edit_name");
    }


    @Override
    public void onReturnNewSong(Song song) {
        //TODO: deal with new song

        addSongToList(song);
        dialog.dismiss();


    }


    public void addSongToList(final Song song){

        db.getSongListReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Push the song, it will appear in the list
                db.getSongListReference().push().setValue(song);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onStart(){
        super.onStart();

        songListviewAdapter = new SongListviewAdapter(this, db.getSongListReference());
        songListView.setAdapter(songListviewAdapter);


        songListView.setLayoutManager(new LinearLayoutManager(this));

    }


}
