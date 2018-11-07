package groept.be.emodetect.serviceclients;

import android.os.AsyncTask;
import android.util.Log;

import java.net.URL;

import groept.be.emodetect.helpers.miscellaneous.ExceptionHandler;

public class SimpleWebServiceClientGet extends AsyncTask< String, Integer, String > implements ExceptionHandler{
    public final static String WEB_SERVICE_CLIENT_GET_TAG = "WebServiceClientGet";

    private SimpleWebServiceClient webServiceClient;

    private boolean success;

    private WebServiceGetResultHandler webServiceGetResultHandler;

    public SimpleWebServiceClientGet( URL webServiceURL, WebServiceGetResultHandler webServiceGetResultHandler ){
        super();

        this.webServiceClient = new SimpleWebServiceClient( webServiceURL );
        this.webServiceGetResultHandler = webServiceGetResultHandler;

        this.success = false;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground( String... params ) {
        try {
            success = false;
            String webServiceClientResult = webServiceClient.GET();
            success = true;
            return( webServiceClientResult );
        } catch( Exception e ){
            success = false;
            handleException( e );
            return( null );
        }
    }

    @Override
    protected void onPostExecute( String result ){
        if( ( success == true ) &&
            ( result != null ) ){
            webServiceGetResultHandler.handleGetResult( result );
        }
    }

    @Override
    public void handleException( Exception exception ){
        Log.d( WEB_SERVICE_CLIENT_GET_TAG, "Exception cause: " + exception.getCause() );
        Log.d( WEB_SERVICE_CLIENT_GET_TAG, "Exception stack trace: " + exception.getStackTrace() );
        Log.d( WEB_SERVICE_CLIENT_GET_TAG, "Exception message: " + exception.getMessage() );
    }
}