package com.mikejones.mykaraokelist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Created by MikeJones on 7/25/17.
 */

public class DatabaseManager {

    private SongDatabase songDatabase;

    public DatabaseManager(Context context){
        songDatabase = new SongDatabase(context);

    }

    /**
     * adds song to SQLite database and Firebase for remote storage
     * @param song
     *      song to be added
     */
    public void addSong(Song song){
        //TODO add song to firebase

        songDatabase.addSong(song);
    }

    /**
     * returns song object based on ID provided
     * @param id
     *      id number of song
     * @return
     *      song object that corrosponds to ID provided
     */
    public Song getSong(int id){
        return songDatabase.getSong(id);
    }


    /**
     * returns list of all songs in database
     * @return
     *      returns list of al songs in database
     */
    public List<Song> getAllSongs(){
        return songDatabase.getAllSongs();
    }


    /**
     * returns number of songs in list
     * @return
     *      returns number of songs in list
     */
    public int getSongCount(){
        return songDatabase.getSongCount();
    }

    /**
     * updates song with proivided song informaiton
     * @param song
     *      new song data to update with
     * @return
     *      returns number of rows effected by the update request
     */
    public int updateSong(Song song){
        //TODO update song in firebase
        return songDatabase.updateSong(song);
    }

    /**
     *deltes song from database
     * @param song
     *      song to be deleted
     */
    public void deleteSong(Song song){
        //TODO delete song from firebase
        songDatabase.deleteSong(song);
    }


    public String printDatabase(){
        String data = null;
        List<Song> songList = getAllSongs();
        for(Song song: songList){
            data+= song.toString();
        }
        return data;
    }

    public void deleteSongTable(){
        songDatabase.deleteAll();
    }
}
