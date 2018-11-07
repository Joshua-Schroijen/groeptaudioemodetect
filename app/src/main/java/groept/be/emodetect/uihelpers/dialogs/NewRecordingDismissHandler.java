package groept.be.emodetect.uihelpers.dialogs;

import java.util.HashSet;

import android.content.Context;

public class NewRecordingDismissHandler implements RecordingNameDialogDismissHandler {
    private Context applicationContext;

    private HashSet<NewRecordingNameObserver> observers;

    public NewRecordingDismissHandler( Context applicationContext ){
        this.applicationContext = applicationContext;

        this.observers = new HashSet< NewRecordingNameObserver >();
    }

    @Override
    public void onDismiss( RecordingNameDialog.RecordingNameDialogResult result ) {
        String newRecordingName = null;

        if( result.getDialogWayOut() == PromptsDialogActionResult.RESULT_CANCEL ){
            newRecordingName = null;
        } else if( result.getDialogWayOut() == PromptsDialogActionResult.RESULT_SUBMIT ){
            newRecordingName = result.getRecordingName();
        }

        notifyObservers( newRecordingName );
    }

    @Override
    public void registerObserver( NewRecordingNameObserver newObserver ){
        observers.add( newObserver );
    }

    @Override
    public void unregisterObserver( NewRecordingNameObserver observerToRemove ){
        observers.remove( observerToRemove );
    }

    @Override
    public void notifyObservers( String newName ){
        for( NewRecordingNameObserver currentObserver : observers ){
            currentObserver.updateName( newName );
        }
    }
}
