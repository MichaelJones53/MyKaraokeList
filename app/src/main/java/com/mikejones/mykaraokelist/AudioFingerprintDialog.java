package com.mikejones.mykaraokelist;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

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
    private TextView statusTextview;
    private TextView artistTextview;
    private TextView titleTextview;

    private GnManager gnManager;
    private GnMusicIdStream musicIdStream;
    private GnUser gnUser;
    private String gnsdkLicenseFilename = "license.txt";
    private String appString;
    private String gnsdkLicense = null;

    public static AudioFingerprintDialog newInstance(){
        AudioFingerprintDialog dialog = new AudioFingerprintDialog();
        dialog.setCancelable(true);
        return dialog;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.audio_fingerprint_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String gnsdkClientTag = getResources().getString(R.string.gnsdk_client_id);
        String gnsdkClientID = getResources().getString(R.string.gnsdk_client_id);
        appString = getResources().getString(R.string.app_name);
        Context context = getContext();

        // Get field from view
        statusTextview = (EditText) view.findViewById(R.id.status_textview);
        artistTextview = (EditText) view.findViewById(R.id.fingerprint_artist_textview);
        titleTextview = (EditText) view.findViewById(R.id.fingerprint_song_title_textview);

        // check the client id and tag have been set
        if ( (gnsdkClientID == null) || (gnsdkClientTag == null) ){
            Log.e(TAG, "gnsdk client id or tag issue");
            return;
        }

        // get the gnsdk license from the application assets
        if ( (gnsdkLicenseFilename == null) || (gnsdkLicenseFilename.length() == 0) ){

            Log.e(TAG, "License filename not set");
        } else {
            gnsdkLicense = getAssetAsString( gnsdkLicenseFilename );
            if ( gnsdkLicense == null ){

                Log.e(TAG, "License file not found: " + gnsdkLicenseFilename);
                return;
            }
        }
        // get a user, if no user stored persistently a new user is registered and stored
        // Note: Android persistent storage used, so no GNSDK storage provider needed to store a user
        try {
            Log.d(TAG, gnsdkLicense);

            // GnManager must be created first, it initializes GNSDK
            gnManager = new GnManager(getActivity().getApplicationContext(), gnsdkLicense, GnLicenseInputMode.kLicenseInputModeString );


            GnUserStore userStore = new GnUserStore(getActivity());
            gnUser = new GnUser( userStore, gnsdkClientID, gnsdkClientTag, appString );
            musicIdStream = new GnMusicIdStream(gnUser, GnMusicIdStreamPreset.kPresetMicrophone, new AudioListener());
//            musicIdStream.options().lookupData(GnLookupData.kLookupDataContent, true);
//            musicIdStream.options().lookupData(GnLookupData.kLookupDataSonicData, true);
            musicIdStream.options().resultSingle( true );


            musicIdStream.audioProcessStart(new GnMic());
            musicIdStream.identifyAlbumAsync();

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
                Log.e(appString, "Asset not found:" + assetName);
            }

        } catch (IOException e) {

            Log.e( appString, "Error getting asset as string: " + e.getMessage() );

        }

        return assetString;
    }

    private class AudioListener implements IGnMusicIdStreamEvents{


        @Override
        public void musicIdStreamProcessingStatusEvent(GnMusicIdStreamProcessingStatus gnMusicIdStreamProcessingStatus, IGnCancellable iGnCancellable) {

            Log.d(TAG, "musicIdStreamProcessingStatusEvent called");

        }

        @Override
        public void musicIdStreamIdentifyingStatusEvent(GnMusicIdStreamIdentifyingStatus gnMusicIdStreamIdentifyingStatus, IGnCancellable iGnCancellable) {

            Log.d(TAG, "musicIdStreamIdentifyingStatusEvent called");
        }

        @Override
        public void musicIdStreamAlbumResult(GnResponseAlbums gnResponseAlbums, IGnCancellable iGnCancellable) {

            Log.d(TAG, "musicIdStreamAlbumResult called");

            getDialog().setCancelable(true);
        }

        @Override
        public void musicIdStreamIdentifyCompletedWithError(GnError gnError) {

            statusTextview.setText("error "+gnError.toString());
            Log.d(TAG, "musicIdStreamIdentifyCompletedWithError called");
            getDialog().setCancelable(true);
        }

        @Override
        public void statusEvent(GnStatus gnStatus, long l, long l1, long l2, IGnCancellable iGnCancellable) {

            statusTextview.setText("error "+gnStatus.toString());
            Log.d(TAG, "statusEvent called");
        }
    }

}
