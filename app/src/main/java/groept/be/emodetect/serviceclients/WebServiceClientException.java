package groept.be.emodetect.serviceclients;

public class WebServiceClientException extends Exception {
    private static final long serialVersionUID = 1L;

    public WebServiceClientException( String message ) {
        super( message );
    }

    public WebServiceClientException( String message, Throwable cause ){
        super( message, cause );
    }
}
