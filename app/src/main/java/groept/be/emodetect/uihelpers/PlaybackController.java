package groept.be.emodetect.uihelpers;

import groept.be.emodetect.R;
import groept.be.emodetect.helpers.recordinghelpers.Stereo16BitPCMAudioFilePlayer;
import groept.be.emodetect.helpers.recordinghelpers.Stereo16BitPCMAudioFilePlayerObserver;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class PlaybackController extends LinearLayout implements Stereo16BitPCMAudioFilePlayerObserver {
    public static final String PLAYBACK_CONTROLLER_TAG = "PlaybackController";

    public static enum State {
        INACTIVE,
        HIDDEN,
        WILL_PLAY,
        WILL_PLAY_OR_STOP,
        WILL_PAUSE_OR_STOP
    }

    private Context context;

    private LinearLayout.LayoutParams containerLayoutParams;

    private ImageButton playButton, pauseButton, stopButton;
    private LinearLayout.LayoutParams buttonLayoutParameters;
    private View.OnClickListener onPlayButtonClickListener;
    private View.OnClickListener onPauseButtonClickListener;
    private View.OnClickListener onStopButtonClickListener;

    private State currentState;
    private State stateWhenReshowing;

    private boolean endInteractionWaitUntilPlayingDone;

    private String name = null;

    private void init( AttributeSet attrs, int defStyleAttr ){
        containerLayoutParams = new LinearLayout.LayoutParams( 0, LayoutParams.MATCH_PARENT, 1 );
        setLayoutParams( containerLayoutParams );
        setOrientation( LinearLayout.HORIZONTAL );
        setVisibility( View.GONE );

        playButton = new ImageButton( context );
        pauseButton = new ImageButton( context );
        stopButton = new ImageButton( context );
        buttonLayoutParameters = new LinearLayout.LayoutParams( 0, LayoutParams.MATCH_PARENT, 1 );
        playButton.setLayoutParams( buttonLayoutParameters );
        pauseButton.setLayoutParams( buttonLayoutParameters );
        stopButton.setLayoutParams( buttonLayoutParameters );
        playButton.setImageResource( R.drawable.md_play_button );
        pauseButton.setImageResource( R.drawable.md_pause_button );
        stopButton.setImageResource( R.drawable.md_stop_button );
        playButton.setVisibility( View.GONE );
        pauseButton.setVisibility( View.GONE );
        stopButton.setVisibility( View.GONE );

        setDescendantFocusability( ViewGroup.FOCUS_BLOCK_DESCENDANTS );
        addView( playButton );
        addView( pauseButton );
        addView( stopButton );

        currentState = State.INACTIVE;
        endInteractionWaitUntilPlayingDone = false;

        View.OnClickListener defaultNoOpListener = new View.OnClickListener(){
            @Override
            public void onClick( View v ){
            }
        };

        setOnPlayButtonClickListener( defaultNoOpListener );
        setOnPauseButtonClickListener( defaultNoOpListener );
        setOnStopButtonClickListener( defaultNoOpListener );
    }

    private void goToState( State newState ){
        switch( newState ){
            case INACTIVE:
            case HIDDEN:
                setVisibility( View.GONE );

                playButton.setVisibility( View.GONE );
                pauseButton.setVisibility( View.GONE );
                stopButton.setVisibility( View.GONE );

                break;

            case WILL_PLAY:
                setVisibility( View.VISIBLE );

                playButton.setVisibility( View.VISIBLE );
                pauseButton.setVisibility( View.GONE );
                stopButton.setVisibility( View.GONE );

                break;

            case WILL_PAUSE_OR_STOP:
                setVisibility( View.VISIBLE );

                playButton.setVisibility( View.GONE );
                pauseButton.setVisibility( View.VISIBLE );
                stopButton.setVisibility( View.VISIBLE );

                break;

            case WILL_PLAY_OR_STOP:
                setVisibility( View.VISIBLE );

                playButton.setVisibility( View.VISIBLE );
                pauseButton.setVisibility( View.GONE );
                stopButton.setVisibility( View.VISIBLE );

                break;

            default:
                updateState( State.INACTIVE );

                Log.v( PLAYBACK_CONTROLLER_TAG, ( "PlaybackController received invalid state! " +
                        "Falling back to INACTIVE state from numeric value " + newState ) );
                break;
        }
    }

    private void updateState( State newState ){
        currentState = newState;

        goToState( newState );
    }

    public PlaybackController( Context context ){
        super( context );
        this.context = context;
        init( null, 0 );
    }

    public PlaybackController( Context context, AttributeSet attrs ){
        super( context, attrs );
        this.context = context;
        init( attrs, 0 );
    }

    public PlaybackController( Context context, AttributeSet attrs, int defStyleAttr ){
        super( context, attrs, defStyleAttr );
        this.context = context;
        init( attrs, defStyleAttr );
    }

    public PlaybackController.State getState(){
        return( currentState );
    }

    public void restoreState( PlaybackController.State storedState ){
        if( storedState == State.HIDDEN ){
            stateWhenReshowing = currentState;
        }

        updateState( storedState );
    }

    public void startInteraction(){
        updateState( State.WILL_PLAY );
    }

    public void endInteraction( boolean waitUntilPlayingDone ){
        if( ( currentState != State.WILL_PLAY ) &&
            ( waitUntilPlayingDone == true ) ){
            this.endInteractionWaitUntilPlayingDone = true;

            Log.d( PLAYBACK_CONTROLLER_TAG, "endInteraction() - waiting to go to INACTIVE" );
        } else {
            updateState( State.INACTIVE );

            Log.d( PLAYBACK_CONTROLLER_TAG, "endInteraction() - going to inactive" );
        }
    }

    public void hide(){
        if( currentState != State.HIDDEN ) {
            stateWhenReshowing = currentState;
        }
        updateState( State.HIDDEN );
    }

    public void reshow(){
        if( currentState == State.HIDDEN ){
            updateState( stateWhenReshowing );
        }
    }

    public void toggleVisibility(){
        if( currentState == State.HIDDEN ){
            reshow();
        } else {
            hide();
        }
    }

    public void setName( String name ){
        this.name = name;
    }

    @Override
    public void update( Stereo16BitPCMAudioFilePlayer.ActionToExecute nextAction ) {
        Log.d(
             PLAYBACK_CONTROLLER_TAG,
             "update() - Playback controller " +
             name +
             " - Updated with nextAction " +
             nextAction.name()
        );
        if( currentState != State.INACTIVE ){
            if( ( endInteractionWaitUntilPlayingDone == true ) &&
                ( ( nextAction == Stereo16BitPCMAudioFilePlayer.ActionToExecute.SHOULD_FINISH ) ||
                  ( nextAction == Stereo16BitPCMAudioFilePlayer.ActionToExecute.SHOULD_STOP ) ) ){
                Log.d( PLAYBACK_CONTROLLER_TAG, "update() - PlaybackController " + name + " is disappearing!" );
                updateState( State.INACTIVE );
                endInteractionWaitUntilPlayingDone = false;
            } else if( nextAction == Stereo16BitPCMAudioFilePlayer.ActionToExecute.SHOULD_FINISH ){
                if( PlaybackController.this.currentState == State.HIDDEN ){
                    stateWhenReshowing = State.WILL_PLAY;
                } else if( PlaybackController.this.currentState != State.INACTIVE ){
                    Log.d( PLAYBACK_CONTROLLER_TAG, "update() - PlaybackController " + name + " is resetting!" );
                    updateState( State.WILL_PLAY );
                }
            } else {
                Log.d( PLAYBACK_CONTROLLER_TAG, "update() - PlaybackController " + name + " is in no need of update." );
            }
        }
    }

    public void setOnPlayButtonClickListener( final View.OnClickListener onPlayButtonClickListener ){
        this.onPlayButtonClickListener = onPlayButtonClickListener;

        playButton.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick( View v ) {
                PlaybackController.this.updateState( State.WILL_PAUSE_OR_STOP );
                PlaybackController.this.onPlayButtonClickListener.onClick( v );
            }
        } );
    }

    public void setOnPauseButtonClickListener( View.OnClickListener onPauseButtonClickListener ){
        this.onPauseButtonClickListener = onPauseButtonClickListener;

        pauseButton.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick( View v ) {
                PlaybackController.this.updateState( State.WILL_PLAY_OR_STOP );
                PlaybackController.this.onPauseButtonClickListener.onClick( v );
            }
        } );
    }

    public void setOnStopButtonClickListener( View.OnClickListener onStopButtonClickListener ){
        this.onStopButtonClickListener = onStopButtonClickListener;

        stopButton.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick( View v ) {
                PlaybackController.this.updateState( State.WILL_PLAY );
                PlaybackController.this.onStopButtonClickListener.onClick( v );
            }
        } );
    }
}
