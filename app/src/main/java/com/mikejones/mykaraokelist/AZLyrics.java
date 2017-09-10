package com.mikejones.mykaraokelist;

/**
 * Created by MikeJones on 7/27/17.
 */


import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AZLyrics extends AsyncTask<String, Void, String> {


    public static final String domain = "www.azlyrics.com/";
    public static final String TAG = "AZLyrics";


    public static String fromMetaData(String artist, String song) {
        if (artist.toLowerCase(Locale.getDefault()).startsWith("the ")) {
            artist = artist.substring(4);
            System.out.println("==="+artist+"===");
        }

        String htmlArtist = artist.replaceAll("[\\s'\"-]", "")
                .replaceAll("&", "and").replaceAll("[^A-Za-z0-9]", "");
        String htmlSong = song.replaceAll("[\\s'\"-]", "")
                .replaceAll("&", "and").replaceAll("[^A-Za-z0-9]", "");


        String urlString = String.format(
                "http://www.azlyrics.com/lyrics/%s/%s.html",
                htmlArtist.toLowerCase(Locale.getDefault()),
                htmlSong.toLowerCase(Locale.getDefault()));
        Log.d(TAG, "URL string is: "+urlString);
        return fromURL(urlString, htmlArtist, htmlSong);
    }

    public static String fromURL(String url, String artist, String song) {
        String html;
        Log.d(TAG, "fromURL called artist: "+artist+"  song: "+song);
        try {
            Document document = Jsoup.connect(url).userAgent(Net.USER_AGENT).get();
            if (document.location().contains("azlyrics")) {
                html = document.html();
                Log.d(TAG, "html set "+html.toString());
            }else {
                Log.d(TAG, "Exception after html creation: ");
                throw new IOException("Redirected to wrong domain " + document.location());
            }
        } catch (HttpStatusException e) {

            Log.d(TAG, e.getMessage().toString()+" : httpstatusexception "+url);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage().toString()+" : some other generic exception");
            Log.d(TAG, e.getMessage().toString());
            return null;
        }
        Pattern p = Pattern.compile(
                "Sorry about that. -->(.*)",
                Pattern.DOTALL);
        Matcher matcher = p.matcher(html);

        if (artist == null || song == null) {
            Pattern metaPattern = Pattern.compile(
                    "ArtistName = \"(.*)\";\r\nSongName = \"(.*)\";\r\n",
                    Pattern.DOTALL);
            Matcher metaMatcher = metaPattern.matcher(html);
            if (metaMatcher.find()) {
                artist = metaMatcher.group(1);
                song = metaMatcher.group(2);
                song = song.substring(0, song.indexOf('"'));
            } else
                artist = song = "";
        }

        if (matcher.find()) {
            String text = matcher.group(1);
            text = text.substring(0, text.indexOf("</div>"));
            text = text.replaceAll("\\[[^\\[]*\\]", "");
            text = text.replaceAll("<i>", "").replaceAll("</i>", "").replaceAll("<br>", "");

            Log.d(TAG, "FOUND LYRICS: "+text);
            return text;
        } else
            return null;
    }

    @Override
    protected String doInBackground(String... params) {
        return null;
    }
}