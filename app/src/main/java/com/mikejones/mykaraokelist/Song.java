package com.mikejones.mykaraokelist;

/**
 * Created by MikeJones on 3/31/17.
 */

public class Song {

    private String title;
    private String artist;
    private String lyrics = null;
    private int year;
    private String album;
    private String genre;
    private int rating = 0;



    public Song(){

    }

    public Song(String title, String artist){
        this.title = title;
        this.artist = artist;
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
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
}
