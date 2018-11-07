package groept.be.emodetect.helpers.analysishelpers;

import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.mfcc.MFCC;

import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;

// MfccExtractor( 44100, 1024, 128, 40, 50, 300, 3000 );
public class MfccExtractor implements FeatureExtractor {
    final int sampleRate;
    final int bufferSize;
    final int bufferOverlap;
    final int noDctCoefficients;
    final int noMelFilters;
    final float lowerFrequency;
    final float upperFrequency;

    final int bufferStep;

    public MfccExtractor(
        int sampleRate,
        int bufferSize,
        int bufferOverlap,
        int noDctCoefficients,
        int noMelFilters,
        float lowerFrequency,
        float upperFrequency ){
        this.sampleRate = sampleRate;
        this.bufferSize = bufferSize;
        this.bufferOverlap = bufferOverlap;
        this.noDctCoefficients = noDctCoefficients;
        this.noMelFilters = noMelFilters;
        this.lowerFrequency = lowerFrequency;
        this.upperFrequency = upperFrequency;

        this.bufferStep = ( bufferSize - bufferOverlap );
    }

    @Override
    public ArrayList< Pair< Integer, float[] > > extractFeatures( String recordingFilename ) throws IOException {
        final ArrayList< Pair< Integer, float[] > > mfccResults = new ArrayList< Pair< Integer, float[] > >();

        File recordingFile = new File( recordingFilename );
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromPipe( recordingFilename, sampleRate, bufferSize, bufferOverlap );

        final MFCC mfcc =
            new MFCC(
                this.bufferSize,        // Samples per frame
                this.sampleRate,        // Sample rate
                this.noDctCoefficients, // Amount of DCT coefficient
                this.noMelFilters,      // Amount of mel filters
                this.lowerFrequency,    // Amount of lower frequency
                this.upperFrequency     // Amount of upper frequency
            );

        dispatcher.addAudioProcessor( mfcc );
        dispatcher.addAudioProcessor( new AudioProcessor(){
            int currentFrameStartingSample = 0;

            @Override
            public void processingFinished() {
            }

            @Override
            public boolean process( AudioEvent audioEvent ){
                mfccResults.add( new Pair< Integer, float[] >( currentFrameStartingSample, mfcc.getMFCC() ) );
                currentFrameStartingSample += bufferStep;
                return( true );
            }
          }
        );

        dispatcher.run();

        return( mfccResults );
    }
}
