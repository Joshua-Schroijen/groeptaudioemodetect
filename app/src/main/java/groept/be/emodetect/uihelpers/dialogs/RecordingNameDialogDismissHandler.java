package groept.be.emodetect.uihelpers.dialogs;

public interface RecordingNameDialogDismissHandler {
    public void onDismiss( RecordingNameDialog.RecordingNameDialogResult result );

    public void registerObserver( NewRecordingNameObserver newObserver );
    public void unregisterObserver( NewRecordingNameObserver observerToRemove );
    public void notifyObservers( String newName );
}
