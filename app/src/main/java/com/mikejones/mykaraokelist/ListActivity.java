package com.mikejones.mykaraokelist;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity implements AddSongDialog.AddSongDialogListener {

    private ListView songListView;
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
    private AddSongDialog dialog;
    private DatabaseManager songDatabase;


    private String test = "test";

    private boolean buttonsShown = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        toolbar = (Toolbar) findViewById(R.id.bottomToolBar);


        songDatabase = new DatabaseManager(this);



        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        rotateForwardMainFABAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_forward_main_fab);
        rotateBackwardMainFABAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_reverse_main_fab);

        hideManualFABAnimation = AnimationUtils.loadAnimation(this, R.anim.hide_manual_fab);
        showManualFABAnimation = AnimationUtils.loadAnimation(this, R.anim.show_manual_fab);

        hideAudioFABAnimation = AnimationUtils.loadAnimation(this, R.anim.hide_audio_fab);
        showAudioFABAnimation = AnimationUtils.loadAnimation(this, R.anim.show_audio_fab);


        //************************ temp data
        ArrayList<Song> songs = new ArrayList<>();
        songs.add(new Song("some weird song", "yo moma"));
        songs.add(new Song("table for 1", "the brothers grim"));
        songs.add(new Song("lazy bones", "yes sur"));
        songs.add(new Song("i think i love you more than anything in the world", "some artist"));
        songs.add(new Song("donkey lover", "testing testing"));
        songs.add(new Song("pupers pupadup", "some other artist"));
        songs.add(new Song("activate the laser", "the Dr. Evils"));
        songs.add(new Song("she loves me not", "the heartbreakers and tom petty and cher and some other people"));
        songs.add(new Song("lazy bones", "yes sur"));
        songs.add(new Song("i think i love you more than anything in the world", "some artist"));
        songs.add(new Song("Puddin' pop", "Mr. J.T."));
        songs.add(new Song("pupers pupadup", "some other artist"));
        //************************


        songListView = (ListView) findViewById(R.id.songListView);
        songListviewAdapter = new SongListviewAdapter(this, R.layout.song_list_layout,songs);
        songListView.setAdapter(songListviewAdapter);

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



            }
        });








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


    @Override
    public void onReturnNewSong(Song song) {
        //TODO: deal with new song
        //add song to list

        songDatabase.addSong(song);

        System.out.println(songDatabase.printDatabase());
        //add song to DB
        //add song to firebase
        dialog.dismiss();


    }



}
