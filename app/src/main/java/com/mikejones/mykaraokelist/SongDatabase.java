package com.mikejones.mykaraokelist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MikeJones on 7/25/17.
 */

public class SongDatabase extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "songsManager";

    // songs table name
    private static final String TABLE_SONGS = "songs";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_ARTIST = "artist";
    private static final String KEY_LYRICS = "lyrics";
    private static final String KEY_RATING = "rating";


    public SongDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_CONTACTS_TABLE =
                "CREATE TABLE "
                        + TABLE_SONGS + "("
                        + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + KEY_TITLE + " TEXT,"
                        + KEY_ARTIST + " TEXT,"
                        + KEY_LYRICS + " TEXT,"
                        + KEY_RATING + " INTEGER"  + ")";

        db.execSQL(CREATE_CONTACTS_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);

    }


    // Adding new song
    public void addSong(Song song) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, song.getTitle());
        values.put(KEY_ARTIST, song.getArtist());
        values.put(KEY_LYRICS, song.getLyrics());
        values.put(KEY_RATING, song.getRating());

        // Inserting Row
        db.insert(TABLE_SONGS, null, values);

        db.close(); // Closing database connection

    }

    // Getting single song
    public Song getSong(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SONGS, new String[] { KEY_ID,
                        KEY_TITLE, KEY_ARTIST, KEY_LYRICS, KEY_RATING }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Song song = new Song(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4));
        // return contact
        return song;
    }

    // Getting All songs
    public List<Song> getAllSongs() {
        List<Song> songList = new ArrayList<Song>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SONGS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Song song = new Song();
                song.setId(Integer.parseInt(cursor.getString(0)));
                song.setTitle(cursor.getString(1));
                song.setArtist(cursor.getString(2));
                song.setLyrics(cursor.getString(3));
                song.setRating(cursor.getInt(4));
                // Adding contact to list
                songList.add(song);
            } while (cursor.moveToNext());
        }

        cursor.close();
        // return contact list
        return songList;
    }

    // Getting song Count
    public int getSongCount() {
        String countQuery = "SELECT  * FROM " + TABLE_SONGS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    // Updating single song
    public int updateSong(Song song) {


        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, song.getTitle());
        values.put(KEY_ARTIST, song.getArtist());
        values.put(KEY_LYRICS, song.getLyrics());
        values.put(KEY_RATING, song.getRating());

        // updating row
        return db.update(TABLE_SONGS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(song.getId()) });
    }

    // Deleting single song
    public void deleteSong(Song song) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SONGS, KEY_ID + " = ?",
                new String[] { String.valueOf(song.getId()) });
        db.close();


    }

    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SONGS, null, null);
    }


}
