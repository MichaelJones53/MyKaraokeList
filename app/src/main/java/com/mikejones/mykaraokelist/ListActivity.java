package com.mikejones.mykaraokelist;


import android.Manifest;
import android.content.Intent;


import android.content.pm.PackageManager;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ListActivity extends AppCompatActivity implements AudioFingerprintDialog.AddAudioSongDialogListener, AddSongDialog.AddSongDialogListener, UpdateSongDialog.UpdateSongDialogListener{
    public static final String TAG = "ListActivity";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    private static RecyclerView songListView;
    private SongRecyclerViewAdapter songListviewAdapter;
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
    private ItemTouchHelper.Callback helper;
    private ItemTouchHelper touchHelper;

    private boolean buttonsShown = false;
    private boolean initialReq = true;

    private DatabaseReference databaseRef;
    private FragmentManager fm;

    private boolean permissionToRecordAccepted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);


        databaseRef = DatabaseUtils.getDatabase().getReference().child("users/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/songList");


        initalizeAnimations();
        initalizeViews();
        initalizeTouchHelper();

        fm = getSupportFragmentManager();


    }

    private void initalizeTouchHelper() {
        helper = new SimpleItemTouchHelperCallback(songListviewAdapter, this);
        touchHelper = new ItemTouchHelper(helper);
        songListView.setLayoutManager(new LinearLayoutManager(this));
        touchHelper.attachToRecyclerView(songListView);
    }


    //TODO: it is not reading from firebase (using presistant data primarially)
    //TODO:  write algorithm for swapping!!!
    private void initalizeViews() {
        toolbar = (Toolbar) findViewById(R.id.bottomToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        songListView = (RecyclerView) findViewById(R.id.songListView);

        mainFAB = (FloatingActionButton) findViewById(R.id.mainFloatingActionButton);
        manualEntryFAB = (FloatingActionButton) findViewById(R.id.manualEntryFAB);
        audioEntryFAB = (FloatingActionButton) findViewById(R.id.audioEntryFAB);


        manualEntryFAB.setVisibility(View.INVISIBLE);
        audioEntryFAB.setVisibility(View.INVISIBLE);



        songListviewAdapter = new SongRecyclerViewAdapter(this);
        songListView.setAdapter(songListviewAdapter);


        mainFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonsShown) {

                    hideFABs();
                    mainFAB.startAnimation(rotateBackwardMainFABAnimation);
                    buttonsShown = false;
                } else {
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
                if (buttonsShown) {

                    hideFABs();
                    mainFAB.startAnimation(rotateBackwardMainFABAnimation);
                    buttonsShown = false;
                }

            }
        });

        audioEntryFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(ListActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ListActivity.this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
                } else {
                    showAudioFingerprintDialog();
                }

                if (buttonsShown) {

                    hideFABs();
                    mainFAB.startAnimation(rotateBackwardMainFABAnimation);
                    buttonsShown = false;
                }

            }
        });

    }

    @Override
    public void onReturnNewSong(Song song) {

        databaseRef.push().setValue(song);

    }


    @Override
    public void onReturnNoMatchFound() {
        Log.d(TAG, "onReturnNoMatchFound called");

        dialog.dismiss();
        Snackbar message = Snackbar.make(findViewById(R.id.constLayout), "Unable To Identify Song", Snackbar.LENGTH_SHORT);
        message.show();

    }


    @Override
    public void onStart() {
        super.onStart();



    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop called");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        songListviewAdapter.cleanupListener();
        songListviewAdapter = null;
        songListView = null;
        Log.d(TAG, "ondestroy called");



    }

    public static RecyclerView getRecyclerView(){
        return songListView;
    }

    private void initalizeAnimations() {
        rotateForwardMainFABAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_forward_main_fab);
        rotateBackwardMainFABAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_reverse_main_fab);

        hideManualFABAnimation = AnimationUtils.loadAnimation(this, R.anim.hide_manual_fab);
        showManualFABAnimation = AnimationUtils.loadAnimation(this, R.anim.show_manual_fab);

        hideAudioFABAnimation = AnimationUtils.loadAnimation(this, R.anim.hide_audio_fab);
        showAudioFABAnimation = AnimationUtils.loadAnimation(this, R.anim.show_audio_fab);

        hideAudioFABAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                audioEntryFAB.setVisibility(View.INVISIBLE);
                manualEntryFAB.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        showAudioFABAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                audioEntryFAB.setVisibility(View.VISIBLE);
                manualEntryFAB.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                DatabaseUtils.signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showFABs() {
        showManualEntryFAB();
        showAudioEntryFAB();
    }

    private void hideFABs() {
        hideManualEntryFAB();
        hideAudioEntryFAB();

    }

    public void showAudioEntryFAB() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) audioEntryFAB.getLayoutParams();
        layoutParams.rightMargin += (int) (audioEntryFAB.getWidth() * -.8);
        layoutParams.bottomMargin += (int) (audioEntryFAB.getHeight() * 1.2);

        audioEntryFAB.setLayoutParams(layoutParams);
        audioEntryFAB.startAnimation(showAudioFABAnimation);
        audioEntryFAB.setClickable(true);
    }


    private void showManualEntryFAB() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) manualEntryFAB.getLayoutParams();
        layoutParams.rightMargin += (int) (manualEntryFAB.getWidth() * .8);
        layoutParams.bottomMargin += (int) (manualEntryFAB.getHeight() * 1.2);

        manualEntryFAB.setLayoutParams(layoutParams);
        manualEntryFAB.startAnimation(showManualFABAnimation);
        manualEntryFAB.setClickable(true);
    }

    private void hideManualEntryFAB() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) manualEntryFAB.getLayoutParams();
        layoutParams.rightMargin += (int) (manualEntryFAB.getWidth() * -.8);
        layoutParams.bottomMargin += (int) (manualEntryFAB.getHeight() * -1.2);
        manualEntryFAB.setLayoutParams(layoutParams);

        manualEntryFAB.startAnimation(hideManualFABAnimation);
        manualEntryFAB.setClickable(false);
    }

    private void hideAudioEntryFAB() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) audioEntryFAB.getLayoutParams();
        layoutParams.rightMargin += (int) (audioEntryFAB.getWidth() * .8);
        layoutParams.bottomMargin += (int) (audioEntryFAB.getHeight() * -1.2);
        audioEntryFAB.setLayoutParams(layoutParams);

        audioEntryFAB.startAnimation(hideAudioFABAnimation);
        audioEntryFAB.setClickable(false);
    }


    private void showNewSongDialog() {

        dialog = AddSongDialog.newInstance();
        dialog.show(fm, "fragment_edit_name");
    }

    public void showNewSongDialogWithSong(String title, String artist) {

        dialog = AddSongDialog.newInstanceWithSong(title, artist);
        dialog.show(fm, "fragment_edit_name");
    }

    private void showAudioFingerprintDialog() {

        dialog = AudioFingerprintDialog.newInstance();
        dialog.show(fm, "fragment_edit_name");


    }


    @Override
    public void onReturnNewAudioSong(String title, String artist) {
        Log.d(TAG, "onReturnNewAudioSong called");
        if (dialog != null && dialog.isVisible()) {
            dialog.dismiss();
        }

        showNewSongDialogWithSong(title, artist);
    }









    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (permissionToRecordAccepted) {
            if (!initialReq) {

                showAudioFingerprintDialog();
            }
        } else {
            Snackbar.make(findViewById(R.id.constLayout), "Permission needed for Audio Identification.", Snackbar.LENGTH_SHORT).show();

        }

    }


    @Override
    public void onCancelUpdateSong(String key) {
        songListviewAdapter.notifyItemChanged(SongRecyclerViewAdapter.getPositionFromKey(key));

    }


}
