package groept.be.emodetect.helpers.recordinghelpers;

import android.media.AudioTrack;

/***********************************************************************************
 * This interface is a blueprint for helper factory classes who will automatically *
 * produce AudioTrack instances playing audio in a certain encoding                *
 *                                                                                 *
 * Our intent is that one will write an AudioTrackFactory that implements this     *
 * interface.                                                                      *
 *                                                                                 *
 * @author Joshua Schroijen <Joshua.Schroijen@student.kuleuven.be>                 *
 * @version 1.0                                                                    *
 **********************************************************************************/

public interface AudioTrackFactory {
    /* These methods allow users of a factory to access
     * parameters of the AudioTrack more easily
     */

    /* This method returns the sample rate in Hz configured for
     * the AudioTrack object constructed ( should be sample rate of
     * AudioTrack's source )
     */
    public int getUsedSampleRate();

    /* This method returns the size of the internal AudioTrack buffer a user might
     * want to write to or read out in plain bytes ( depends on sample rate, bit depth
     * and channel count of AudioTrack source )
     */
    public int getUsedAudioRecordBufferByteSize();

    /* This method should be invoked on an implementing factory to construct an AudioTrack instance.
     *
     * @return A valid AudioTrack instance to which one will be able to write samples
     */
    public AudioTrack buildAudioTrack( ) throws AudioTrackCreationException;
}