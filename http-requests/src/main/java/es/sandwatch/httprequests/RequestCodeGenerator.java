package es.sandwatch.httprequests;


/**
 * Generates unique request codes. The count resets after generating every million codes.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
class RequestCodeGenerator{
    private static final int MAX_REQUEST_CODE = 999999;

    //Need to keep track of the last request code delivered to avoid collisions
    private static int sLastRequestCode = 0;


    /**
     * Generates a unique request code.
     *
     * @return the request code.
     */
    static int generate(){
        if (sLastRequestCode >= MAX_REQUEST_CODE){
            sLastRequestCode = 0;
        }
        return ++sLastRequestCode;
    }
}
