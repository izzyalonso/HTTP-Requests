package es.sandwatch.httprequests;


/**
 * Callback interface for requests.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public interface RequestCallback{
    /**
     * Called when a request is completed successfully.
     *
     * @param requestCode the request code of the particular request.
     * @param result the result of the request as a string.
     */
    void onRequestComplete(int requestCode, String result);

    /**
     * Called when a request fails.
     *
     * @param requestCode the request code of the particular request.
     * @param error the object containing all the information about the error.
     */
    void onRequestFailed(int requestCode, HttpRequestError error);
}
