package groept.be.emodetect.helpers.analysishelpers;

import android.os.Environment;
import android.content.Context;
import android.support.v4.util.Pair;
import android.util.Log;

import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

import be.tarsos.dsp.io.android.AndroidFFMPEGLocator;

import groept.be.emodetect.helpers.databasehelpers.featuresdatabases.FeaturesDatabaseHelper;
import groept.be.emodetect.helpers.databasehelpers.featuresdatabases.MfccsDatabaseHelper;

/* BELANGRIJK OM OVER NA TE DENKEN - INVALIDE STAAT NA SLUITEN? */
public class FeatureAnalysisManager {
    private static final String FEATURE_ANALYSIS_MANAGER_TAG = "FeatureAnalysisManager";

    private static final String RECORDING_FILENAME_PREFIX = Environment.getExternalStorageDirectory().getAbsolutePath() + "/GroepT/SpeechEmotionDetection/";

    private Context applicationContext;

    private BufferedWriter outputFileBufferedWriter = null;

    private FeaturesDatabaseHelper selectedFeaturesDatabaseHelper;

    private ArrayList< FeatureExtractor > featureExtractors;

    private void writeFeaturesToOutputFile( float[] featureValues, int frameNumber ) throws IOException{
        String featureOutputString = "";

        featureOutputString = "Frame number: " + frameNumber + ": ";
        for( float currentFeatureValue : featureValues ){
            featureOutputString += currentFeatureValue;
            featureOutputString += " ";
        }
        featureOutputString += "\n";

        this.outputFileBufferedWriter.
            write( featureOutputString );
        this.outputFileBufferedWriter.flush();
    }

    public FeatureAnalysisManager( Context applicationContext ){
        this.applicationContext = applicationContext;

        MfccExtractor defaultMfccExtractor =
            new MfccExtractor( 44100, 1024, 128, 40, 50, 300, 3000 );
        this.featureExtractors = new ArrayList< FeatureExtractor >();
        this.addFeatureExtractor( defaultMfccExtractor );

        MfccsDatabaseHelper mfccsDatabaseHelper = new MfccsDatabaseHelper( applicationContext, "emotion_detection_database.db" );
        mfccsDatabaseHelper.createTableIfNeeded();
        this.selectedFeaturesDatabaseHelper = mfccsDatabaseHelper;

        new AndroidFFMPEGLocator( this.applicationContext );
    }

    public void logToOutputFile( File outputFileReference ) throws IOException{
        if( !outputFileReference.exists() ){
            outputFileReference.createNewFile();
        }

        this.outputFileBufferedWriter =
                new BufferedWriter(
                        new FileWriter(
                                outputFileReference,
                                true
                        )
                );
    }

    public void addFeatureExtractor( FeatureExtractor featureExtractorToAdd ){
        featureExtractors.add( featureExtractorToAdd );
    }

    public void extractFeaturesToOutputFile( String recordingFilename ) throws IOException{
        if( outputFileBufferedWriter != null ){
            for( FeatureExtractor currentFeatureExtractor : featureExtractors ){
                ArrayList< Pair< Integer, float[] > > extractedFeatures =
                    currentFeatureExtractor.extractFeatures( recordingFilename );
                int currentFrameNumber = 1;

                for( Pair< Integer, float[] > currentFrameExtractedFeatures : extractedFeatures ){
                    this.writeFeaturesToOutputFile( currentFrameExtractedFeatures.second, currentFrameNumber );
                    ++currentFrameNumber;
                }
            }
        }
    }

    public void extractFeaturesToOutputFile( String[] recordingFilenames ) throws IOException{
        for( String currentRecordingFilename : recordingFilenames ) {
            if( outputFileBufferedWriter != null ){
                for( FeatureExtractor currentFeatureExtractor : featureExtractors ){
                    ArrayList< Pair< Integer, float[] > > extractedFeatures =
                        currentFeatureExtractor.extractFeatures( currentRecordingFilename );
                    int currentFrameNumber = 1;

                    for( Pair< Integer, float[] > currentFrameExtractedFeatures : extractedFeatures ){
                        this.writeFeaturesToOutputFile( currentFrameExtractedFeatures.second, currentFrameNumber );
                        ++currentFrameNumber;
                    }
                }
            }
        }
    }

    public void extractFeaturesToOutputFile( ArrayList< String > recordingFilenames ) throws IOException{
        for( String currentRecordingFilename : recordingFilenames ){
            if( outputFileBufferedWriter != null ){
                currentRecordingFilename =
                    FeatureAnalysisManager.RECORDING_FILENAME_PREFIX + currentRecordingFilename;
                for( FeatureExtractor currentFeatureExtractor : featureExtractors ){
                    ArrayList<Pair < Integer, float[] > > extractedFeatures =
                        currentFeatureExtractor.extractFeatures( currentRecordingFilename );
                    int currentFrameNumber = 1;

                    for( Pair< Integer, float[] > currentFrameExtractedFeatures : extractedFeatures ){
                        this.writeFeaturesToOutputFile( currentFrameExtractedFeatures.second, currentFrameNumber );
                        ++currentFrameNumber;
                    }
                }
            }
        }
    }

    /*
    public FeatureAnalysisManager( Context applicationContext ){
        PreferenceManager.
            getDefaultSharedPreferences( applicationContext ).get
            getString( WEB_SERVICE_URL_KEY, DEFAULT_WEB_SERVICE_URL );

            return( webServiceURL );
        }
    }*/

    public ArrayList< String > getAnalyzableRecordings(){
        return( selectedFeaturesDatabaseHelper.getAnalyzableRecordings() );
    }

    public void freeResources(){
        if( this.outputFileBufferedWriter != null ){
            try {
                this.outputFileBufferedWriter.close();
            } catch( IOException e ){
                Log.d( "FEATURE_ANALYSIS_MANAGER_TAG", "Important error: could not close reference to output feature logging file! IOException caught with message: " + e.getMessage() );
            }
        }
    }
}
