package groept.be.emodetect.uihelpers.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import groept.be.emodetect.R;
import groept.be.emodetect.uihelpers.LabeledSlider;

public class RecordingLabelingDialog implements View.OnClickListener {
    public static final String RECORDING_NAME_DIALOG_TAG = "LabelingDialog";

    public static class RecordingLabelingDialogResult {
        private PromptsDialogActionResult dialogWayOut;
        private double valence;
        private double arousal;

        public RecordingLabelingDialogResult(PromptsDialogActionResult dialogWayOut,
                                             double valence,
                                             double arousal ){
            this.dialogWayOut = dialogWayOut;
            this.valence = valence;
            this.arousal = arousal;
        }

        public PromptsDialogActionResult getDialogWayOut(){
            return( dialogWayOut );
        }

        public double getValence(){
            return( this.valence );
        }

        public double getArousal() { return( this.arousal ); }
    }

    private Context context;

    private LabeledSlider valenceSlider;
    private LabeledSlider arousalSlider;

    private AlertDialog dialog;
    private RecordingLabelingDialogDismissHandler dismissHandler;
    private boolean resultReady;

    private PromptsDialogActionResult dialogWayOut;
    private double valence;
    private double arousal;

    public RecordingLabelingDialog(Context context ){
        this.context = context;

        resultReady = false;
        valence = Double.NaN;
        arousal = Double.NaN;
        dialogWayOut = null;

        dismissHandler = null;
    }

    public void setDismissHandler( RecordingLabelingDialogDismissHandler dismissHandler ){
        this.dismissHandler = dismissHandler;
    }

    public void showDialog(){
        LayoutInflater inflater = ( ( LayoutInflater ) context.getSystemService( context.LAYOUT_INFLATER_SERVICE ) );

        View popupLayout = inflater.inflate( R.layout.feature_labeling_pop_up, null );

        this.valenceSlider = popupLayout.findViewById( R.id.feature_labeling_valence_slider_input );
        this.arousalSlider = popupLayout.findViewById( R.id.feature_labeling_arousal_slider_input );

        popupLayout.findViewById( R.id.feature_labeling_submit ).setOnClickListener( this );
        popupLayout.findViewById( R.id.feature_labeling_cancel ).setOnClickListener( this );

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder( context );
        dialogBuilder.setView( popupLayout );
        dialog = dialogBuilder.create();
        dialog.show();
    }

    @Override
    public void onClick( View v ){
        dialog.dismiss();

        if( v.getId() == R.id.feature_labeling_submit ){
            this.dialogWayOut = PromptsDialogActionResult.RESULT_SUBMIT;
            this.valence = this.valenceSlider.getValue();
            this.arousal = this.arousalSlider.getValue();

            Log.d(RECORDING_NAME_DIALOG_TAG, "Got valence: " + valence + ", got arousal: " + arousal );

            if( dismissHandler != null ){
                dismissHandler.onDismiss( new RecordingLabelingDialogResult( dialogWayOut, this.valence, this.arousal ) );
            }

            this.resultReady = true;
        }
        if( v.getId() == R.id.feature_labeling_cancel ){
            this.dialogWayOut = PromptsDialogActionResult.RESULT_CANCEL;
            this.valence = Double.NaN;
            this.arousal = Double.NaN;
            if( dismissHandler != null ){
                dismissHandler.onDismiss( new RecordingLabelingDialogResult( dialogWayOut, this.valence, this.arousal ) );
            }

            resultReady = true;
        }
    }

    public boolean isResultReady(){
        return( this.resultReady );
    }

    public RecordingLabelingDialogResult getResult(){
        return( new RecordingLabelingDialogResult( dialogWayOut, this.valence, this.arousal ) );
    }
}
