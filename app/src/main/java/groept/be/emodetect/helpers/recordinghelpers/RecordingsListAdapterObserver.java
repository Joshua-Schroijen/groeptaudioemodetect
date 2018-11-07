package groept.be.emodetect.helpers.recordinghelpers;

import java.util.ArrayList;

import android.util.Pair;

public interface RecordingsListAdapterObserver {
    public void update( ArrayList< Pair< String, Stereo16BitPCMAudioFilePlayer > > newRecordingsBaseObjects );
}
