package groept.be.emodetect.helpers.recordinghelpers;

import android.media.AudioRecord;
import android.media.AudioFormat;
import android.media.MediaRecorder;

/********************************************************************************
 * This helper factory class is an implementation of the AudioRecordFactory     *
 * interface that will automatically produce AudioRecord instances optimally    *
 * configured for the device on which the using app is running. These instances *
 * will record stereo audio from the microphone in PCM format. This class is    *
 * designed for compatibility with older phones (API level > 15).               *
 *                                                                              *
 * @author Joshua Schroijen <Joshua.Schroijen@student.kuleuven.be>              *
 * @version 1.0                                                                 *
 *******************************************************************************/

public class LegacyStereoPCMAudioRecordFactory implements AudioRecordFactory {
    private static int sampleRate;
    private static int newAudioRecordBufferSize;

    static {
        /* First of all, we have to determine an appropriate sample rate and
         * audio buffer size.
         */
        int sampleRate = AudioRecordFactoryHelper.getHighestSupportedSampleRate();
        newAudioRecordBufferSize = AudioRecord.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT );
        if( ( newAudioRecordBufferSize == AudioRecord.ERROR ) ||
            ( newAudioRecordBufferSize == AudioRecord.ERROR_BAD_VALUE ) ){
            /* We know that four times the sample rate is a good minimum buffer size in
             * any case when recording 16-bit stereo PCM samples. So if getMinBufferSize
             * doesn't work for any reason, we can set this as a default.
             */
            newAudioRecordBufferSize = 2 * 2 * sampleRate;
        }

        /* Now we quadruple the minimum buffer size to get the actual buffer size we will use.
         * This will remove some unwanted artifacts such as a "beat" from the recording
         */
        newAudioRecordBufferSize *= 2;
    }

    @Override
    public int getUsedSampleRate(){
        return( sampleRate );
    }

    @Override
    public int getUsedAudioRecordBufferByteSize(){
        return( newAudioRecordBufferSize );
    }

    @Override
    /* This method will return an AudioRecord instance that will record
     * stereo PCM samples from the microphone.
     *
     * It is an implementation of a method of the AudioRecordFactory interface.
     * See the AudioRecordFactory interface documentation for more information.
     */
    public AudioRecord buildAudioRecord() throws AudioRecordCreationException {
        /* Now we have determined enough information to actually construct an AudioRecord
         * during class initialization
         */
        AudioRecord newAudioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                newAudioRecordBufferSize
        );

        /* Before enthusiastically returning the AudioRecord instance, we should
         * inspect if it's actually ready to record anything.
         */
        if( ( newAudioRecord == null ) ||
            ( newAudioRecord.getState() != AudioRecord.STATE_INITIALIZED ) ){
            throw( new AudioRecordCreationException( AudioRecordCreationException.NOT_INITIALIZED ) );
        } else {
            return( newAudioRecord );
        }
    }
}
