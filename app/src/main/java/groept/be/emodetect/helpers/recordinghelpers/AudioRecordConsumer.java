package groept.be.emodetect.helpers.recordinghelpers;

import android.util.Log;

import groept.be.emodetect.helpers.fileformathelpers.WAVFile;
import groept.be.emodetect.helpers.miscellaneous.ArrayTools;

import java.io.RandomAccessFile;
import java.io.IOException;

import java.nio.ByteOrder;
import java.util.concurrent.BlockingQueue;

public class AudioRecordConsumer implements Runnable{
    private final static String AUDIO_RECORD_CONSUMER_TAG = "AudioRecordConsumer";

    protected BlockingQueue< byte[] > passingQueue;
    protected RandomAccessFile outputFileRandomAccessReference;

    public AudioRecordConsumer( BlockingQueue< byte[] > passingQueue, RandomAccessFile outputFileRandomAccessReference ){
        this.passingQueue = passingQueue;
        this.outputFileRandomAccessReference = outputFileRandomAccessReference;
    }

    @Override
    public void run() {
        WAVFile outputWAVFile = new WAVFile( ( short )( 2 ),
                                             44100,
                                             ( short )( 16 ) );
        try{
            outputWAVFile.writeHeadersToFile( outputFileRandomAccessReference );
            while( ! Thread.interrupted() ){
                byte[] currentSegment = passingQueue.take();
                outputWAVFile.appendData( ArrayTools.byteArrayToShortArrayList( currentSegment, ByteOrder.LITTLE_ENDIAN ), outputFileRandomAccessReference );
            }
            throw new InterruptedException();
        } catch( InterruptedException e ){
            Log.v( AUDIO_RECORD_CONSUMER_TAG,"Interrupted. Message: " + e.getMessage() );
        } catch( IOException e ){
            Log.v( AUDIO_RECORD_CONSUMER_TAG, "Error appending consumed data to file. Message: " + e.getMessage() );
        }
    }
}