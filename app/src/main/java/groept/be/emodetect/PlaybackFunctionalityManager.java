package groept.be.emodetect;

import java.util.ArrayList;
import java.util.HashSet;

import android.util.Log;
import android.util.Pair;

import groept.be.emodetect.helpers.recordinghelpers.RecordingsListAdapterObserver;
import groept.be.emodetect.helpers.recordinghelpers.Stereo16BitPCMAudioFilePlayer;

public class PlaybackFunctionalityManager implements RecordingsListAdapterObserver {
    private HashSet< Stereo16BitPCMAudioFilePlayer > recordingsPlayers;

    public PlaybackFunctionalityManager(){
        this.recordingsPlayers = new HashSet< Stereo16BitPCMAudioFilePlayer >( );
    }

    @Override
    public void update( ArrayList< Pair<String, Stereo16BitPCMAudioFilePlayer > > newRecordingsBaseObjects ){
        for( Pair< String, Stereo16BitPCMAudioFilePlayer > currentBaseObjectsPair : newRecordingsBaseObjects ){
            if( currentBaseObjectsPair.second != null ){
                recordingsPlayers.add( currentBaseObjectsPair.second );
            }
        }
    }

    private void stopAllExcept( Stereo16BitPCMAudioFilePlayer except ){
        for( Stereo16BitPCMAudioFilePlayer currentPlayer : recordingsPlayers ){
            if( ( currentPlayer != null ) &&
                ( currentPlayer != except ) ){
                currentPlayer.stop();
            }
        }
    }

    private void stopAll(){
        for( Stereo16BitPCMAudioFilePlayer currentPlayer : recordingsPlayers ){
            if( currentPlayer != null ) {
                currentPlayer.stop();
            }
        }
    }

    public void letPlay( Stereo16BitPCMAudioFilePlayer player ){
        if( player != null ){
            stopAllExcept( player );
            Log.d( "EXTRA_SPECIAL", "stopAllExcept() done!" );
            player.play();
        }
    }

    public void letPause( Stereo16BitPCMAudioFilePlayer player ){
        if( player != null ){
            player.pause();
        }
    }

    public void letStop( Stereo16BitPCMAudioFilePlayer player ){
        if( player != null ){
            player.stop();
        }
    }
}
