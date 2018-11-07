package groept.be.emodetect.uihelpers;

import groept.be.emodetect.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

/*********************************************************
 * This class provides a stateful UI element that        *
 * correctly provides the user the means to control      *
 * the recording creation capabilities of the app.       *
 *                                                       *
 * @author Joshua Schroijen <Joshua.Schroijen@gmail.com> *
 *********************************************************/
public class RecordingController extends LinearLayout implements View.OnClickListener {
    public static final String RECORDING_CONTROLLER_TAG = "RecordingController";

    public static enum State {
        HIDDEN,
        WILl_RECORD_NEW,
        WILL_START_RECORDING,
        WILL_STOP_RECORDING,
        WILL_HANDLE_NEW_RECORDING
    }

    private Context context;
    private AttributeSet attrs;
    private int defStyleAttr;
    private boolean startHidden;

    private LinearLayout.LayoutParams containerLayoutParams;

    private Button firstButton;
    private Button secondButton;
    private final int firstButtonID = 1;
    private final int secondButtonID = 2;
    private LinearLayout.LayoutParams buttonLayoutParameters;
    private View.OnClickListener onNewRecordingClickListener;
    private View.OnClickListener onStartRecordingClickListener;
    private View.OnClickListener onStopRecordingClickListener;
    private View.OnClickListener onKeepRecordingClickListener;
    private View.OnClickListener onDiscardRecordingClickListener;

    private State currentState;
    private State stateBeforeHidden;

    private void init( AttributeSet attrs, int defStyleAttr, boolean startHidden ){
        this.attrs = attrs;
        this.defStyleAttr = defStyleAttr;
        this.startHidden = startHidden;

        containerLayoutParams = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT );
        setLayoutParams( containerLayoutParams );
        setOrientation( LinearLayout.HORIZONTAL );

        firstButton = new Button( context );
        secondButton = new Button( context );
        firstButton.setId( firstButtonID );
        secondButton.setId( secondButtonID );
        buttonLayoutParameters = new LinearLayout.LayoutParams( 0, LayoutParams.WRAP_CONTENT, 1 );
        firstButton.setLayoutParams( buttonLayoutParameters );
        secondButton.setLayoutParams( buttonLayoutParameters );
        firstButton.setText( R.string.new_recording );
        secondButton.setText( R.string.discard_recording );
        float textSize = ( ( context.getResources().getDimension( R.dimen.recording_controller_buttons_text_size ) ) /
                           ( context.getResources().getDisplayMetrics().density ) );
        firstButton.setTextSize( textSize );
        secondButton.setTextSize( textSize );
        firstButton.setOnClickListener( this );
        secondButton.setOnClickListener( this );

        setDescendantFocusability( ViewGroup.FOCUS_BLOCK_DESCENDANTS );
        addView( firstButton );
        addView( secondButton );

        currentState = State.WILl_RECORD_NEW;
        if( startHidden == true ){
            hide();
        } else {
            show();
        }
    }

    private void goToState( State newState ){
        if( newState == State.HIDDEN ){
            if( currentState != State.HIDDEN ){
                stateBeforeHidden = currentState;
            }

            setVisibility( View.GONE );

            firstButton.setVisibility( View.GONE );
            secondButton.setVisibility( View.GONE );
        } else if( newState == State.WILL_HANDLE_NEW_RECORDING ){
            setVisibility( View.VISIBLE );

            firstButton.setText( R.string.keep_recording );
            secondButton.setText( R.string.discard_recording );
            firstButton.setVisibility( View.VISIBLE );
            secondButton.setVisibility( View.VISIBLE );
        } else {
            setVisibility( View.VISIBLE );

            switch( newState ){
                case WILl_RECORD_NEW:
                    firstButton.setText( R.string.new_recording );

                    break;

                case WILL_START_RECORDING:
                    firstButton.setText( R.string.start_recording );

                    break;

                case WILL_STOP_RECORDING:
                    firstButton.setText( R.string.stop_recording );

                    break;

                default:
                    // Impossible
                    break;
            }

            firstButton.setVisibility( View.VISIBLE );
            secondButton.setVisibility( View.GONE );
        }

        currentState = newState;
    }

    public RecordingController( Context context ){
        super( context );
        this.context = context;
        init( null, 0, true );
    }

    public RecordingController( Context context, boolean startHidden ){
        super( context );
        this.context = context;
        init( null, 0, startHidden );
    }

    public RecordingController( Context context, AttributeSet attrs ){
        super( context, attrs );
        this.context = context;
        init( attrs, 0, true );
    }

    public RecordingController( Context context, AttributeSet attrs, boolean startHidden ){
        super( context, attrs );
        this.context = context;
        init( attrs, 0, startHidden );
    }

    public RecordingController( Context context, AttributeSet attrs, int defStyleAttr ){
        super( context, attrs, defStyleAttr );
        this.context = context;
        init( attrs, defStyleAttr, true );
    }

    public RecordingController( Context context, AttributeSet attrs, int defStyleAttr, boolean startHidden ){
        super( context, attrs, defStyleAttr );
        this.context = context;
        init( attrs, defStyleAttr, startHidden );
    }

    public RecordingController copyWithNewContext( Context newContext ){
        RecordingController newRecordingController = new RecordingController( newContext, attrs, defStyleAttr, startHidden );

        newRecordingController.onNewRecordingClickListener = this.onNewRecordingClickListener;
        newRecordingController.onStartRecordingClickListener = this.onStartRecordingClickListener;
        newRecordingController.onStopRecordingClickListener = this.onStopRecordingClickListener;
        newRecordingController.onKeepRecordingClickListener = this.onKeepRecordingClickListener;
        newRecordingController.onDiscardRecordingClickListener = this.onDiscardRecordingClickListener;

        newRecordingController.currentState = this.currentState;
        newRecordingController.stateBeforeHidden = this.stateBeforeHidden;

        newRecordingController.goToState( currentState );

        return( newRecordingController );
    }

    public void setOnNewRecordingClickListener( View.OnClickListener onNewRecordingClickListener ){
        this.onNewRecordingClickListener = onNewRecordingClickListener;
    }

    public void setOnStartRecordingClickListener( View.OnClickListener onStartRecordingClickListener ){
        this.onStartRecordingClickListener = onStartRecordingClickListener;
    }

    public void setOnStopRecordingClickListener( View.OnClickListener onStopRecordingClickListener ){
        this.onStopRecordingClickListener = onStopRecordingClickListener;
    }

    public void setOnKeepRecordingClickListener( View.OnClickListener onKeepRecordingClickListener ){
        this.onKeepRecordingClickListener = onKeepRecordingClickListener;
    }

    public void setOnDiscardRecordingClickListener( View.OnClickListener onDiscardRecordingClickListener ){
        this.onDiscardRecordingClickListener = onDiscardRecordingClickListener;
    }

    public void show(){
        if( currentState == State.HIDDEN ){
            goToState( stateBeforeHidden );
        } else {
            goToState( currentState );
        }
    }

    public void hide(){
        goToState( State.HIDDEN );
    }

    public void toggleVisibility(){
        if( currentState == State.HIDDEN ){
            show();
        } else {
            hide();
        }
    }

    public void resetProcess(){
        goToState( State.WILl_RECORD_NEW );
    }

    @Override
    public void onClick( View v ){
        if( v.getId() == firstButtonID ){
            switch( RecordingController.this.currentState ){
                case WILl_RECORD_NEW:
                    if( RecordingController.this.onNewRecordingClickListener != null ){
                        RecordingController.this.onNewRecordingClickListener.onClick( v );
                    }
                    RecordingController.this.goToState( State.WILL_START_RECORDING );

                    break;

                case WILL_START_RECORDING:
                    if( RecordingController.this.onStartRecordingClickListener != null ){
                        RecordingController.this.onStartRecordingClickListener.onClick( v );
                    }
                    RecordingController.this.goToState( State.WILL_STOP_RECORDING );

                    break;

                case WILL_STOP_RECORDING:
                    if( RecordingController.this.onStopRecordingClickListener != null ){
                        RecordingController.this.onStopRecordingClickListener.onClick( v );
                    }
                    RecordingController.this.goToState( State.WILL_HANDLE_NEW_RECORDING );

                    break;

                case WILL_HANDLE_NEW_RECORDING:
                    if( RecordingController.this.onKeepRecordingClickListener != null ){
                        RecordingController.this.onKeepRecordingClickListener.onClick( v );
                    }
                    RecordingController.this.goToState( State.WILl_RECORD_NEW );

                    break;

                default:
                    Log.v( RECORDING_CONTROLLER_TAG, "Main/left recording control button is in an invalid state!" );
                    break;
            }
        } else if( v.getId() == secondButtonID ){
            switch( RecordingController.this.currentState ){
                case WILL_HANDLE_NEW_RECORDING:
                    if( RecordingController.this.onDiscardRecordingClickListener != null ){
                        RecordingController.this.onDiscardRecordingClickListener.onClick( v );
                    }
                    RecordingController.this.goToState( State.WILl_RECORD_NEW );

                    break;

                default:
                    Log.v( RECORDING_CONTROLLER_TAG, "Discard/right recording control button is in an invalid state! ( Should not be visible / clickable )" );
                    break;
            }
        } else {
            Log.v( RECORDING_CONTROLLER_TAG, "Spuriously made to respond to unowned view!" );
        }
    }
}
