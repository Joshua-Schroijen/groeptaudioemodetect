package groept.be.emodetect.helpers.databasehelpers.featuresdatabases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.util.Pair;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.lang.NoSuchFieldException;
import java.lang.IllegalAccessException;

import groept.be.emodetect.helpers.databasehelpers.RecordingsDatabaseContract;

public abstract class FeaturesDatabaseHelper extends SQLiteOpenHelper {
    private static final String FEATURES_DATABASE_HELPER_TAG = "FeaturesDBHelper";

    protected abstract Class getContractClass();

    protected String getContractFieldValue( String fieldName ){
        String fieldValue = null;

        Class contract = getContractClass();

        try{
            Field desiredField = contract.getField( fieldName );
            fieldValue = ( ( String )( desiredField.get( this ) ) );
        } catch( NoSuchFieldException e ){
        } catch( IllegalAccessException e ){
        }

        return( fieldValue );
    }

    protected boolean checkIfTableExists(){
        SQLiteDatabase featuresDatabase = this.getReadableDatabase();
        String checkIfTableExistsQuery =
                "SELECT name FROM sqlite_master WHERE type = 'table' AND name = '" +
                        getContractFieldValue( "TABLE_NAME" ) +
                        "'";
        Cursor checkIfTableExistsResultCursor = featuresDatabase.rawQuery( checkIfTableExistsQuery, null );

        return( ( checkIfTableExistsResultCursor.getCount() > 0 ) );
    }

    public FeaturesDatabaseHelper( Context applicationContext, String featuresDatabaseFileName ){
        super( applicationContext, featuresDatabaseFileName, null, 1 );
    }

    @Override
    public void onCreate( SQLiteDatabase featuresDatabase ){
    }

    @Override
    public void onUpgrade( SQLiteDatabase featuresDatabase, int oldVersion, int newVersion ){
        /* We will never need to migrate data to newer database schemas here */
    }

    public ArrayList< String > getAnalyzableRecordings(){
        ArrayList<String> analyzableRecordings = new ArrayList< String >();;

        SQLiteDatabase database = this.getReadableDatabase();

        String getAnalyzableRecordingsQuery =
            "SELECT " +
            RecordingsDatabaseContract.Recordings.COLUMN_NAME_RECORDING_FILE_NAME +
            " FROM " +
            RecordingsDatabaseContract.Recordings.TABLE_NAME +
            " WHERE " +
            RecordingsDatabaseContract.Recordings.COLUMN_NAME_ID +
            " NOT IN ( SELECT " +
            getContractFieldValue("COLUMN_NAME_RECORDING_ID") +
            " FROM " +
            getContractFieldValue("TABLE_NAME") +
            " )";
        Cursor analyzableRecordingsCursor = database.rawQuery(getAnalyzableRecordingsQuery, null);

        if( analyzableRecordingsCursor.moveToFirst() ){
            do {
                analyzableRecordings.add( analyzableRecordingsCursor.getString(0) );
            } while( analyzableRecordingsCursor.moveToNext() );
        }

        return( analyzableRecordings );
    }

    public abstract boolean isAnalyzed( int recordingID );

    public abstract boolean insertFeatures( int recordingID, ArrayList< Pair< Integer, double[] > > featuresToStore );
}
