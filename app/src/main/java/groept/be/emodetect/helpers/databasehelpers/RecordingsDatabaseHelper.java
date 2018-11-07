package groept.be.emodetect.helpers.databasehelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

public class RecordingsDatabaseHelper extends SQLiteOpenHelper {
    private static final String RECORDINGS_DATABASE_HELPER_TAG = "RecordingsDBHelper";

    private boolean checkIfTableExists(){
        SQLiteDatabase recordingsDatabase = this.getReadableDatabase();
        String checkIfTableExistsQuery =
            "SELECT name FROM sqlite_master WHERE type = 'table' AND name = '" +
            RecordingsDatabaseContract.Recordings.TABLE_NAME +
            "'";
        Cursor checkIfTableExistsResultCursor = recordingsDatabase.rawQuery( checkIfTableExistsQuery, null );

        return( ( checkIfTableExistsResultCursor.getCount() > 0 ) );
    }

    public RecordingsDatabaseHelper( Context applicationContext, String recordingsDatabaseFileName ){
        super( applicationContext, recordingsDatabaseFileName, null, 1 );
    }

    @Override
    public void onCreate( SQLiteDatabase recordingsDatabase ) {
    }

    public void createTableIfNeeded(){
        if( !checkIfTableExists() ) {
            /* Here we generate and execute the SQL query that creates our table
             * for keeping track of kept recordings
             */
            SQLiteDatabase recordingsDatabase = this.getWritableDatabase();
            String recordingsTableCreationQuery = "CREATE TABLE " +
                RecordingsDatabaseContract.Recordings.TABLE_NAME +
                " ( " +
                RecordingsDatabaseContract.Recordings.COLUMN_NAME_ID +
                " TEXT PRIMARY KEY, " +
                RecordingsDatabaseContract.Recordings.COLUMN_NAME_RECORDING_FILE_NAME +
                " TEXT, CONSTRAINT recordingFileNameUnique UNIQUE ( " +
                RecordingsDatabaseContract.Recordings.COLUMN_NAME_RECORDING_FILE_NAME +
                " ) )";
            recordingsDatabase.execSQL( recordingsTableCreationQuery );
        }
    }

    @Override
    public void onUpgrade( SQLiteDatabase recordingsDatabase, int oldVersion, int newVersion ){
        /* We will never need to migrate data to newer database schemas here */
    }

    public ArrayList< String > getKeptRecordings(){
        SQLiteDatabase recordingsDatabase = this.getReadableDatabase();
        ArrayList< String > keptRecordings = new ArrayList<>();
        String getKeptRecordingsQuery = "SELECT * FROM " +
                                        RecordingsDatabaseContract.Recordings.TABLE_NAME;
        Cursor keptRecordingsCursor = recordingsDatabase.rawQuery( getKeptRecordingsQuery, null );

        if( keptRecordingsCursor.moveToFirst() ){
            do{
                keptRecordings.add( keptRecordingsCursor.getString( 1 ) );
            } while( keptRecordingsCursor.moveToNext() );
        }

        return( keptRecordings );
    }

    public int getRecordingID( String recordingFileName ){
        int recordingID = -1;

        SQLiteDatabase recordingsDatabase = this.getReadableDatabase();
        String getRecordingIDQuery =
            "SELECT ID FROM " +
            RecordingsDatabaseContract.Recordings.TABLE_NAME +
            " WHERE RecordingFileName = '" +
            recordingFileName +
            "';";
        Cursor recordingIDCursor = recordingsDatabase.rawQuery( getRecordingIDQuery, null );

        if( recordingIDCursor.moveToFirst() ){
            recordingID = Integer.parseInt( recordingIDCursor.getString( 0 ) );
            Log.d( RECORDINGS_DATABASE_HELPER_TAG, "Recording ID retrieved: " + recordingID );
        }

        return( recordingID );
    }

    public boolean isRecorded( String recordingFileName ){
        SQLiteDatabase recordingsDatabase = this.getReadableDatabase();
        String findRecordingQuery = "SELECT * FROM " +
                                    RecordingsDatabaseContract.Recordings.TABLE_NAME +
                                    " WHERE " +
                                    RecordingsDatabaseContract.Recordings.COLUMN_NAME_RECORDING_FILE_NAME +
                                    " = '" +
                                    recordingFileName +
                                    "'";
        Log.d( RECORDINGS_DATABASE_HELPER_TAG, "Executing query: " + findRecordingQuery );
        Cursor findRecordingResultCursor = recordingsDatabase.rawQuery( findRecordingQuery, null );
        Log.d( RECORDINGS_DATABASE_HELPER_TAG, "# result rows = " + findRecordingResultCursor.getCount() );

        return( ( findRecordingResultCursor.getCount() > 0 ) );
    }

    public void insertRecording( String newRecordingFileName ){
        SQLiteDatabase recordingsDatabase = this.getWritableDatabase();

        ContentValues contentValue = new ContentValues();
        contentValue.put( RecordingsDatabaseContract.Recordings.COLUMN_NAME_ID, UUID.randomUUID().toString() );
        contentValue.put( RecordingsDatabaseContract.Recordings.COLUMN_NAME_RECORDING_FILE_NAME, newRecordingFileName );

        Log.d( RECORDINGS_DATABASE_HELPER_TAG, "Checking for existence" );
        if( isRecorded( newRecordingFileName ) ){
            Log.d( RECORDINGS_DATABASE_HELPER_TAG, newRecordingFileName + " was already recorded!" );
        }
        if( !isRecorded( newRecordingFileName ) ){
            Log.d( RECORDINGS_DATABASE_HELPER_TAG, newRecordingFileName + " was NOT already recorded!" );
            recordingsDatabase.insert(RecordingsDatabaseContract.Recordings.TABLE_NAME, null, contentValue);
        }
    }

    public void deleteRecording( String recordingToDeleteFileName ){
        SQLiteDatabase recordingsDatabase = this.getWritableDatabase();

        String deletionQuery = "DELETE FROM " +
                               RecordingsDatabaseContract.Recordings.TABLE_NAME +
                               " WHERE " +
                               RecordingsDatabaseContract.Recordings.COLUMN_NAME_RECORDING_FILE_NAME +
                               " = '" +
                               recordingToDeleteFileName +
                               "'";

        if( isRecorded( recordingToDeleteFileName ) ){
            recordingsDatabase.execSQL( deletionQuery );
        }
    }
}
