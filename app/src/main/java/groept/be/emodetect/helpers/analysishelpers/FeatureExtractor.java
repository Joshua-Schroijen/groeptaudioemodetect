package groept.be.emodetect.helpers.analysishelpers;

import android.support.v4.util.Pair;

import java.io.IOException;
import java.util.ArrayList;

public interface FeatureExtractor {
    public ArrayList< Pair< Integer, float[] > > extractFeatures( String recordingFilename ) throws IOException;
}
