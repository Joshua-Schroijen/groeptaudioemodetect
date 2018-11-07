package groept.be.emodetect;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import groept.be.emodetect.fragments.models.RecordPlaybackTabModel;
import groept.be.emodetect.helpers.recordinghelpers.AudioRecordConsumer;
import groept.be.emodetect.helpers.recordinghelpers.AudioRecordCreationException;
import groept.be.emodetect.helpers.recordinghelpers.StereoPCMAudioRecordProducer;
import groept.be.emodetect.uihelpers.dialogs.NewRecordingDismissHandler;
import groept.be.emodetect.uihelpers.dialogs.NewRecordingNameObserver;
import groept.be.emodetect.uihelpers.RecordingController;
import groept.be.emodetect.uihelpers.dialogs.RecordingNameDialog;
import groept.be.emodetect.uihelpers.dialogs.RecordingNameDialogDismissHandler;

public class RecordingFunctionalityManager implements NewRecordingNameObserver {
    public static final String RECORDING_FUNCTIONALITY_MANAGER_TAG = "RecordingFuncManager";

    private static final String SPEECH_FRAGMENT_STORAGE_DIRECTORY_NAME = "/GroepT/SpeechEmotionDetection";
    private static final String SPEECH_FRAGMENT_STORAGE_DIRECTORY_FULL_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() +
                                                                              SPEECH_FRAGMENT_STORAGE_DIRECTORY_NAME;

    public static String getRecordingFilename( String recordingName ){
        if( recordingName.endsWith( ".wav" ) ){
            return( SPEECH_FRAGMENT_STORAGE_DIRECTORY_FULL_PATH + "/" + recordingName );
        } else {
            return( SPEECH_FRAGMENT_STORAGE_DIRECTORY_FULL_PATH + "/" + recordingName + ".wav" );
        }
    }

    private Context activityContext;
    private Context applicationContext;
    private RecordPlaybackTabModel recordPlaybackTabModel;
    private RecordingController recordingController;

    /* We will use this recording name dialog object to construct and show
     * a dialog window prompting a user for a new recording name
     */
    RecordingNameDialog newRecordingNameDialog;

    /* We will use this field to store the user's desired name for the new recording
     */
    private String newRecordingName;

    /* We will use this field to generate and store a filename for the new recording based on
     * the desired new recording name
     */
    String currentSpeechFragmentFilePathname;

    private File speechFragmentStorageDirectory;

    private RandomAccessFile speechFragmentFileReference;

    private View.OnClickListener onNewRecordingClickListener;
    private View.OnClickListener onStartRecordingClickListener;
    private View.OnClickListener onStopRecordingClickListener;
    private View.OnClickListener onKeepRecordingClickListener;
    private View.OnClickListener onDiscardRecordingClickListener;

    /* We will use this thread pool to execute the audio recording producer-consumer pair
     */
    ExecutorService producerConsumerAudioExecutorService;

    /* We will use this new recording name dialog dismiss handler to store the name
     * the user put in
     */
    RecordingNameDialogDismissHandler recordingNameDialogDismissHandler;

    public RecordingFunctionalityManager( Context activityContext, RecordingController recordingController ){
        this.activityContext = activityContext;
        this.applicationContext = activityContext.getApplicationContext();
        this.recordPlaybackTabModel = RecordPlaybackTabModel.getCurrentInstance();

        this.recordingNameDialogDismissHandler = new NewRecordingDismissHandler( activityContext );
        this.recordingNameDialogDismissHandler.registerObserver( this );
        this.newRecordingName = null;
        this.currentSpeechFragmentFilePathname = null;

        this.recordingController = recordingController;

        if( this.recordingController == null ){
            throw( new IllegalArgumentException( "RecordingFunctionalityManager cannot function without a RecordingController UI view!" ) );
        } else {
            onNewRecordingClickListener = new View.OnClickListener(){
                @Override
                public void onClick( View v ){
                    RecordingFunctionalityManager.this.newRecording();
                }
            };
            onStartRecordingClickListener = new View.OnClickListener(){
                @Override
                public void onClick( View v ){
                    RecordingFunctionalityManager.this.startRecording();
                }
            };
            onStopRecordingClickListener = new View.OnClickListener(){
                @Override
                public void onClick( View v ){
                    RecordingFunctionalityManager.this.stopRecording();
                }
            };
            onKeepRecordingClickListener = new View.OnClickListener(){
                @Override
                public void onClick( View v ){
                    RecordingFunctionalityManager.this.keepRecording();
                }
            };
            onDiscardRecordingClickListener = new View.OnClickListener(){
                @Override
                public void onClick( View v ){
                    RecordingFunctionalityManager.this.discardRecording();
                }
            };
            recordingController.setOnNewRecordingClickListener( onNewRecordingClickListener );
            recordingController.setOnStartRecordingClickListener( onStartRecordingClickListener );
            recordingController.setOnStopRecordingClickListener( onStopRecordingClickListener );
            recordingController.setOnKeepRecordingClickListener( onKeepRecordingClickListener );
            recordingController.setOnDiscardRecordingClickListener( onDiscardRecordingClickListener );
        }
    }

    @Override
    public void updateName( String newName ){
        if( newName == null ){
            recordingController.resetProcess();
        } else {
            if( RecordPlaybackTabModel.getCurrentInstance().getRecordingsDatabase().isRecorded( newName ) ){
                recordingController.resetProcess();

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( activityContext );
                alertDialogBuilder.setMessage( R.string.error_recording_already_exists );
                alertDialogBuilder.setTitle( "Error" );
                alertDialogBuilder.setCancelable( false );
                alertDialogBuilder.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick( DialogInterface dialog, int which ){
                            newRecording();
                        }
                    } );

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                float textSize =
                    ( ( applicationContext.getResources().getDimension( R.dimen.error_dialog_text_size ) ) /
                      ( applicationContext.getResources().getDisplayMetrics().density ) );
                TextView errorMessage = alertDialog.findViewById( android.R.id.message );
                errorMessage.setTextSize( textSize );
            } else if( newName.isEmpty() == true ){
                recordingController.resetProcess();

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( activityContext );
                alertDialogBuilder.setMessage( R.string.error_recording_name_field_empty );
                alertDialogBuilder.setTitle( "Error" );
                alertDialogBuilder.setCancelable( false );
                alertDialogBuilder.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick( DialogInterface dialog, int which ){
                                newRecording();
                            }
                        } );

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                float textSize =
                    ( ( applicationContext.getResources().getDimension( R.dimen.error_dialog_text_size ) ) /
                        ( applicationContext.getResources().getDisplayMetrics().density ) );
                TextView errorMessage = alertDialog.findViewById( android.R.id.message );
                errorMessage.setTextSize( textSize );
            } else {
                newRecordingName = newName;
            }
        }
    }

    public void createSpeechFragmentStorageDirectory(){
        speechFragmentStorageDirectory = new File( SPEECH_FRAGMENT_STORAGE_DIRECTORY_FULL_PATH );

        if( !speechFragmentStorageDirectory.exists() ){
            Log.d( "RecordingFuncManager", speechFragmentStorageDirectory.getAbsolutePath() + ", can write: " + speechFragmentStorageDirectory.canWrite() );
            Log.d( "RecordingFuncManager", "DIR DOES NOT EXISTS - CREATING!" );
            boolean result = speechFragmentStorageDirectory.mkdirs();
            Log.d( "RecordingFuncMangaer", "mkdirs() result: " + result );
        }
    }

    public boolean tryToCreateSpeechFragmentStorageDirectory(){
        boolean caughtSecurityException = false;
        try{
            createSpeechFragmentStorageDirectory();
        } catch( SecurityException e ){
            caughtSecurityException = true;
        } finally{
            return( !caughtSecurityException );
        }
    }

    public void startProducerConsumerAudioRecorder(){
        producerConsumerAudioExecutorService = Executors.newFixedThreadPool( 2 );

        try{
            if( ! newRecordingName.endsWith( ".wav" ) ){
                newRecordingName += ".wav";
            }
            Log.d( RECORDING_FUNCTIONALITY_MANAGER_TAG, SPEECH_FRAGMENT_STORAGE_DIRECTORY_FULL_PATH );
            currentSpeechFragmentFilePathname = SPEECH_FRAGMENT_STORAGE_DIRECTORY_FULL_PATH + "/" + newRecordingName;
            speechFragmentFileReference = new RandomAccessFile( currentSpeechFragmentFilePathname, "rw" );

            Log.v( RECORDING_FUNCTIONALITY_MANAGER_TAG, "File opened: " + currentSpeechFragmentFilePathname );
        } catch( FileNotFoundException e ) {
            Log.v( RECORDING_FUNCTIONALITY_MANAGER_TAG, "Could not open recording output file " + currentSpeechFragmentFilePathname + " for writing!" );
        }

        try{
            ArrayBlockingQueue< byte[] > passingQueue = new ArrayBlockingQueue< byte[] >(64 );
            Thread audioRecordProducer = new Thread( new StereoPCMAudioRecordProducer( passingQueue ) );
            Thread audioRecordConsumer = new Thread( new AudioRecordConsumer( passingQueue, speechFragmentFileReference ) );

            producerConsumerAudioExecutorService.execute( audioRecordProducer );
            producerConsumerAudioExecutorService.execute( audioRecordConsumer );
        } catch( AudioRecordCreationException e ){
            Log.v( RECORDING_FUNCTIONALITY_MANAGER_TAG, "Creation of the audio recording producer-consumer system failed. Message: " + e.getMessage() );
        }
    }

    public void stopProducerConsumerAudioRecorder(){
        producerConsumerAudioExecutorService.shutdownNow();

        try{
            if( speechFragmentFileReference != null ) {
                speechFragmentFileReference.close();
            }
        } catch( IOException e ){
            Log.v( RECORDING_FUNCTIONALITY_MANAGER_TAG, "Could not close recording output file " + currentSpeechFragmentFilePathname + " !" );
        }
    }

    public void newRecording( ){
        newRecordingNameDialog = new RecordingNameDialog( activityContext,
                                                          R.layout.recording_filename_pop_up,
                                                          R.id.new_recording_filename_input,
                                                          R.id.new_recording_filename_submit,
                                                          R.id.new_recording_filename_cancel );
        newRecordingNameDialog.setDismissHandler( recordingNameDialogDismissHandler );

        newRecordingNameDialog.showDialog();
    }

    public void startRecording(){
        startProducerConsumerAudioRecorder();
    }

    public void stopRecording(){
        stopProducerConsumerAudioRecorder();
    }

    public void keepRecording(){
        recordPlaybackTabModel.getRecordingsDatabase().insertRecording( newRecordingName );
        recordPlaybackTabModel.getStoredAudioRecordingsAdapter().notifyDataSetChanged();
    }

    public void discardRecording(){
        new File( currentSpeechFragmentFilePathname ).delete();
    }
}
