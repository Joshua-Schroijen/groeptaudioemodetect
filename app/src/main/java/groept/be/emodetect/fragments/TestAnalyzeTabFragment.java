package groept.be.emodetect.fragments;

import android.util.Log;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Button;

import java.util.ArrayList;
import java.io.IOException;

import groept.be.emodetect.R;
import groept.be.emodetect.fragments.models.TestAnalyzeTabModel;
import groept.be.emodetect.uihelpers.dialogs.RecordingLabelingDialog;

public class TestAnalyzeTabFragment extends Fragment {
    private Context activityContext;

    private TestAnalyzeTabModel testAnalyzeTabModel;

    private ListView analyzableAudioRecordingsListView;
    private Button analyzeSelectedRecordingsButton;

    private RecordingLabelingDialog featureLabelingDialog;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ){
        View rootView = inflater.inflate( R.layout.fragment_analyze_tab, null );

        this.activityContext = getActivity();

        this.testAnalyzeTabModel = ViewModelProviders.of( this ).get( TestAnalyzeTabModel.class );

        analyzableAudioRecordingsListView =
            ( ListView )( rootView.findViewById( R.id.analyzable_audio_recordings ) );
        analyzableAudioRecordingsListView.setAdapter( testAnalyzeTabModel.getStoredAnalyzableRecordingsAdapter() );

        final TestAnalyzeTabModel testAnalyzeTabModelReference =
            this.testAnalyzeTabModel;
        this.analyzeSelectedRecordingsButton =
            rootView.findViewById( R.id.analyze_selected_recordings_button );
        this.analyzeSelectedRecordingsButton.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick( View view ){
                /**/
                TestAnalyzeTabFragment.this.featureLabelingDialog =
                    new RecordingLabelingDialog(
                        TestAnalyzeTabFragment.this.activityContext
                    );
                TestAnalyzeTabFragment.this.featureLabelingDialog.showDialog();
                /**/

                ArrayList< String > recordingsToAnalyze =
                    testAnalyzeTabModelReference.
                        getStoredAnalyzableRecordingsAdapter().
                        getSelectedAnalyzableRecordings();

                try {
                    testAnalyzeTabModelReference.
                        getStoredFeatureAnalysisManager().
                        extractFeaturesToOutputFile(recordingsToAnalyze);
                } catch( IOException e ){
                    AlertDialog ioErrorDialog =
                        ( new AlertDialog.Builder( TestAnalyzeTabFragment.this.activityContext ) )
                        .create();
                    ioErrorDialog.setTitle("A storage error occured");
                    ioErrorDialog.setMessage( ( "An error occured while writing the extracted feature data:\n" + e.getMessage() ) );
                    ioErrorDialog.setButton( AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        } );
                    ioErrorDialog.show();
                }
            }
        } );

        return( rootView );
    }
}
