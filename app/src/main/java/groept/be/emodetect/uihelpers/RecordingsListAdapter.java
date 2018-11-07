package groept.be.emodetect.uihelpers;

import java.util.ArrayList;
import java.util.HashSet;

import android.content.Context;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import groept.be.emodetect.PlaybackFunctionalityManager;
import groept.be.emodetect.R;
import groept.be.emodetect.RecordingFunctionalityManager;
import groept.be.emodetect.helpers.miscellaneous.ExceptionHandler;
import groept.be.emodetect.helpers.databasehelpers.RecordingsDatabaseHelper;
import groept.be.emodetect.helpers.recordinghelpers.RecordingsListAdapterObserver;
import groept.be.emodetect.helpers.recordinghelpers.Stereo16BitPCMAudioFilePlayer;

public class RecordingsListAdapter extends BaseAdapter {
    public static final String RECORDINGS_LIST_ADAPTER_TAG = "RecordingsListAdapter";

    private static class StringStereo16BitPCMAudioFilePlayerPair extends Pair< String, Stereo16BitPCMAudioFilePlayer >{
        public StringStereo16BitPCMAudioFilePlayerPair( String first, Stereo16BitPCMAudioFilePlayer second ){
            super( first, second );
        }

        public boolean equals( StringStereo16BitPCMAudioFilePlayerPair otherPair ){
            return( otherPair.first.equals( this.first ) );
        }
    }

    private boolean initiailzing;
    private Context context;
    private Handler playerStateHandler;
    private LayoutInflater layoutInflater;
    private ExceptionHandler exceptionHandler;
    private RecordingsDatabaseHelper recordingsDatabase;
    private PlaybackFunctionalityManager playbackFunctionalityManager;
    private ArrayList< Pair< String, Stereo16BitPCMAudioFilePlayer > > recordingsBaseObjects;
    private HashSet< RecordingsListAdapterObserver > recordingsListAdapterObservers;

    private boolean recordingsBaseObjectsContains( String recordingName ){
        boolean containsRecordingWithName = false;

        for( Pair< String, Stereo16BitPCMAudioFilePlayer > currentRecordingBaseObject : recordingsBaseObjects ){
            if( recordingName.equals( currentRecordingBaseObject.first ) ){
                containsRecordingWithName = true;
                break;
            }
        }

        return( containsRecordingWithName );
    }

    public RecordingsListAdapter(
            Context context,
            Handler playerStateHandler,
            ExceptionHandler exceptionHandler,
            PlaybackFunctionalityManager playbackFunctionalityManager,
            RecordingsDatabaseHelper recordingsDatabase ){
        this.initiailzing = true;

        this.context = context;
        this.playerStateHandler = playerStateHandler;
        this.exceptionHandler = exceptionHandler;
        this.playbackFunctionalityManager = playbackFunctionalityManager;
        this.recordingsDatabase = recordingsDatabase;

        layoutInflater = ( LayoutInflater ) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        this.recordingsListAdapterObservers = new HashSet< RecordingsListAdapterObserver >( );

        if( recordingsDatabase == null ){
            exceptionHandler.handleException( new IllegalArgumentException( "Valid database must be passed as data source for RecordingsListAdapter" ) );
        } else {
            this.recordingsBaseObjects = new ArrayList< Pair< String, Stereo16BitPCMAudioFilePlayer > >();
            setupRecordingsBaseObjects();
        }

        registerObserver( playbackFunctionalityManager );
        notifyObservers();

        this.initiailzing = false;
    }

    @Override
    public int getCount(){
        return( recordingsBaseObjects.size() );
    }

    @Override
    public Object getItem( int position ){
        return( recordingsBaseObjects.get( position ).first );
    }

    @Override
    public long getItemId( int position ) {
        return( position );
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent ){
        View newRowView = layoutInflater.inflate( R.layout.recordings_list_item, parent, false );

        String recordingName = recordingsBaseObjects.get( position ).first;
        TextView recordingNameView = ( TextView ) newRowView.findViewById( R.id.recordings_list_item_recording_name );
        recordingNameView.setText( recordingName );

        PlaybackController recordingPlaybackController = newRowView.findViewById( R.id.recordings_list_item_buttons );
        recordingPlaybackController.setName( recordingName );
        final Stereo16BitPCMAudioFilePlayer accompanyingAudioFilePlayer =
            recordingsBaseObjects.get( position ).second;
        recordingPlaybackController.setOnPlayButtonClickListener( new View.OnClickListener(){
            @Override
            public void onClick( View v ){
                playbackFunctionalityManager.letPlay( accompanyingAudioFilePlayer );
            }
        } );
        recordingPlaybackController.setOnPauseButtonClickListener( new View.OnClickListener(){
            @Override
            public void onClick( View v ){
                playbackFunctionalityManager.letPause( accompanyingAudioFilePlayer );
            }
        } );
        recordingPlaybackController.setOnStopButtonClickListener( new View.OnClickListener(){
            @Override
            public void onClick( View v ){
                playbackFunctionalityManager.letStop( accompanyingAudioFilePlayer );
            }
        } );
        accompanyingAudioFilePlayer.registerObserver( recordingPlaybackController );

        return( newRowView );
    }

    @Override
    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();

        setupRecordingsBaseObjects();
        notifyObservers();
    }

    public void setupRecordingsBaseObjects(){
        ArrayList< String > fetchedRecordings = recordingsDatabase.getKeptRecordings();

        /* When not initializing - add all recordings that were added but are
         * not yet stored as such in the list
         * When initializing - initially populate the recordings list adapter
         */
        for( String currentRecordingName : fetchedRecordings ){
            if( ! recordingsBaseObjectsContains( currentRecordingName ) ){
                recordingsBaseObjects.add(
                    new Pair<String, Stereo16BitPCMAudioFilePlayer>(
                        currentRecordingName,
                        new Stereo16BitPCMAudioFilePlayer(
                            RecordingFunctionalityManager.getRecordingFilename(currentRecordingName),
                            playerStateHandler)
                    )
                );
            }
        }

        /* When not initializing we remove all records that were deleted
         * but are still erroneously stored in the recordings list adapter
         */
        if( initiailzing == false ){
            ArrayList< Pair< String, Stereo16BitPCMAudioFilePlayer > > toRemoveList =
                    new ArrayList< Pair <String, Stereo16BitPCMAudioFilePlayer > >();
            for( Pair< String, Stereo16BitPCMAudioFilePlayer > currentRecordingBaseObject : recordingsBaseObjects ){
                if( ! fetchedRecordings.contains( currentRecordingBaseObject.first ) ){
                    toRemoveList.add( currentRecordingBaseObject );
                }
            }
            recordingsBaseObjects.removeAll( toRemoveList );
        }
    }

    public ArrayList< Pair< String, Stereo16BitPCMAudioFilePlayer > > getRecordingsBaseObjects(){
        return( recordingsBaseObjects );
    }

    public void registerObserver( RecordingsListAdapterObserver newObserver ){
        recordingsListAdapterObservers.add( newObserver );
    }

    public void unregisterObserver( RecordingsListAdapterObserver observerToRemove ) {
        recordingsListAdapterObservers.remove( observerToRemove );
    }

    public void notifyObservers(){
        for( RecordingsListAdapterObserver currentObserver : recordingsListAdapterObservers ){
            if( currentObserver != null ){
                currentObserver.update( recordingsBaseObjects );
            }
        }
    }
}
