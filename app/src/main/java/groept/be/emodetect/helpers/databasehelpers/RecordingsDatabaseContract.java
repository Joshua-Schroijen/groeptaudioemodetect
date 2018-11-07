package groept.be.emodetect.helpers.databasehelpers;

import android.provider.BaseColumns;

public class RecordingsDatabaseContract {
    private RecordingsDatabaseContract(){}

    public static class Recordings implements BaseColumns {
        public static final String TABLE_NAME = "Recordings";
        public static final String COLUMN_NAME_ID = "ID";
        public static final String COLUMN_NAME_RECORDING_FILE_NAME = "RecordingFileName";
    }
}
