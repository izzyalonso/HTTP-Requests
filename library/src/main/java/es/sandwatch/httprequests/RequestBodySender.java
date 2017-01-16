package es.sandwatch.httprequests;

import java.io.OutputStream;


/**
 * Interface used to transmit large bodies.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public interface RequestBodySender{
    /**
     * Called when the body is starting to be transmitted.
     *
     * @param requestCode the code of the associated request.
     * @param stream the stream to write the body to.
     */
    void onSendBody(final int requestCode, final OutputStream stream);
}
