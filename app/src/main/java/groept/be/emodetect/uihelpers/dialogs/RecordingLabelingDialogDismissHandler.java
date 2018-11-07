package groept.be.emodetect.uihelpers.dialogs;

public interface RecordingLabelingDialogDismissHandler {
    public void onDismiss( RecordingLabelingDialog.RecordingLabelingDialogResult result );

    public void registerObserver( NewRecordingLabelObserver newObserver );
    public void unregisterObserver( NewRecordingLabelObserver observerToRemove );
    public void notifyObservers( double valence, double arousal );
}
