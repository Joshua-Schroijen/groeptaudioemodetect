package groept.be.emodetect.fragments.models;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import groept.be.emodetect.helpers.analysishelpers.FeatureAnalysisManager;
import groept.be.emodetect.helpers.miscellaneous.ExceptionHandler;
import groept.be.emodetect.uihelpers.AnalyzablesListAdapter;

public class TestAnalyzeTabModel extends AndroidViewModel implements ExceptionHandler {
    public final static String TEST_ANALYZE_TAB_MODEL_TAG = "TestAnalyzeTabModel";

    private static TestAnalyzeTabModel currentInstance = null;
    public static TestAnalyzeTabModel getCurrentInstance(){
        return( TestAnalyzeTabModel.currentInstance );
    }

    private Context applicationContext;

    private FeatureAnalysisManager storedFeatureAnalysisManager;
    private AnalyzablesListAdapter storedAnalyzableRecordingsAdapter;

    public TestAnalyzeTabModel( Application application ){
        super( application );

        TestAnalyzeTabModel.currentInstance = this;

        this.applicationContext = application;

        this.storedFeatureAnalysisManager =
            new FeatureAnalysisManager( applicationContext );
        try {
            this.storedFeatureAnalysisManager.logToOutputFile(
                new File(
                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/GroepT/SpeechEmotionDetection/features_output.dat"
                )
            );
        } catch( IOException e ) {
        }

        this.storedAnalyzableRecordingsAdapter =
            new AnalyzablesListAdapter(
                applicationContext,
                this,
                this.storedFeatureAnalysisManager
            );
    }

    public FeatureAnalysisManager getStoredFeatureAnalysisManager(){
        return( this.storedFeatureAnalysisManager );
    }

    public AnalyzablesListAdapter getStoredAnalyzableRecordingsAdapter(){
        return( this.storedAnalyzableRecordingsAdapter );
    }

    @Override
    public void handleException( Exception exception ){
        Log.v(
            TEST_ANALYZE_TAB_MODEL_TAG,
            ( "FATAL EXCEPTION CAUSED PROGRAM TO TERMINATE!\n" +
              exception.getMessage() )
        );

        System.exit( 2 );
    }
}
