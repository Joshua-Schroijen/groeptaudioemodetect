package groept.be.emodetect.helpers.databasehelpers.featuresdatabases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.support.v4.util.Pair;

import java.util.ArrayList;

import be.tarsos.dsp.mfcc.MFCC;
import groept.be.emodetect.helpers.databasehelpers.RecordingsDatabaseContract;

public class MfccsDatabaseHelper extends FeaturesDatabaseHelper {
    private static final String MFCC_FEATURES_DATABASE_HELPER_TAG = "MfccFeaturesDBHelper";

    private Long insertFeature( SQLiteDatabase mfccsDatabase, int recordingID, int frameOffset, double[] newFeature ){
        ContentValues contentValues = new ContentValues();
        contentValues.put( getContractFieldValue( "COLUMN_NAME_RECORDING_ID" ), recordingID );
        contentValues.put( getContractFieldValue( "COLUMN_NAME_FRAME_OFFSET" ), frameOffset );
        contentValues.put( getContractFieldValue( "COLUMN_NAME_COEFFICIENT_1" ), newFeature[ 0 ] );
        contentValues.put( getContractFieldValue( "COLUMN_NAME_COEFFICIENT_2" ), newFeature[ 1 ] );
        contentValues.put( getContractFieldValue( "COLUMN_NAME_COEFFICIENT_3" ), newFeature[ 2 ] );
        contentValues.put( getContractFieldValue( "COLUMN_NAME_COEFFICIENT_4" ), newFeature[ 3 ] );
        contentValues.put( getContractFieldValue( "COLUMN_NAME_COEFFICIENT_5" ), newFeature[ 4 ] );
        contentValues.put( getContractFieldValue( "COLUMN_NAME_COEFFICIENT_6" ), newFeature[ 5 ] );
        contentValues.put( getContractFieldValue( "COLUMN_NAME_COEFFICIENT_7" ), newFeature[ 6 ] );
        contentValues.put( getContractFieldValue( "COLUMN_NAME_COEFFICIENT_8" ), newFeature[ 7 ] );
        contentValues.put( getContractFieldValue( "COLUMN_NAME_COEFFICIENT_9" ), newFeature[ 8 ] );
        contentValues.put( getContractFieldValue( "COLUMN_NAME_COEFFICIENT_10" ), newFeature[ 9 ] );
        contentValues.put( getContractFieldValue( "COLUMN_NAME_COEFFICIENT_11" ), newFeature[ 10 ] );
        contentValues.put( getContractFieldValue( "COLUMN_NAME_COEFFICIENT_12" ), newFeature[ 11 ] );

        if( isAnalyzed( recordingID ) ){
            Log.d( MFCC_FEATURES_DATABASE_HELPER_TAG, recordingID + " was already recorded!" );
            return( null );
        } else {
            Log.d( MFCC_FEATURES_DATABASE_HELPER_TAG, recordingID + " was NOT already recorded!" );
            return( mfccsDatabase.insert( getContractFieldValue( "TABLE_NAME" ), null, contentValues ) );
        }
    }

    protected Class getContractClass(){
        return( MfccsDatabaseContract.class );
    }

    public MfccsDatabaseHelper( Context applicationContext, String featuresDatabaseFileName ){
        super( applicationContext, featuresDatabaseFileName );
    }

    public void createTableIfNeeded(){
        if( !checkIfTableExists() ) {
            /* Here we generate and execute the SQL query that creates our table
             * for storing extracted MFCC features
             */
            SQLiteDatabase mfccsDatabase = this.getWritableDatabase();
            String mfccsTableCreationQuery = "CREATE TABLE " +
                getContractFieldValue("TABLE_NAME") +
                " ( " +
                getContractFieldValue("COLUMN_NAME_ID") +
                " INTEGER PRIMARY KEY, " +
                getContractFieldValue("COLUMN_NAME_RECORDING_ID") +
                " INTEGER, " +
                getContractFieldValue("COLUMN_NAME_FRAME_OFFSET") +
                " INTEGER, " +
                getContractFieldValue("COLUMN_NAME_COEFFICIENT_1") +
                " REAL, " +
                getContractFieldValue("COLUMN_NAME_COEFFICIENT_2") +
                " REAL, " +
                getContractFieldValue("COLUMN_NAME_COEFFICIENT_3") +
                " REAL, " +
                getContractFieldValue("COLUMN_NAME_COEFFICIENT_4") +
                " REAL, " +
                getContractFieldValue("COLUMN_NAME_COEFFICIENT_5") +
                " REAL, " +
                getContractFieldValue("COLUMN_NAME_COEFFICIENT_6") +
                " REAL, " +
                getContractFieldValue("COLUMN_NAME_COEFFICIENT_7") +
                " REAL, " +
                getContractFieldValue("COLUMN_NAME_COEFFICIENT_8") +
                " REAL, " +
                getContractFieldValue("COLUMN_NAME_COEFFICIENT_9") +
                " REAL, " +
                getContractFieldValue("COLUMN_NAME_COEFFICIENT_10") +
                " REAL, " +
                getContractFieldValue("COLUMN_NAME_COEFFICIENT_11") +
                " REAL, " +
                getContractFieldValue("COLUMN_NAME_COEFFICIENT_12") +
                " REAL, FOREIGN KEY ( " +
                getContractFieldValue("COLUMN_NAME_RECORDING_ID") +
                " ) REFERENCES " +
                RecordingsDatabaseContract.Recordings.TABLE_NAME +
                " ( " +
                RecordingsDatabaseContract.Recordings.COLUMN_NAME_ID +
                " ) )";
            mfccsDatabase.execSQL(mfccsTableCreationQuery);
            Log.d(MFCC_FEATURES_DATABASE_HELPER_TAG, "Executed table creation query: " + mfccsTableCreationQuery);
            Log.d("DEBUG", "Executed table creation query: " + mfccsTableCreationQuery);
        }
    }

    @Override
    public boolean isAnalyzed( int recordingID ){
        SQLiteDatabase mfccsDatabase = this.getReadableDatabase();
        String findRecordingQuery = "SELECT * FROM " +
                                    getContractFieldValue( "TABLE_NAME" ) +
                                    " WHERE " +
                                    getContractFieldValue( "COLUMN_NAME_RECORDING_ID" ) +
                                    " = " +
                                    recordingID;
        Log.d( MFCC_FEATURES_DATABASE_HELPER_TAG, "Executing query: " + findRecordingQuery );
        Cursor findRecordingResultCursor = mfccsDatabase.rawQuery( findRecordingQuery, null );
        Log.d( MFCC_FEATURES_DATABASE_HELPER_TAG, "# result rows = " + findRecordingResultCursor.getCount() );

        return( ( findRecordingResultCursor.getCount() > 0 ) );
    }

    @Override
    public boolean insertFeatures( int recordingID, ArrayList< Pair< Integer, double[] > > featuresToStore ){
        SQLiteDatabase mfccsDatabase = this.getWritableDatabase();

        boolean allInsertsSuccessful = true;
        try{
            mfccsDatabase.beginTransaction();

            for( Pair< Integer, double[] > currentFeatureToInsert : featuresToStore ){
                long insertResult = insertFeature( mfccsDatabase, recordingID, currentFeatureToInsert.first, currentFeatureToInsert.second );

                if( insertResult == -1 ){
                    allInsertsSuccessful = false;
                    break;
                }
            }
        } catch( Exception e ){
        } finally{
            if( allInsertsSuccessful == true ){
                mfccsDatabase.setTransactionSuccessful();
            }
            mfccsDatabase.endTransaction();

            return( allInsertsSuccessful );
        }
    }
}
