package com.mikejones.mykaraokelist;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by MikeJones on 7/23/17.
 */

public class SongListviewAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList<Song> songList;

    public SongListviewAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<Song> objects) {
        super(context, resource, objects);

        this.context = context;
        songList = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        ViewHolder holder = null;

        if(convertView == null){
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.song_list_layout, null);

            holder = new ViewHolder();
            holder.songTitleTextView = (TextView) convertView.findViewById(R.id.songTitleTextView);
            holder.artistTextView = (TextView) convertView.findViewById(R.id.artistTextView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Song currentSong = songList.get(position);
        holder.songTitleTextView.setText(currentSong.getTitle());
        holder.artistTextView.setText(currentSong.getArtist());


        return convertView;
    }

    private class ViewHolder
    {
        TextView songTitleTextView;
        TextView artistTextView;

    }


}
