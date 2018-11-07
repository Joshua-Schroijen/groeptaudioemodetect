package groept.be.emodetect.helpers.recordinghelpers;

import java.util.ArrayList;
import java.util.HashSet;

import android.media.AudioTrack;
import android.os.Handler;
import android.util.Log;

import groept.be.emodetect.helpers.miscellaneous.ArrayTools;
import groept.be.emodetect.helpers.miscellaneous.ListTools;
import groept.be.emodetect.helpers.fileformathelpers.WAVFile;

public class Stereo16BitPCMAudioFilePlayer implements AudioFilePlayer {
    public static final String STEREO_PCM_AUDIO_FILE_PLAYER_TAG = "SterPCMAudFilePlayer";

    public enum ActionToExecute{
        SHOULD_START,
        SHOULD_PLAY,
        SHOULD_PAUSE,
        SHOULD_STOP,
        SHOULD_FINISH
    };

    private Stereo16BitPCMAudioFilePlayerExceptionHandler threadExceptionHandler;

    private String currentlyPlaying;
    private Stereo16BitPCMFileAudioTrackFactory audioTrackFactory;
    private AudioTrack audioTrack;

    private ActionToExecute command;

    private Runnable playerThreadRunnable = new Runnable(){
        @Override
        public void run(){
            int currentSampleBlockStartIndex = 0;

            ArrayList<Short> WAVFileSamples = null;
            int bufferLength = 0;
            Short[] buffer = null;

            while( true ){
                try{
                    if( command == ActionToExecute.SHOULD_START ){
                        Log.d(
                             STEREO_PCM_AUDIO_FILE_PLAYER_TAG,
                             "Thread playing " +
                             currentlyPlaying +
                             "\nCommand = SHOULD_START" );

                        if( Thread.interrupted() == true ){ throw new InterruptedException(); }
                        notifyObserversWithTag( observersContainingThreadHandler, "STARTER" );

                        Log.d(
                                STEREO_PCM_AUDIO_FILE_PLAYER_TAG,
                                "Thread playing " +
                                        currentlyPlaying +
                                        "\nObservers notified!" );
                        if( Thread.interrupted() == true ){ throw new InterruptedException(); }
                        bufferLength = ( audioTrackFactory.getUsedAudioRecordBufferByteSize() / 2 );
                        buffer = new Short[ bufferLength ];

                        Log.d(
                                STEREO_PCM_AUDIO_FILE_PLAYER_TAG,
                                "Thread playing " +
                                        currentlyPlaying +
                                        "\nBuffer obtained" );

                        if( Thread.interrupted() == true ){ throw new InterruptedException(); }
                        WAVFileSamples = null;
                        try {
                            WAVFile playingWAVFile = WAVFile.getWAVFileFromFile( audioTrackFactory.getFilename() );
                            WAVFileSamples = ( ArrayList< Short > )( playingWAVFile.getPCMData() );
                        } catch( Exception e ){
                            threadExceptionHandler.handleException( e );
                            Log.d(
                                    STEREO_PCM_AUDIO_FILE_PLAYER_TAG,
                                    "Thread playing " +
                                            currentlyPlaying +
                                            "\nEXCEPTION CAUGHT! " + e.getMessage() );
                        }


                        Log.d(
                                STEREO_PCM_AUDIO_FILE_PLAYER_TAG,
                                "Thread playing " +
                                        currentlyPlaying +
                                        "\nWAV file samples loaded!" );
                        if( Thread.interrupted() == true ){ throw new InterruptedException(); }
                        currentSampleBlockStartIndex = 0;

                        if( Thread.interrupted() == true ){ throw new InterruptedException(); }
                        command = ActionToExecute.SHOULD_PLAY;
                    }
                    if( command == ActionToExecute.SHOULD_PLAY ){
                        Log.d(
                             STEREO_PCM_AUDIO_FILE_PLAYER_TAG,
                                  "Thread playing " +
                             currentlyPlaying +
                             "\nCommand = SHOULD_PLAY" );

                        Log.d( "EXTRA_SPECIAL", "Should play reached!" );

                        notifyObserversWithTag( observersContainingThreadHandler, "PLAYER" );

                        if( audioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING ){
                            audioTrack.play();
                            Log.d( STEREO_PCM_AUDIO_FILE_PLAYER_TAG , "Audio streaming started" );
                        }

                        while( ! ( currentSampleBlockStartIndex >= WAVFileSamples.size() ) ){
                            int currentSampleBlockLength =
                                (
                                    Math.min(
                                        bufferLength,
                                        ( WAVFileSamples.size() - currentSampleBlockStartIndex )
                                    )
                                );
                            int currentSampleBlockEndIndex = ( ( currentSampleBlockStartIndex + currentSampleBlockLength ) - 1 );

                            ListTools.<Short>getFromTo(
                                    WAVFileSamples,
                                    buffer,
                                    currentSampleBlockStartIndex,
                                    currentSampleBlockEndIndex);

                            audioTrack.write(
                                    ArrayTools.shortObjectArrayToShortArray(buffer),
                                    0,
                                    currentSampleBlockLength);

                            currentSampleBlockStartIndex += bufferLength;

                            if( Thread.interrupted() ){
                                throw new InterruptedException();
                            }
                        }

                        command = ActionToExecute.SHOULD_FINISH;
                        Thread.currentThread().interrupt();
                    }
                    if( command == ActionToExecute.SHOULD_PAUSE ){
                        Log.d(
                             STEREO_PCM_AUDIO_FILE_PLAYER_TAG,
                             "Thread playing " +
                             currentlyPlaying +
                             "\nCommand = SHOULD_PAUSE" );

                        notifyObserversWithTag( observersContainingThreadHandler, "PAUSER" );

                        audioTrack.pause();

                        while( ! Thread.interrupted() ){
                        }

                        throw new InterruptedException();
                    }
                    if( command == ActionToExecute.SHOULD_STOP ){
                        Log.d(
                             STEREO_PCM_AUDIO_FILE_PLAYER_TAG,
                             "Thread playing " +
                             currentlyPlaying +
                             "\nCommand = SHOULD_STOP" );

                        Log.d( "SPECIAL", "SIGNALLING STOP NOW!" );
                        notifyObserversWithTag( observersContainingThreadHandler, "STOPPER" );
                        Log.d( "SPECIAL", "STOP SHOULD BE SIGNALLED!" );

                        /* In a clean way we immediately stop playback and discard all
                         * samples left in the buffer
                         */
                        audioTrack.pause();
                        audioTrack.flush();
                        audioTrack.release();

                        currentSampleBlockStartIndex = 0;

                        command = ActionToExecute.SHOULD_START;

                        return;
                    }
                    if( command == ActionToExecute.SHOULD_FINISH ){
                        Log.d(
                             STEREO_PCM_AUDIO_FILE_PLAYER_TAG,
                             "Thread playing" +
                             currentlyPlaying +
                             "\nCommand = SHOULD_FINISH" );

                        notifyObserversWithTag( observersContainingThreadHandler, "FINISHER" );

                        /* In a clean way we immediately stop playback and discard all
                         * samples left in the buffer
                         */
                        audioTrack.pause();
                        audioTrack.flush();
                        audioTrack.release();

                        currentSampleBlockStartIndex = 0;

                        command = ActionToExecute.SHOULD_START;

                        return;
                    }
                } catch( InterruptedException e ){
                }
            }
        }
    };

    private Thread playerThread;

    private HashSet< Stereo16BitPCMAudioFilePlayerObserver > observers;

    private Handler observersContainingThreadHandler;

    public Stereo16BitPCMAudioFilePlayer( String filename, Handler observersContainingThreadHandler ){
        this.threadExceptionHandler = new Stereo16BitPCMAudioFilePlayerExceptionHandler();
        this.observers = new HashSet< Stereo16BitPCMAudioFilePlayerObserver >();
        this.observersContainingThreadHandler = observersContainingThreadHandler;

        this.currentlyPlaying = filename;

        this.command = ActionToExecute.SHOULD_START;

        Log.d( STEREO_PCM_AUDIO_FILE_PLAYER_TAG,
             "NEW PLAYER CONSTRUCTED! Playing " + filename );
    }

    public Stereo16BitPCMAudioFilePlayer( String filename ){
        this( filename, null );
    }

    @Override
    public void play(){
        Log.d( STEREO_PCM_AUDIO_FILE_PLAYER_TAG, "play() called!" );

        Log.d( STEREO_PCM_AUDIO_FILE_PLAYER_TAG, "ActionToExecute is " + command.name() );

        if( command == ActionToExecute.SHOULD_START ){
            Log.d( STEREO_PCM_AUDIO_FILE_PLAYER_TAG, "play() in command == SHOULD_START" );

            try {
                this.audioTrackFactory = new Stereo16BitPCMFileAudioTrackFactory( currentlyPlaying );
                this.audioTrack = audioTrackFactory.buildAudioTrack();
            } catch( AudioTrackCreationException e ){
                threadExceptionHandler.handleException( e );
            }

            playerThread = new Thread( playerThreadRunnable );
            Log.d(
                    STEREO_PCM_AUDIO_FILE_PLAYER_TAG,
                    "Thread playing " +
                            currentlyPlaying +
                            "\nBefore: playerThread.isAlive() = " +
                            playerThread.isAlive()
            );
            playerThread.start();
            Log.d(
                    STEREO_PCM_AUDIO_FILE_PLAYER_TAG,
                    "Thread playing " +
                    currentlyPlaying +
                    "\nAfter: playerThread.isAlive() = " +
                    playerThread.isAlive()
            );
        } else if( ( command == ActionToExecute.SHOULD_PAUSE ) ||
                   ( command == ActionToExecute.SHOULD_PLAY ) ){
            Log.d(STEREO_PCM_AUDIO_FILE_PLAYER_TAG, "play() in command != SHOULD_START");
            Log.d(
                    STEREO_PCM_AUDIO_FILE_PLAYER_TAG,
                    "Thread playing " +
                            currentlyPlaying +
                            "\nplayerThread.isAlive() = " +
                            playerThread.isAlive()
            );
            command = ActionToExecute.SHOULD_PLAY;
            playerThread.interrupt();
        }
    }

    @Override
    public void pause(){
        if( playerThread != null ) {
            command = ActionToExecute.SHOULD_PAUSE;
            playerThread.interrupt();
        }
    }

    @Override
    public void stop(){
        Log.d(
                STEREO_PCM_AUDIO_FILE_PLAYER_TAG,
                "Thread playing " +
                        currentlyPlaying +
                        "\nstop() called!" );

        if( ( playerThread != null ) && /*
            ( command != ActionToExecute.SHOULD_START*/
            ( playerThread.isAlive() == true ) ){
            Log.d(
                    STEREO_PCM_AUDIO_FILE_PLAYER_TAG,
                    "Thread playing " +
                            currentlyPlaying +
                            "\nGoing to stop!" );

            command = ActionToExecute.SHOULD_STOP;
            playerThread.interrupt();
        }
    }

    public void registerObserver( Stereo16BitPCMAudioFilePlayerObserver newObserver ){
        boolean result = observers.add( newObserver );

        if( result == true ){
            Log.d( STEREO_PCM_AUDIO_FILE_PLAYER_TAG, "Effectively added observer!" );
        } else {
            Log.d( STEREO_PCM_AUDIO_FILE_PLAYER_TAG, "Observer was already in observer set!" );
        }
    }

    public void unregisterObserver( Stereo16BitPCMAudioFilePlayerObserver observerToRemove ){
        observers.remove( observerToRemove );
    }

    public void notifyObservers(){
        ActionToExecute savedCommand = command;
        for( Stereo16BitPCMAudioFilePlayerObserver currentObserver : observers ){
            currentObserver.update( savedCommand );
        }
    }

    public void notifyObservers( Handler observersContainingThreadHandler ){
        if( observersContainingThreadHandler == null ){
            notifyObservers();
        } else {
            final ActionToExecute savedCommand = command;
            observersContainingThreadHandler.post( new Runnable(){
                @Override
                public void run(){
                    Log.d( "SPECIAL", "Not signalling stop! Signalling " + savedCommand.name() );
                    for( Stereo16BitPCMAudioFilePlayerObserver currentObserver : Stereo16BitPCMAudioFilePlayer.this.observers ){
                        currentObserver.update( savedCommand );
                    }
                }
            } );
        }
    }

    public void notifyObserversWithTag( Handler observersContainingThreadHandler, String tag ){
        final String theTag = tag;

        if( observersContainingThreadHandler == null ){
            notifyObservers();
        } else {
            final ActionToExecute savedCommand = command;
            observersContainingThreadHandler.post( new Runnable(){
                @Override
                public void run(){
                    if( savedCommand == ActionToExecute.SHOULD_STOP ){
                        Log.d( "SPECIAL", theTag + ": SIGNALLING STOP!!!!" );
                    } else {
                        Log.d( "SPECIAL", theTag + ": Not signalling stop! Signalling " + savedCommand.name() );
                    }
                    for( Stereo16BitPCMAudioFilePlayerObserver currentObserver : Stereo16BitPCMAudioFilePlayer.this.observers ){
                        currentObserver.update( savedCommand );
                    }
                }
            } );
        }
    }
}
