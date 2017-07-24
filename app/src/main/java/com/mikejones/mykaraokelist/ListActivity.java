package com.mikejones.mykaraokelist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

import java.sql.Array;
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    private ListView songListView;
    private SongListviewAdapter songListviewAdapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        toolbar = (Toolbar) findViewById(R.id.bottomToolBar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


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
}
