package groept.be.emodetect.helpers.recordinghelpers;

import groept.be.emodetect.helpers.miscellaneous.ExceptionHandler;
import android.util.Log;

public class Stereo16BitPCMAudioFilePlayerExceptionHandler implements ExceptionHandler {
    public static final String STEREO_16_BIT_PCM_AUDIO_FILE_PLAYER_EXCEPTION_HANDLER =
        "Ster16PCMAudFlPlayerEx";

    @Override
    public void handleException( Exception exception ) {
        Log.d( STEREO_16_BIT_PCM_AUDIO_FILE_PLAYER_EXCEPTION_HANDLER, "An exception occurred in the player thread!\n" + exception.getMessage() );
    }
}
