package groept.be.emodetect.uihelpers;

import groept.be.emodetect.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.View;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.support.v7.widget.AppCompatSeekBar;

public class LabeledSlider extends LinearLayout {
    private String purpose;
    private int min;
    private int max;

    private double value;

    private double calculateValue( int seekBarProgess ){
        double scaleFactor = ( ( max - min ) / 100.0 );
        double scaledSeekBarProgress = ( scaleFactor * seekBarProgess );
        double finalValue = scaledSeekBarProgress + min;
        return( finalValue );
    }

    public LabeledSlider( Context context, AttributeSet attrs ){
        super( context, attrs );

        this.purpose = attrs.getAttributeValue( "http://schemas.android.com/apk/res-auto", "sliderPurpose" );
        this.min = attrs.getAttributeIntValue( "http://schemas.android.com/apk/res-auto", "sliderMin", 0 );
        this.max = attrs.getAttributeIntValue( "http://schemas.android.com/apk/res-auto", "sliderMax", 100 );

        LayoutInflater inflater = LayoutInflater.from( context );
        View inflatedView =
            inflater.inflate( R.layout.labeled_slider_view_layout, this );

        final TextView inputValueIndicator =
            inflatedView.findViewById( R.id.labeled_slider_value_label );
        inputValueIndicator.setText( this.purpose + " value: " );
        AppCompatSeekBar slider =
            inflatedView.findViewById( R.id.labeled_slider_slider );
        slider.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged( SeekBar seekBar, int i, boolean b ){
                LabeledSlider.this.value = LabeledSlider.this.calculateValue( i );
                inputValueIndicator.setText(
                    LabeledSlider.this.purpose +
                    " value: " +
                    String.format( "%.2f", LabeledSlider.this.value )
                );
            }

            @Override
            public void onStartTrackingTouch( SeekBar seekBar ){
            }

            @Override
            public void onStopTrackingTouch( SeekBar seekBar ){
            }
        } );
    }

    public double getValue(){
        return( this.value );
    }
}