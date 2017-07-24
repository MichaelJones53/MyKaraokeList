package com.mikejones.mykaraokelist;

import java.util.ArrayList;

/**
 * Created by MikeJones on 3/31/17.
 */

public class User {

    private String email;
    private String uid;
    private ArrayList<Entry> songList = new ArrayList<Entry>();

    public User(){

    }

    public User(String e, String id){
        email = e;
        uid = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Entry> getSongList() {
        return songList;
    }

    public void setSongList(ArrayList<Entry> songList) {
        this.songList = songList;
    }


    public void addSong(Song song){
        songList.add(new Entry(song));
    }



    public void removeSong(Song song){

        for(Entry entry: songList){
            if(entry.getEntry().equals(song)){
                songList.remove(entry);
            }
        }

    }

}
