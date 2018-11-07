package groept.be.emodetect.uihelpers.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class RecordingNameDialog implements View.OnClickListener {
    public static final String RECORDING_NAME_DIALOG_TAG = "RecordingNameDialog";

    public static class RecordingNameDialogResult {
        private PromptsDialogActionResult dialogWayOut;
        private String recordingName;

        public RecordingNameDialogResult ( PromptsDialogActionResult dialogWayOut,
                                           String recordingName){
            this.dialogWayOut = dialogWayOut;
            this.recordingName = recordingName;
        }

        public PromptsDialogActionResult getDialogWayOut(){
            return( dialogWayOut );
        }

        public String getRecordingName(){
            return( recordingName );
        }
    }

    private Context context;
    private int popupLayoutResource;
    private int promptViewID;
    private int submitButtonViewID;
    private int cancelButtonViewID;
    private EditText prompt;

    private AlertDialog dialog;
    private RecordingNameDialogDismissHandler dismissHandler;
    private boolean resultReady;

    private PromptsDialogActionResult dialogWayOut;
    private String recordingName;

    public RecordingNameDialog( Context context,
                                int popupLayoutResource,
                                int promptViewID,
                                int submitButtonViewID,
                                int cancelButtonViewID ){
        this.context = context;
        this.popupLayoutResource = popupLayoutResource;
        this.promptViewID = promptViewID;
        this.submitButtonViewID = submitButtonViewID;
        this.cancelButtonViewID = cancelButtonViewID;

        resultReady = false;
        recordingName = null;
        dialogWayOut = null;

        dismissHandler = null;
    }

    public void setDismissHandler( RecordingNameDialogDismissHandler dismissHandler ){
        this.dismissHandler = dismissHandler;
    }

    public void showDialog(){
        LayoutInflater inflater = ( ( LayoutInflater ) context.getSystemService( context.LAYOUT_INFLATER_SERVICE ) );
        View popupLayout = inflater.inflate( popupLayoutResource, null );

        prompt = popupLayout.findViewById( promptViewID );

        popupLayout.findViewById( submitButtonViewID ).setOnClickListener( this );
        popupLayout.findViewById( cancelButtonViewID ).setOnClickListener( this );

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder( context );
        dialogBuilder.setView( popupLayout );
        dialog = dialogBuilder.create();
        dialog.show();
    }

    @Override
    public void onClick( View v ){
        dialog.dismiss();

        if( v.getId() == submitButtonViewID ){
            dialogWayOut = PromptsDialogActionResult.RESULT_SUBMIT;
            recordingName = prompt.getText().toString();
            if( dismissHandler != null ){
                dismissHandler.onDismiss( new RecordingNameDialogResult( dialogWayOut, recordingName ) );
            }

            resultReady = true;
        }
        if( v.getId() == cancelButtonViewID ){
            dialogWayOut = PromptsDialogActionResult.RESULT_CANCEL;
            recordingName = null;
            if( dismissHandler != null ){
                dismissHandler.onDismiss( new RecordingNameDialogResult( dialogWayOut, recordingName ) );
            }

            resultReady = true;
        }
    }

    public boolean isResultReady(){
        return( resultReady );
    }

    public RecordingNameDialogResult getResult(){
        return( new RecordingNameDialogResult( dialogWayOut, recordingName ) );
    }
}
