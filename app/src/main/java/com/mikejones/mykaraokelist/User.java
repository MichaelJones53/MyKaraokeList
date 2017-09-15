package com.mikejones.mykaraokelist;

import java.util.ArrayList;

/**
 * Created by MikeJones on 3/31/17.
 */

public class User {

    private String email;
    private String uid;
    private ArrayList<Song> songList = new ArrayList<Song>();

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

    public ArrayList<Song> getSongList() {
        return songList;
    }

    public void setSongList(ArrayList<Song> songList) {
        this.songList = songList;
    }


    public void addSong(Song song){
        songList.add(song);
    }



    public void removeSong(Song otherSong){

        for(Song song: songList){
            if(song.equals(otherSong)){
                songList.remove(song);
            }
        }

    }

}
