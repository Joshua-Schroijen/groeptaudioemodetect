package groept.be.emodetect.serviceclients;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.io.IOException;

import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

public class SimpleWebServiceClient{
    private URL webServiceURL;

    public SimpleWebServiceClient( URL webServiceURL ){
        this.webServiceURL = webServiceURL;
    }

    public SimpleWebServiceClient( String webServiceURL ) throws MalformedURLException {
        this.webServiceURL = new URL( webServiceURL );
    }

    public String GET() throws WebServiceClientException{
        try {
            String GETResult = new String();

            URLConnection connection = webServiceURL.openConnection();

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    connection.getInputStream()
                            )
                    );

            String inputLine;
            while( ( inputLine = reader.readLine() ) != null ){
                GETResult += (inputLine + "\n");
            }

            reader.close();

            return( GETResult );
        } catch( IOException e ){
            throw( new WebServiceClientException( e.getMessage(), e ) );
        }
    }

    public String POST( String body ) throws WebServiceClientException{
        try {
            String POSTResult = new String();

            HttpURLConnection connection = ( ( HttpURLConnection ) webServiceURL.openConnection() );
            connection.setDoOutput( true );

            OutputStreamWriter writer =
                    new OutputStreamWriter(
                            connection.getOutputStream()
                    );
            writer.write( body );
            writer.close();

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    connection.getInputStream()
                            )
                    );

            String inputLine;
            while( ( inputLine = reader.readLine() ) != null ){
                POSTResult += ( inputLine + "\n" );
            }
            reader.close();

            return( POSTResult );
        } catch( IOException e ){
            throw( new WebServiceClientException( e.getMessage(), e ) );
        }
    }
}