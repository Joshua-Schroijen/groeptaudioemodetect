package groept.be.emodetect;


import android.content.Context;
import android.os.Environment;
import android.util.Log;

import be.tarsos.dsp.io.android.AndroidFFMPEGLocator;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.AudioDispatcher;

public class TarsosTester{
    public static void tryIt(Context context){
        Log.d(
             "TARSOSTEST",
             Environment.getExternalStorageDirectory().getAbsolutePath()
        );
        Log.d( "TARSOSTEST", "Doing Tarsos test!" );
        new AndroidFFMPEGLocator( context );
        AudioDispatcher myDispatcher =
            AudioDispatcherFactory.fromPipe(
              Environment.getExternalStorageDirectory().getAbsolutePath() + "/GroepT/SpeechEmotionDetection/SineWave500.wav",
              44100,
             1024,
             128
            );
    }
}