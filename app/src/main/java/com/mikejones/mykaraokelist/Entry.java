package com.mikejones.mykaraokelist;

import java.util.Date;

/**
 * Created by MikeJones on 7/23/17.
 */

public class Entry {
    private Date entered;
    private Song entry;

    public Entry(){
        entered = new Date();
    }
    public Entry(Song song){
        entered = new Date();
        entry = song;
    }

    public Date getEntered() {
        return entered;
    }

    public void setEntered(Date entered) {
        this.entered = entered;
    }

    public Song getEntry() {
        return entry;
    }

    public void setEntry(Song entry) {
        this.entry = entry;
    }
}
