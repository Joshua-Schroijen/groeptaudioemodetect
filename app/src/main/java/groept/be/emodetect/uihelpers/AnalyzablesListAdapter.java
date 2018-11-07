package groept.be.emodetect.uihelpers;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CheckBox;
import android.widget.TextView;

import groept.be.emodetect.R;
import groept.be.emodetect.helpers.analysishelpers.FeatureAnalysisManager;
import groept.be.emodetect.helpers.miscellaneous.ExceptionHandler;

public class AnalyzablesListAdapter extends BaseAdapter {
    private static class ViewHolder{
        private TextView analyzableRecordingNameView;
        private CheckBox markedAsToAnalyzeView;

        public TextView getAnalyzableRecordingNameView(){
            return( this.analyzableRecordingNameView );
        }

        public void setAnalyzableRecordingNameView( TextView analyzableRecordingNameView ){
            this.analyzableRecordingNameView = analyzableRecordingNameView;
        }

        public CheckBox getMarkedAsToAnalyzeView(){
            return( this.markedAsToAnalyzeView );
        }

        public void setMarkedAsToAnalyzeView( CheckBox markedAsToAnalyzeView ){
            this.markedAsToAnalyzeView = markedAsToAnalyzeView;
        }
    }

    private static class ToAnalyzeWatcher implements CompoundButton.OnCheckedChangeListener{
        private AtomicBoolean toAnalyzeBooleanReference;

        public ToAnalyzeWatcher( AtomicBoolean toAnalyzeBooleanReference ){
            this.toAnalyzeBooleanReference = toAnalyzeBooleanReference;
        }

        @Override
        public void onCheckedChanged( android.widget.CompoundButton compoundButton, boolean b ){
            this.toAnalyzeBooleanReference.set( b );
        }
    }

    public static final String ANALYZABLES_LIST_ADAPTER_TAG = "AnalyzablesListAdapter";

    private Context context;
    private LayoutInflater layoutInflater;
    private ExceptionHandler exceptionHandler;
    private FeatureAnalysisManager featureAnalysisManager;

    private ArrayList< Pair< String, AtomicBoolean > > analyzableRecordings;

    public AnalyzablesListAdapter(
        Context context,
        ExceptionHandler exceptionHandler,
        FeatureAnalysisManager featureAnalysisManager ){
        this.context = context;
        this.exceptionHandler = exceptionHandler;
        this.featureAnalysisManager = featureAnalysisManager;

        layoutInflater = ( LayoutInflater ) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        if( featureAnalysisManager == null ){
            exceptionHandler.handleException( new IllegalArgumentException( "A valid FeatureAnalysisManager is necessary for a AnalyzablesListAdapter" ) );
        } else {
            this.analyzableRecordings = new ArrayList< Pair< String, AtomicBoolean > >();
            ArrayList< String > analyzableRecordingsLeft =
                featureAnalysisManager.getAnalyzableRecordings();
            for( String currentAnalyzableRecording : analyzableRecordingsLeft ){
                this.analyzableRecordings.add(
                    new Pair< String, AtomicBoolean >(
                        currentAnalyzableRecording,
                        new AtomicBoolean( false )
                    )
                );
            }
        }
    }

    @Override
    public int getCount(){
        return( analyzableRecordings.size() );
    }

    @Override
    public Object getItem( int position ){
        return( analyzableRecordings.get( position ).first );
    }

    @Override
    public long getItemId( int position ) {
        return( position );
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent ){
        View newRowView;
        AnalyzablesListAdapter.ViewHolder newRowViewSubviews;

        if( convertView == null ){
            newRowView = layoutInflater.inflate( R.layout.analyzable_recordings_list_item, parent, false );

            TextView analyzableRecordingNameView =
                ( TextView )( newRowView.findViewById( R.id.analyzable_recording_name ) );
            CheckBox markAsToAnalyzeView =
                ( CheckBox )( newRowView.findViewById( R.id.marked_as_to_analyze ) );

            newRowViewSubviews = new AnalyzablesListAdapter.ViewHolder();
            newRowViewSubviews.setAnalyzableRecordingNameView(
                analyzableRecordingNameView
            );
            newRowViewSubviews.setMarkedAsToAnalyzeView(
                markAsToAnalyzeView
            );

            newRowView.setTag( newRowViewSubviews );
        } else {
            newRowView = convertView;

            newRowViewSubviews =
                ( AnalyzablesListAdapter.ViewHolder )( convertView.getTag() );
        }

        newRowViewSubviews.
            getAnalyzableRecordingNameView().
                setText(
                    analyzableRecordings.get( position ).first
                );
        newRowViewSubviews.
            getMarkedAsToAnalyzeView().
                setOnCheckedChangeListener(
                    new ToAnalyzeWatcher(
                        analyzableRecordings.get( position ).second
                    )
                );

        return( newRowView );
    }

    @Override
    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();
    }

    public ArrayList< String > getSelectedAnalyzableRecordings(){
        ArrayList< String > selectedAnalyzableRecordings =
            new ArrayList< String >();

        for( Pair< String, AtomicBoolean > currentAnalyzableRecordingInfoPair : analyzableRecordings ){
            if( currentAnalyzableRecordingInfoPair.second.get() == true ){
                selectedAnalyzableRecordings.add(
                    currentAnalyzableRecordingInfoPair.first
                );
            }
        }

        return( selectedAnalyzableRecordings );
    }
}
