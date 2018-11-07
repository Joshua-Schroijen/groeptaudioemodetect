package groept.be.emodetect.helpers.recordinghelpers;

import android.os.Process;
import android.util.Log;

import java.util.concurrent.BlockingQueue;

public class StereoPCMAudioRecordProducer extends AudioRecordProducer{
    private final static String STEREO_PCM_AUDIO_RECORD_PRODUCER_TAG = "SterPCMAudioRecProducer";

    public StereoPCMAudioRecordProducer( BlockingQueue< byte[] > passingQueue ) throws AudioRecordCreationException {
        super( passingQueue, new LegacyStereoPCMAudioRecordFactory() );

        audioBuffer = new byte[ ( audioRecordFactory.getUsedAudioRecordBufferByteSize() ) ];
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority( Process.THREAD_PRIORITY_URGENT_AUDIO );

        try {
            audioRecord.startRecording();

            while ( ! Thread.interrupted() ) {
                audioRecord.read( audioBuffer, 0, audioBuffer.length );
                passingQueue.put( audioBuffer );
            }

            throw( new InterruptedException() );
        } catch( InterruptedException e ) {
            Log.v( STEREO_PCM_AUDIO_RECORD_PRODUCER_TAG, "Interrupted. Message: " + e.getMessage() );
            audioRecord.stop();
            audioRecord.release();
        }
    }
}
