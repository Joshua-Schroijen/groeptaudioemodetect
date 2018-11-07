package groept.be.emodetect.helpers.recordinghelpers;

import android.media.AudioRecord;

/***********************************************************************************
 * This interface is a blueprint for helper factory classes who will automatically *
 * produce AudioRecord instances recording in a certain encoding optimally         *
 * configured for the device on which the using app is running.                    *
 *                                                                                 *
 * Our intent is that one will write an AudioRecordFactory that implements this    *
 * interface and also uses the AudioRecordFactoryHelper abstract class that will   *
 * handle some general tasks that all AudioRecordFactories will probably need to   *
 * perform.                                                                        *
 *                                                                                 *
 * @author Joshua Schroijen <Joshua.Schroijen@student.kuleuven.be>                 *
 * @version 1.0                                                                    *
 **********************************************************************************/

public interface AudioRecordFactory {
    /* These methods allow users of a factory to access
     * parameters of the AudioRecord more easily
     */

    /* This method returns the sample rate in Hz of the AudioRecord object
     * constructed
     */
    public int getUsedSampleRate();

    /* This method returns the size of the internal AudioRecord buffer a user might
     * want to read out in plain bytes
     */
    public int getUsedAudioRecordBufferByteSize();

    /* This method should be invoked on an implementing factory to construct an AudioRecord instance.
     *
     * @return A valid AudioRecord instance from which one will be able to read samples
     */
    public AudioRecord buildAudioRecord( ) throws AudioRecordCreationException;
}