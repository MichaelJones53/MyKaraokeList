package com.mikejones.mykaraokelist;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.gracenote.gnsdk.GnError;
import com.gracenote.gnsdk.GnException;
import com.gracenote.gnsdk.GnLicenseInputMode;
import com.gracenote.gnsdk.GnLookupData;
import com.gracenote.gnsdk.GnManager;
import com.gracenote.gnsdk.GnMic;
import com.gracenote.gnsdk.GnMusicIdStream;
import com.gracenote.gnsdk.GnMusicIdStreamIdentifyingStatus;
import com.gracenote.gnsdk.GnMusicIdStreamPreset;
import com.gracenote.gnsdk.GnMusicIdStreamProcessingStatus;
import com.gracenote.gnsdk.GnResponseAlbums;
import com.gracenote.gnsdk.GnStatus;
import com.gracenote.gnsdk.GnUser;
import com.gracenote.gnsdk.GnUserStore;
import com.gracenote.gnsdk.IGnCancellable;
import com.gracenote.gnsdk.IGnMusicIdStreamEvents;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by MikeJones on 7/28/17.
 */

public class AudioFingerprintDialog extends DialogFragment{

    public static final String TAG = "AudioFingerprintDialog";

    private GnManager gnManager;
    private GnMusicIdStream musicIdStream;
    private GnUser gnUser;
    private String gnsdkLicenseFilename = "license.txt";
    private String appString;
    private GnMic mic;
    private String gnsdkLicense = null;

    private Animation animation1;
    private Animation animation2;
    private ImageView note1ImageView;
    private ImageView note2ImageView;
    private ImageView note3ImageView;
    private ImageView note4ImageView;
    private Context context;
    private AudioFingerprintDialog.AddAudioSongDialogListener activity;

    private boolean isMatchFound = true;


    /**
     * creates instance of AudioFingerprintDialog statically
     * @return
     *      reference to AudioFingerprintDialog
     */
    public static AudioFingerprintDialog newInstance(){
        AudioFingerprintDialog dialog = new AudioFingerprintDialog();

        dialog.setCancelable(false);
        return dialog;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_music_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        activity = (AudioFingerprintDialog.AddAudioSongDialogListener) getActivity();


        animation1 = new TranslateAnimation(
                TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, -.15f,
                TranslateAnimation.RELATIVE_TO_PARENT, .15f);
        animation1.setDuration(800);
        animation1.setRepeatCount(-1);
        animation1.setRepeatMode(Animation.REVERSE);
        animation1.setInterpolator(new LinearInterpolator());

        animation2 = new TranslateAnimation(
                TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.ABSOLUTE, -.15f,
                TranslateAnimation.RELATIVE_TO_PARENT, .15f);
        animation2.setDuration(800);
        animation2.setRepeatCount(-1);
        animation2.setRepeatMode(Animation.REVERSE);
        animation2.setInterpolator(new LinearInterpolator());

        note1ImageView = (ImageView) view.findViewById(R.id.note1);
        note2ImageView = (ImageView) view.findViewById(R.id.note2);
        note3ImageView = (ImageView) view.findViewById(R.id.note3);
        note4ImageView = (ImageView) view.findViewById(R.id.note4);

        note1ImageView.setAnimation(animation1);
        note2ImageView.setAnimation(animation2);
        note3ImageView.setAnimation(animation1);
        note4ImageView.setAnimation(animation2);

        String gnsdkClientId = getResources().getString(R.string.gnsdk_client_id);
        String gnsdkClientTag = getResources().getString(R.string.gnsdk_client_tag);
        appString = getResources().getString(R.string.app_name);
        context = getContext();

        //display searching music dialog



        // check the client id and tag have been set
        if ( (gnsdkClientId == null) || (gnsdkClientTag == null) ){
         //   Log.e(TAG, "gnsdk client id or tag issue");
            return;
        }

        // get the gnsdk license from the application assets
        if ( (gnsdkLicenseFilename == null) || (gnsdkLicenseFilename.length() == 0) ){

         //   Log.e(TAG, "License filename not set");
        } else {
            gnsdkLicense = getAssetAsString( gnsdkLicenseFilename );
            if ( gnsdkLicense == null ){

            //    Log.e(TAG, "License file not found: " + gnsdkLicenseFilename);
                return;
            }
        }

        mic = new GnMic();
        // get a user, if no user stored persistently a new user is registered and stored
        // Note: Android persistent storage used, so no GNSDK storage provider needed to store a user
        try {
          //  Log.d(TAG, gnsdkLicense);

            // GnManager must be created first, it initializes GNSDK
            gnManager = new GnManager(getActivity().getApplicationContext(), gnsdkLicense, GnLicenseInputMode.kLicenseInputModeString );


            // get a user, if no user stored persistently a new user is registered and stored
            // Note: Android persistent storage used, so no GNSDK storage provider needed to store a user
            gnUser = new GnUser( new GnUserStore(context), gnsdkClientId, gnsdkClientTag, appString );
            musicIdStream = new GnMusicIdStream(gnUser, GnMusicIdStreamPreset.kPresetMicrophone, new AudioListener());
          musicIdStream.options().lookupData(GnLookupData.kLookupDataContent, true);
          musicIdStream.options().lookupData(GnLookupData.kLookupDataSonicData, true);
            musicIdStream.options().resultSingle( true );

        // Create a thread to process the data pulled from GnMic
        // Internally pulling data is a blocking call, repeatedly called until
        // audio processing is stopped. This cannot be called on the main thread.
        new Thread(new AudioProcessRunnable()).start();
        } catch (GnException e) {
            e.printStackTrace();
        }












    }

    /**
     * Helpers to read license file from assets as string
     */
    private String getAssetAsString( String assetName ){

        String 		assetString = null;
        InputStream assetStream;

        try {

            assetStream = getActivity().getApplicationContext().getAssets().open(assetName);
            if(assetStream != null){

                java.util.Scanner s = new java.util.Scanner(assetStream).useDelimiter("\\A");

                assetString = s.hasNext() ? s.next() : "";
                assetStream.close();

            }else{
               // Log.e(appString, "Asset not found:" + assetName);
            }

        } catch (IOException e) {

           // Log.e( appString, "Error getting asset as string: " + e.getMessage() );

        }

        return assetString;
    }

    private class AudioListener implements IGnMusicIdStreamEvents{


        @Override
        public void musicIdStreamProcessingStatusEvent(GnMusicIdStreamProcessingStatus status, IGnCancellable iGnCancellable) {

          //  Log.d(TAG, "musicIdStreamProcessingStatusEvent called: "+status.toString());


            if (GnMusicIdStreamProcessingStatus.kStatusProcessingAudioStarted.compareTo(status) == 0) {
                try {

                    Log.i(TAG, "calling idnow");
                    musicIdStream.identifyAlbumAsync();


                } catch (GnException e) {
                    e.printStackTrace();
                    try {
                        musicIdStream.audioProcessStop();
                    } catch (GnException ex) {
                        // ignore
                    }
                }
            }


        }

        @Override
        public void musicIdStreamIdentifyingStatusEvent(GnMusicIdStreamIdentifyingStatus event, IGnCancellable iGnCancellable) {

          //  Log.d(TAG, "musicIdStreamIdentifyingStatusEvent called: "+event.toString());

        }

        @Override
        public void musicIdStreamAlbumResult(GnResponseAlbums result, IGnCancellable iGnCancellable) {

         //   Log.d(TAG, "musicIdStreamAlbumResult called: ");

            try {

                if(result.resultCount() > 0 && result.albums().count() > 0){


                    // display the first match result



                    String artist =  result.albums().at(0).next().artist().name().display();

                    String trackTitle = result.albums().at(0).next().trackMatched().title().display();

              //      Log.d(TAG, "--------------------"+artist+"  "+ trackTitle+"---------------");


                    new Thread(new AudioProcessStopRunnable()).start();

                    activity.onReturnNewAudioSong(trackTitle, artist);



                }
                else{
                //    Log.d(TAG, "NO MATCH FOUND");
                    isMatchFound = false;
                    new Thread(new AudioProcessStopRunnable()).start();
                    activity.onReturnNoMatchFound();

                }
            } catch (GnException e) {
                e.printStackTrace();
            }
            onDestroy();
        }

        @Override
        public void musicIdStreamIdentifyCompletedWithError(GnError error) {

            long errorCode = error.errorCode();
            isMatchFound = false;

         //   Log.d(TAG, "musicIdStreamIdentifyCompletedWithError called: "+errorCode+"  "+error.errorDescription());

            //GnMusicIdStream.audioProcessStop() waits for this result callback to finish,
            //so call audioProcessStop() in another thread and don't block here
            new Thread(new AudioProcessStopRunnable()).start();


            activity.onReturnNoMatchFound();
            onDestroy();



        }


        @Override
        public void statusEvent(GnStatus status, long l, long l1, long l2, IGnCancellable iGnCancellable) {

          //  Log.d(TAG, "statusEvent called: "+status.toString());

        }
    }

    /**
     * GnMusicIdStream object processes audio read directly from GnMic object
     */
    class AudioProcessRunnable implements Runnable {

        @Override
        public void run() {
            try {

                // start audio processing with GnMic, GnMusicIdStream pulls data from GnMic internally
                Log.d(TAG, "Audio Process started");
                musicIdStream.audioProcessStart( mic );

            } catch (GnException e) {
             //   Log.e( TAG, e.errorCode() + ", " + e.errorDescription() + ", " + e.errorModule() );


            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    class AudioProcessStopRunnable implements Runnable {

        public AudioProcessStopRunnable(){

        }

        @Override
        public void run() {


            if (musicIdStream != null) {

                try {
                    // stopping audio processing stops the audio processing thread
                    musicIdStream.audioProcessStop();
                    Log.d(TAG, "AudioProcessStopped");

                } catch (GnException e) {

                    Log.e(TAG,  e.errorCode() + ", "
                            + e.errorDescription() + ", "
                            + e.errorModule());

                }

            }

        }



    }


    public interface AddAudioSongDialogListener {
        public void onReturnNewAudioSong(String title, String artist);
        public void onReturnNoMatchFound();
    }



}
