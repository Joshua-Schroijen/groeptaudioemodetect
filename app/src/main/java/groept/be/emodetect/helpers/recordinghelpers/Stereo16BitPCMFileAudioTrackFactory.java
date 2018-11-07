package groept.be.emodetect.helpers.recordinghelpers;

import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import groept.be.emodetect.helpers.fileformathelpers.WAVFile;

/********************************************************************************
 * This helper factory class is an implementation of the AudioTrackFactory      *
 * interface that will automatically produce AudioTrack instances that will     *
 * play 16-bit stereo PCM audio from a file given in the constructor            *
 *                                                                              *
 * @author Joshua Schroijen <Joshua.Schroijen@student.kuleuven.be>              *
 * @version 1.0                                                                 *
 *******************************************************************************/
public class Stereo16BitPCMFileAudioTrackFactory implements AudioTrackFactory {
    private int sampleRate;
    private int newAudioTrackBufferByteSize;

    private String filename;

    public Stereo16BitPCMFileAudioTrackFactory( String filename ) throws AudioTrackCreationException {
        this.filename = filename;

        try{
            WAVFile.Information audioProperties = WAVFile.readWAVFileInformation( filename );

            if( audioProperties.getNoChannel() != 2 ){
                throw new AudioTrackCreationException( AudioTrackCreationException.UNEXPECTED_SOURCE_FORMAT );
            } else {
                this.sampleRate = audioProperties.getSampleRate();

                this.newAudioTrackBufferByteSize = AudioTrack.getMinBufferSize(
                    this.sampleRate,
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT );
                if( ( this.newAudioTrackBufferByteSize == AudioTrack.ERROR ) ||
                    ( this.newAudioTrackBufferByteSize == AudioTrack.ERROR_BAD_VALUE ) ){
                    this.newAudioTrackBufferByteSize = ( this.sampleRate * 2 ); // 2 * 2 = channels * bytes / sample
                }
            }
        } catch( IOException e ){
            throw new AudioTrackCreationException( AudioTrackCreationException.SOURCE_INACCESSIBLE, e );
        }
    }

    public String getFilename(){
        return( filename );
    }

    @Override
    public int getUsedSampleRate(){
        return( sampleRate );
    }

    @Override
    public int getUsedAudioRecordBufferByteSize(){
        return( newAudioTrackBufferByteSize );
    }

    @Override
    /* This method will return an AudioTrack instance that will play
     * stereo PCM samples from the file given in the constructor
     *
     * It is an implementation of a method of the AudioTrackFactory interface.
     * See the AudioTrackFactory interface documentation for more information.
     */
    public AudioTrack buildAudioTrack() throws AudioTrackCreationException {
        /* We already determined enough information in the constructor to
         * actually construct an AudioTrack for the given audio source file
         */
        AudioTrack newAudioTrack = new AudioTrack(
            AudioManager.STREAM_MUSIC,
            sampleRate,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_16BIT,
            newAudioTrackBufferByteSize,
            AudioTrack.MODE_STREAM );

        /* Before enthusiastically returning the AudioTrack instance, we should
         * inspect if it's actually ready to play anything.
         */
        if( ( newAudioTrack == null ) ||
            ( newAudioTrack.getState() != AudioTrack.STATE_INITIALIZED ) ){
            throw new AudioTrackCreationException( AudioTrackCreationException.NOT_INITIALIZED );
        } else {
            return( newAudioTrack );
        }
    }
}
