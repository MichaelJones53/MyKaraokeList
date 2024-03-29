package com.mikejones.mykaraokelist;

import java.util.Date;

/**
 * Created by MikeJones on 3/31/17.
 */

public class Song {


    private String title;
    private String artist;
    private String lyrics = null;
    private int rating = 0;
    private Date entered;



    public Song(){

    }

    public Song(String title, String artist){
        this.title = title;
        this.artist = artist;
        entered = new Date();
    }

    public Song(String title, String artist, String lyrics, int rating){


        this.title = title;
        this.artist = artist;
        this.lyrics = lyrics;
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public int getRating()
    {
        return  rating;
    }

    public Date getEntered() {
        return entered;
    }

    public void setEntered(Date entered) {
        this.entered = entered;
    }

    public void setRating(int rating){
        this.rating = rating;
    }
    public boolean equals(Object o){
        if(o instanceof Song == false){
            return false;
        }else{
            Song other = (Song) o;
            if(getTitle().toLowerCase().equalsIgnoreCase(other.getTitle()) && getArtist().equalsIgnoreCase(other.getArtist())){
                return true;
            }else{
                return false;
            }
        }

    }



    public boolean equals(Song song){
        return title.equalsIgnoreCase(song.title) && artist.equalsIgnoreCase(song.artist);
    }
    public String toString(){
        return "\nTitle: "+title+ "\n"
                +"Artist: "+artist+"\n"
                +"Lyrics: "+lyrics+"\n"
                +"rating: "+rating+"\n_";
    }
}
