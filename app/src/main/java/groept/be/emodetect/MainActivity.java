package groept.be.emodetect;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.net.MalformedURLException;
import java.net.URL;

import groept.be.emodetect.fragments.RecordPlaybackTabFragment;
import groept.be.emodetect.fragments.TestAnalyzeTabFragment;
import groept.be.emodetect.serviceclients.SimpleWebServiceClientGet;
import groept.be.emodetect.serviceclients.WebServiceGetResultHandler;
import groept.be.emodetect.settingchangelisteners.WebServiceURLSettingChangeListener;
import groept.be.emodetect.uihelpers.MainActivityFragmentPagerAdapter;

public class MainActivity extends AppCompatActivity implements WebServiceURLSettingChangeListener {
    public static class Employee {
        String name;
        int age;
        String title;

        public Employee( String name, int age, String title ){
            this.name = name;
            this.age = age;
            this.title = title;
        }
    }

    public static final String MAIN_ACTIVITY_TAG = "MAIN_ACTIVITY";

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib" );
    }

    /***************************
     * USER INTERFACE HANDLING *
     **************************/
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MainActivityFragmentPagerAdapter fragmentPagerAdapter;

    private RecordPlaybackTabFragment recordPlaybackTabFragment;

    /************************
     * PERMISSIONS HANDLING *
     ***********************/
    /* We use these objects to access the microphone and store the data */

    /* First we need integer constants to use as identifiers for the
     * different kinds of permission(s) requests needed
     *
     * Please note that we should be able to combine these by OR'ing them,
     * so make sure no constant equals other constants OR'ed ...
     * This can be done by making the constant either 1 or a power of 2 ...
     */
    private final int WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST = 1;
    private final int RECORD_AUDIO_PERMISSION_REQUEST = 2;

    /* This is a small convenience enum to check permission allocation state
     */
    private static enum PermissionState {
        ALREADY_HAD_PERMISSION,
        PERMISSION_REQUESTED,
        PERMISSION_REQUESTED_AND_DENIED,
        PERMISSION_REQUESTED_AND_GRANTED
    }

    /* This field will be used to poll on a pending permissions request, so
     * we can pause our application until we have a permission
     */
    PermissionState mainPermissionState;

    /* This callback method will handle the result of requesting any permission
     */
    @Override
    public void onRequestPermissionsResult( int requestCode, String[] permissions, int[] grantResults ){
        switch( requestCode ){
            case( ( WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST |
                    RECORD_AUDIO_PERMISSION_REQUEST ) ):
                if( ( grantResults.length > 0 ) &&
                    ( grantResults[ 0 ] == PackageManager.PERMISSION_GRANTED ) &&
                    ( grantResults[ 1 ] == PackageManager.PERMISSION_GRANTED ) ){
                    mainPermissionState = PermissionState.PERMISSION_REQUESTED_AND_GRANTED;

                    onPermissionsGranted();
                } else {
                    mainPermissionState = PermissionState.PERMISSION_REQUESTED_AND_DENIED;

                    onPermissionsDenied();
                }
                break;

            default:
                Log.v( MAIN_ACTIVITY_TAG, "Warning: unsupported permission granted! EmoDetect might behave unexpectedly" );
                break;
        }
    }

    /* This method will try and make sure our application has permission to write
     * to external storage and record audio. We use the standard framework API
     * through the ContextCompat and ActivityCompat classes for backwards
     * compatibility.
     *
     * If we already had the permissions, the method will return
     * PermissionState.ALREADY_HAD_PERMISSION
     * If we didn't have (one of) the permissions, the method will
     * request it from the system and return
     * PermissionState.PERMISSION_REQUESTED
     */
    private void getPermissionToWriteExternalStorageAndRecordAudio(){
        if( ( ContextCompat.checkSelfPermission( this, Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED ) ||
            ( ContextCompat.checkSelfPermission( this, Manifest.permission.RECORD_AUDIO ) != PackageManager.PERMISSION_GRANTED ) ){
            String[] permissionsToRequest = { Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                              Manifest.permission.RECORD_AUDIO };
            int permissionsRequestCode = ( WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST | RECORD_AUDIO_PERMISSION_REQUEST );

            if( ActivityCompat.shouldShowRequestPermissionRationale( this, Manifest.permission.WRITE_EXTERNAL_STORAGE ) ){
                Toast.makeText( this,"Without this permission, EmoDetect cannot save your recordings!", Toast.LENGTH_SHORT ).show();
            }
            if( ActivityCompat.shouldShowRequestPermissionRationale( this, Manifest.permission.RECORD_AUDIO ) ){
                Toast.makeText( this,"Without this permission, EmoDetect cannot record your voice!", Toast.LENGTH_SHORT ).show();
            }

            ActivityCompat.requestPermissions( this,
                                               permissionsToRequest,
                                               permissionsRequestCode );

            mainPermissionState = mainPermissionState.PERMISSION_REQUESTED;
        } else {
            mainPermissionState = mainPermissionState.ALREADY_HAD_PERMISSION;
        }
    }

    /**********************
     * PREFERENCE LOADING *
     **********************/
    /* These variables' values are dependent on preference values
     * and will be loaded when the activity is created through the
     * loadPreferences method!
     */
    String backendWebServiceURL;

    protected void loadPreferences(){
        backendWebServiceURL = SettingsActivity.getWebServiceURLFromPreferences( getApplicationContext() );
    }

    @Override
    public void updateWebServiceURL( String newWebServiceURL ){
        backendWebServiceURL = newWebServiceURL;
    }

    SimpleWebServiceClientGet newGetTask = null;

    public void callWebService( View v ){
        try{
            if( newGetTask == null ) {
                newGetTask =
                    new SimpleWebServiceClientGet(
                        new URL( backendWebServiceURL ),
                        new WebServiceGetResultHandler(){
                            @Override
                                public void handleGetResult( String returnData ){
                                    Log.d( MainActivity.this.MAIN_ACTIVITY_TAG,
                                     "EmoDetect web service GET returned: " + returnData );
                                    }
                                }
                        );
            }

            newGetTask.execute();
        } catch( MalformedURLException e ){
            Log.d( MAIN_ACTIVITY_TAG, "EmoDetect web service URL was malformed. Exception message:\n" + e.getMessage() + "\nEmoDetect web service GET task not executed!" );
        }
    }

    @Override
    protected void onCreate( Bundle savedInstanceState ){
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        Toolbar toolbar = ( Toolbar ) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        /* ////////////////////////////////////////////
         * CUSTOM APP INITIALIZATION BEINGS HERE      /
         * ///////////////////////////////////////// */

        /* Here we load the user preferences
         */
        loadPreferences();

        /* Here we register our activity as a watcher of several of
         * our settings (we do this only if it's absolutely needed)
         */
        SettingsActivity.registerWebServiceURLSettingChangeListener( this );

        /* Here we set up the tab layout!
         */
        recordPlaybackTabFragment = new RecordPlaybackTabFragment();

        fragmentPagerAdapter = new MainActivityFragmentPagerAdapter( getSupportFragmentManager(), getApplicationContext() );
        fragmentPagerAdapter.setRecordPlaybackTabFragment( recordPlaybackTabFragment );
        fragmentPagerAdapter.setTestAnalyzeTabFragment( new TestAnalyzeTabFragment() );

        viewPager = ( ( ViewPager )( findViewById( R.id.main_activity_tab_pager ) ) );
        viewPager.setAdapter( fragmentPagerAdapter );

        tabLayout = ( TabLayout ) findViewById( R.id.main_activity_tab_layout );
        tabLayout.setupWithViewPager( viewPager );

        /* Here we make sure our application has the required permissions.
         * If we don't have those permissions, the application will terminate!
         */
        getPermissionToWriteExternalStorageAndRecordAudio();

        TarsosTester.tryIt(this.getApplicationContext());
    }

    public void onPermissionsGranted(){
        /* Here we create a directory on the SD card for storing
         * the recorded speech
         */
        recordPlaybackTabFragment.
            getRecordPlaybackTabModel().
            getRecordingFunctionalityManager().
            createSpeechFragmentStorageDirectory();

        Log.d( MAIN_ACTIVITY_TAG, "ONPERMISSIONGRANTED" );
    }

    public void onPermissionsDenied(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( this );
        alertDialogBuilder.setMessage( R.string.error_necessary_permissions_denied );
        alertDialogBuilder.setTitle( "Error" );
        alertDialogBuilder.setCancelable( false );
        alertDialogBuilder.setPositiveButton(
            "OK",
            new DialogInterface.OnClickListener(){
                @Override
                public void onClick( DialogInterface dialog, int which ){
                    MainActivity.this.finish();
                    System.exit( 1 );
                }
            } );

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        float textSize =
                ( ( getResources().getDimension( R.dimen.error_dialog_text_size ) ) /
                  ( getResources().getDisplayMetrics().density ) );
        TextView errorMessage = alertDialog.findViewById( android.R.id.message );
        errorMessage.setTextSize( textSize );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.menu_main, menu );
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ){
        boolean standardReturnValue = super.onOptionsItemSelected( item );

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if( id == R.id.action_settings ){
            Intent settingsIntent = new Intent( getApplicationContext(), SettingsActivity.class );
            startActivity( settingsIntent );

            return( true );
        }

        return( standardReturnValue );
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
