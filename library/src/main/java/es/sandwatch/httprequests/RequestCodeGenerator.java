package es.sandwatch.httprequests;


/**
 * Generates unique request codes. The count resets after generating every million codes.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
abstract class RequestCodeGenerator{
    private static final int MAX_REQUEST_CODE = 999999;

    //Need to keep track of the last request code delivered to avoid collisions
    private static int lastRequestCode = 0;


    /**
     * Generates a unique request code.
     *
     * @return the request code.
     */
    static int generate(){
        if (lastRequestCode >= MAX_REQUEST_CODE){
            lastRequestCode = 0;
        }
        return ++lastRequestCode;
    }
}
