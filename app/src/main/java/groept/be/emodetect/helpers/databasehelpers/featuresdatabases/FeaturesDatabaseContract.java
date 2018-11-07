package groept.be.emodetect.helpers.databasehelpers.featuresdatabases;

import android.provider.BaseColumns;

public class FeaturesDatabaseContract implements BaseColumns {
    protected FeaturesDatabaseContract(){}

    public static final String COLUMN_NAME_ID = "ID";
    public static final String COLUMN_NAME_RECORDING_ID = "RecordingID";
    public static final String COLUMN_NAME_FRAME_OFFSET = "FrameOffset";
}
