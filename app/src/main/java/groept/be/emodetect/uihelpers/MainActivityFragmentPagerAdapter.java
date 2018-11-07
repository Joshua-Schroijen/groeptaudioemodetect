package groept.be.emodetect.uihelpers;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import groept.be.emodetect.R;
import groept.be.emodetect.fragments.RecordPlaybackTabFragment;
import groept.be.emodetect.fragments.TestAnalyzeTabFragment;

public class MainActivityFragmentPagerAdapter extends FragmentPagerAdapter {
    private Context context;

    private FragmentManager fragmentManger;

    private RecordPlaybackTabFragment recordPlaybackTabFragment;
    private TestAnalyzeTabFragment testAnalyzeTabFragment;

    public MainActivityFragmentPagerAdapter( FragmentManager fragmentManger, Context context ){
        super( fragmentManger );

        this.context = context;

        this.fragmentManger = fragmentManger;
    }

    public void setRecordPlaybackTabFragment( RecordPlaybackTabFragment recordPlaybackTabFragment ){
        this.recordPlaybackTabFragment = recordPlaybackTabFragment;
    }

    public RecordPlaybackTabFragment getRecordPlaybackTabFragment(){
        return( recordPlaybackTabFragment );
    }

    public void setTestAnalyzeTabFragment( TestAnalyzeTabFragment testAnalyzeTabFragment ){
        this.testAnalyzeTabFragment = testAnalyzeTabFragment;
    }

    public TestAnalyzeTabFragment getTestAnalyzeTabFragment(){
        return( testAnalyzeTabFragment );
    }

    @Override
    public Fragment getItem( int position ){
        switch( position ){
            case 0:
                return( recordPlaybackTabFragment );
            case 1:
                return( testAnalyzeTabFragment );

            default:
                return( null );
        }
    }

    @Override
    public CharSequence getPageTitle( int position ){
        switch( position ){
            case 0:
                return( context.getString( R.string.record_and_playback_tab_name ) );

            case 1:
                return( context.getString( R.string.analyze_recordings_tab_name ) );

            default:
                return( null );
        }
    }

    @Override
    public int getCount(){
        return( 2 );
    }
}
