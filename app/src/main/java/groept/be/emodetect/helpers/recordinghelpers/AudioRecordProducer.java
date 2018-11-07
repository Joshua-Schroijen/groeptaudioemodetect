package groept.be.emodetect.helpers.recordinghelpers;

import android.media.AudioRecord;

import java.util.concurrent.BlockingQueue;

public abstract class AudioRecordProducer implements Runnable{
    public static final String AUDIO_RECORD_PRODUCER_TAG = "AudioRecordProducer";

    protected AudioRecord audioRecord;
    protected AudioRecordFactory audioRecordFactory;
    protected byte[] audioBuffer;

    protected BlockingQueue< byte[] > passingQueue;

    public AudioRecordProducer( BlockingQueue< byte[] > passingQueue, AudioRecordFactory audioRecordFactory ) throws AudioRecordCreationException {
        this.passingQueue = passingQueue;

        this.audioRecordFactory = audioRecordFactory;

        if( audioRecordFactory != null ) {
            audioRecord = audioRecordFactory.buildAudioRecord();
        }
    }

    @Override
    public abstract void run();
}
