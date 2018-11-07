package groept.be.emodetect.helpers.recordinghelpers;

import android.media.AudioFormat;
import android.media.AudioRecord;

/********************************************************************************
 * The goal of this abstract helper class is to provide static methods that can *
 * be used by AudioRecordFactories to perform such general tasks as checking    *
 * device capabilities and validating inputs                                    *
 *                                                                              *
 * @author Joshua Schroijen <Joshua.Schroijen@student.kuleuven.be>              *
 * @version 1.0                                                                 *
 *******************************************************************************/
public abstract class AudioRecordFactoryHelper {
    /* This static array contains potential/common sample rates that we could use
     * Please respect the descending order - our efficient sample rate selection
     * algorithm depends on it
     */
    public final static int[] commonSampleRates = { 44100, 22050, 16000, 11025, 8000 };

    /* This static array contains a list of integer constants referring to the supported
     * audio encodings. Anywhere in the program, only these constants specify a valid
     * audio format.
     */
    public final static int[] availableEncodings = {
            AudioFormat.ENCODING_AC3,
            AudioFormat.ENCODING_DEFAULT,
            AudioFormat.ENCODING_DOLBY_TRUEHD,
            AudioFormat.ENCODING_DTS,
            AudioFormat.ENCODING_E_AC3,
            AudioFormat.ENCODING_IEC61937,
            AudioFormat.ENCODING_PCM_8BIT,
            AudioFormat.ENCODING_PCM_16BIT
    };

    /* This method finds, by trial-and-error, the highest recording sample rate the device supports
     * and returns it as an integer. If no supported sample rate was found, -1 is returned.
     *
     * IF THIS METHOD BEHAVES UNEXPECTEDLY, PLEASE CHECK THAT THE commonSampleRates ARRAY IS
     * SPECIFIED IN DESCENDING ORDER!
     *
     * This method uses a not too dirty hack to be compatible with all phones having API level 15
     * as the highest supported API level
     *
     * Note: the sample rate of the created AudioRecord instance can be retrieved in the client
     * by using the getSampleRate() method of AudioRecord
     */
    final public static int getHighestSupportedSampleRate() {
        /* The algorithm for finding the highest supported sample rate is extremely simple.
         * If a sample rate is supported, calling AudioRecords getMinBufferSize() method will return
         * an integer greater than 0. The channel configuration and encoding we also have to specify
         * are in fact irrelevant here and the maintainer should not worry about their precise values.
         * Any values are fine.
         *
         * Please note that this method can be implemented in a better way if one assumes a high API
         * level, using the AudioManager class
         *
         * @return The highest supported common sample rate in Hz
         */
        for( int currentSampleRate : commonSampleRates ){
            if( ( AudioRecord.getMinBufferSize( currentSampleRate, AudioFormat.CHANNEL_CONFIGURATION_DEFAULT, AudioFormat.ENCODING_PCM_16BIT ) ) > 0 ) {
                return( currentSampleRate );
            }
        }

        /* No supported sample rate was found, hence we return -1
         */
        return( -1 );
    }

    /* This method checks whether a given integer corresponds to an AudioFormat.ENCODING_*
     * value supported on both older and newer phones ( API level > 15 ).
     *
     * If the integer does indeed make sense, true is returned. Otherwise, false is returned.
     *
     * This function can be written better when higher API levels are assumed, but this is
     * the best we can do to still support older phones ( API level > 15 )
     *
     * @param encodingChoice A constant ( such as the AudioFormat.ENCODING_* constants ) that
     *                       supposedly refers to an encoding format
     * @return true if the audio encoding is supported, false if not
     */
    final public static boolean refersToSupportedEncoding( int encodingChoice ) {
        /* This function works very simply, if the integer passed through encodingChoice
         * resides in the availableEncodings array, true will be returned. Otherwise, false
         * will be returned.
         *
         */
        for( int currentSupportedEncoding : availableEncodings ){
            if( encodingChoice == currentSupportedEncoding ){
                return( true );
            }
        }

        return( false );
    }
}
