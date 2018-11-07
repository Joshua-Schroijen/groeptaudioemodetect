package groept.be.emodetect.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import groept.be.emodetect.R;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.io.File;

import groept.be.emodetect.RecordingFunctionalityManager;
import groept.be.emodetect.fragments.models.RecordPlaybackTabModel;
import groept.be.emodetect.helpers.databasehelpers.RecordingsDatabaseHelper;
import groept.be.emodetect.uihelpers.PlaybackController;
import groept.be.emodetect.uihelpers.RecordingController;
import groept.be.emodetect.uihelpers.RecordingsListAdapter;

public class RecordPlaybackTabFragment extends Fragment {
    public static final String RECORD_PLAYBACK_TAB_FRAGMENT_TAG = "RecPlayTabFragment";

    private RecordPlaybackTabModel recordPlaybackTabModel;

    private View fragmentRootView;

    private RecordingController recordingController;
    private ListView storedAudioRecordingsListView;

    private AdapterView.OnItemClickListener recordingsListOnItemClickListener = new AdapterView.OnItemClickListener(){
        PlaybackController previouslySelectedViewPlaybackController;
        ImageButton previouslySelectedViewDeleteButton;

        @Override
        public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
            final RecordPlaybackTabModel recordPlaybackTabModel = ViewModelProviders.of( RecordPlaybackTabFragment.this ).get( RecordPlaybackTabModel.class );
            final RecordingsDatabaseHelper recordingsDatabaseHelper = recordPlaybackTabModel.getRecordingsDatabase();
            final RecordingsListAdapter recordingsListAdapter = recordPlaybackTabModel.getStoredAudioRecordingsAdapter();
            final String currentlySelectedRecordingName =
                    ( ( String ) recordingsListAdapter.getItem( position ) );

            PlaybackController currentPlaybackController = view.findViewById( R.id.recordings_list_item_buttons );
            ImageButton currentDeleteButton = view.findViewById( R.id.recordings_list_item_delete_button );

            if( !( ( currentPlaybackController == previouslySelectedViewPlaybackController ) &&
                    ( currentDeleteButton == previouslySelectedViewDeleteButton ) ) ){
                if( previouslySelectedViewPlaybackController != null ){
                    previouslySelectedViewPlaybackController.endInteraction( true );
                }
                if( previouslySelectedViewDeleteButton != null ){
                    previouslySelectedViewDeleteButton.setVisibility( View.GONE );
                }

                currentPlaybackController.startInteraction();

                currentDeleteButton.setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick( View v ) {
                        recordingsDatabaseHelper.deleteRecording( currentlySelectedRecordingName );
                        new File( RecordingFunctionalityManager.getRecordingFilename( currentlySelectedRecordingName ) ).delete();
                        recordingsListAdapter.notifyDataSetChanged();
                    }
                } );
                currentDeleteButton.setVisibility( View.VISIBLE );
            } else {
                currentPlaybackController.toggleVisibility();

                if( currentDeleteButton.getVisibility() == View.VISIBLE ){
                    currentDeleteButton.setVisibility( View.GONE );
                } else if( currentDeleteButton.getVisibility() == View.GONE ){
                    currentDeleteButton.setVisibility( View.VISIBLE );
                }
            }

            previouslySelectedViewPlaybackController = currentPlaybackController;
            previouslySelectedViewDeleteButton = currentDeleteButton;
        }
    };

    private void insertRecordingController( RecordingController recordingControllerToInsert ){
        if( fragmentRootView != null ) {
            View recordingControllerView = fragmentRootView.findViewById( R.id.recording_activity_controller );
            ViewGroup recordingControllerViewParent = ( ViewGroup )( recordingControllerView.getParent() );
            int recordingControllerViewIndex = recordingControllerViewParent.indexOfChild( recordingControllerView );
            recordingControllerViewParent.removeView( recordingControllerView );
            recordingControllerViewParent.addView( recordingControllerToInsert, recordingControllerViewIndex );
        }
    }

    private void prepareStoredAudioRecordingsList(){
        storedAudioRecordingsListView = ( ( ListView )( fragmentRootView.findViewById( R.id.stored_audio_recordings ) ) );

        storedAudioRecordingsListView.setOnItemClickListener( recordingsListOnItemClickListener );
        storedAudioRecordingsListView.setAdapter( recordPlaybackTabModel.getStoredAudioRecordingsAdapter() );
    }

    public RecordPlaybackTabFragment() {
        super();
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ){
        this.fragmentRootView = inflater.inflate( R.layout.fragment_record_and_playback_tab, null );

        this.recordPlaybackTabModel = ViewModelProviders.of( this ).get( RecordPlaybackTabModel.class );
        this.recordPlaybackTabModel.setupStatefulElements( getActivity() );

        this.recordingController = recordPlaybackTabModel.getRecordingController();
        insertRecordingController( recordingController );
        this.recordingController.show();

        prepareStoredAudioRecordingsList();

        this.recordPlaybackTabModel.
            getRecordingFunctionalityManager().
            tryToCreateSpeechFragmentStorageDirectory();

        return( fragmentRootView );
    }

    public RecordPlaybackTabModel getRecordPlaybackTabModel(){
        return( recordPlaybackTabModel );
    }
}
