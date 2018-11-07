package groept.be.emodetect.helpers.recordinghelpers;

import java.util.HashMap;

public class AudioTrackCreationException extends Exception {
    final public static HashMap< Integer, String > errorCodeToMessageMap;

    final public static int NOT_INITIALIZED = 1;
    final public static int SOURCE_INACCESSIBLE = 2;
    final public static int UNEXPECTED_SOURCE_FORMAT = 3;

    static {
        errorCodeToMessageMap = new HashMap< Integer, String >();
        errorCodeToMessageMap.put( NOT_INITIALIZED, "New AudioTrack instance was found not to be initialized" );
        errorCodeToMessageMap.put( SOURCE_INACCESSIBLE, "Audio source inaccessible" );
        errorCodeToMessageMap.put( UNEXPECTED_SOURCE_FORMAT, "Audio source does not have data in the expected format" );
    }

    public final int audioTrackCreationFailureCode;

    public AudioTrackCreationException( int audioTrackCreationFailureCode ){
        this( audioTrackCreationFailureCode, null );
    }

    public AudioTrackCreationException( int audioTrackCreationFailureCode, Exception cause ){
        super( errorCodeToMessageMap.get( audioTrackCreationFailureCode ), cause );

        this.audioTrackCreationFailureCode = audioTrackCreationFailureCode;
    }
}
