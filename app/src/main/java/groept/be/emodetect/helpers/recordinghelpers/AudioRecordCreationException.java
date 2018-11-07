package groept.be.emodetect.helpers.recordinghelpers;

import java.util.HashMap;

public class AudioRecordCreationException extends Exception {
    final public static HashMap< Integer, String > errorCodeToMessageMap;

    final public static int NOT_INITIALIZED = 1;

    static {
        errorCodeToMessageMap = new HashMap< Integer, String >();
        errorCodeToMessageMap.put( NOT_INITIALIZED, "New AudioRecord instance was found not to be initialized" );
    }

    public final int audioRecordCreationFailureCode;

    public AudioRecordCreationException( int audioRecordCreationFailureCode ){
        this( audioRecordCreationFailureCode, null );
    }

    public AudioRecordCreationException( int audioRecordCreationFailureCode, Exception cause ){
        super( errorCodeToMessageMap.get( audioRecordCreationFailureCode ), cause );

        this.audioRecordCreationFailureCode = audioRecordCreationFailureCode;
    }
}
