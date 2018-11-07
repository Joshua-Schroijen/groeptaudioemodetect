package groept.be.emodetect.fragments.models;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import groept.be.emodetect.PlaybackFunctionalityManager;
import groept.be.emodetect.RecordingFunctionalityManager;
import groept.be.emodetect.helpers.miscellaneous.ExceptionHandler;
import groept.be.emodetect.helpers.databasehelpers.RecordingsDatabaseHelper;
import groept.be.emodetect.uihelpers.RecordingController;
import groept.be.emodetect.uihelpers.RecordingsListAdapter;

public class RecordPlaybackTabModel extends AndroidViewModel implements ExceptionHandler {
    public final static String RECORD_PLAYBACK_TAB_MODEL_TAG = "RecPlayTabModelTag";

    private static RecordPlaybackTabModel currentInstance = null;
    public static RecordPlaybackTabModel getCurrentInstance(){
        return( RecordPlaybackTabModel.currentInstance );
    }

    private Activity activityContext;
    private Context applicationContext;
    private Handler handler;

    private PlaybackFunctionalityManager playbackFunctionalityManager;
    private RecordingFunctionalityManager recordingFunctionalityManager;

    private RecordingsDatabaseHelper recordingsDatabase;
    private RecordingsListAdapter storedAudioRecordingsAdapter;

    private RecordingController recordingController;

    private boolean initialized;

    public RecordPlaybackTabModel( Application application ){
        super( application );

        RecordPlaybackTabModel.currentInstance = this;
        this.applicationContext = application;
        this.initialized = false;
    }

    /****************************************************************
     * IMPORTANT FUNCTION! Always call right after getting instance *
     * through ViewModelProviders in RecordPlaybackTabFragment      *
     *  class - otherwise getters will return invalid & dangerous   *
     *  results!                                                    *
     *                                                              *
     * @param activityContext The activity context to make sure     *
     *                        all stateful views run in right one   *
     *                        all the time!                         *
     ****************************************************************/
    public void setupStatefulElements( Activity activityContext ){
        if( initialized == false ){
            this.activityContext = activityContext;
            this.applicationContext = activityContext.getApplicationContext();
            this.handler = new Handler();

            /* Here we ready our application to use the internal recordings database
             */
            playbackFunctionalityManager = new PlaybackFunctionalityManager();

            recordingsDatabase = new RecordingsDatabaseHelper( applicationContext, "emotion_detection_database.db" );
            recordingsDatabase.createTableIfNeeded();
            storedAudioRecordingsAdapter = new RecordingsListAdapter(
                applicationContext,
                handler,
                this,
                playbackFunctionalityManager,
                recordingsDatabase );

            recordingController = new RecordingController( activityContext );

            recordingFunctionalityManager = new RecordingFunctionalityManager( activityContext, recordingController );

            this.initialized = true;
        } else {
            recordingController = recordingController.copyWithNewContext( activityContext );
        }
    }

    public RecordingsDatabaseHelper getRecordingsDatabase(){
        return( recordingsDatabase );
    }

    public RecordingsListAdapter getStoredAudioRecordingsAdapter(){
        return( storedAudioRecordingsAdapter );
    }

    public RecordingController getRecordingController() {
        return( recordingController );
    }

    public RecordingFunctionalityManager getRecordingFunctionalityManager(){
        return( recordingFunctionalityManager );
    }

    public PlaybackFunctionalityManager getPlaybackFunctionalityManager(){
        return( playbackFunctionalityManager );
    }

    @Override
    public void handleException( Exception exception ){
        Log.v(
             RECORD_PLAYBACK_TAB_MODEL_TAG,
             ( "FATAL EXCEPTION CAUSED PROGRAM TO TERMINATE!\n" +
               exception.getMessage() )
        );

        System.exit( 2 );
    }
}
