package es.sandwatch.httprequests;

import android.util.Log;

import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;


/**
 * Contains all the information about a request error.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class HttpRequestError{
    private static final String TAG = "HttpRequestError";

    private static final int ERROR_TYPE_SERVER = 1;
    private static final int ERROR_TYPE_NETWORK = 2;
    private static final int ERROR_TYPE_OTHER = 3;

    private final int mErrorType;
    private final String mMessage;
    private final int mStatusCode;


    /**
     * Constructor. Extracts the relevant information from a VolleyError.
     *
     * @param error the source volley error.
     */
    HttpRequestError(VolleyError error){
        Log.d(TAG, error.toString());
        NetworkResponse response = error.networkResponse;
        if (error instanceof ServerError && response != null && response.data != null){
            mErrorType = ERROR_TYPE_SERVER;
            mMessage = new String(response.data);
            mStatusCode = response.statusCode;
            Log.d(TAG, "Server error: " + response.statusCode);
        }
        else if (error instanceof NoConnectionError || error instanceof NetworkError){
            mErrorType = ERROR_TYPE_NETWORK;
            mMessage = "Offline, check your internet connection";
            mStatusCode = -1;
        }
        else{
            mErrorType = ERROR_TYPE_OTHER;
            mMessage = error.getMessage();
            mStatusCode = -1;
        }
    }

    /**
     * Determines whether this error is a server error.
     *
     * @return true if it is a server error, false otherwise.
     */
    public boolean isServerError(){
        return mErrorType == ERROR_TYPE_SERVER;
    }

    /**
     * Determines whether this error is a network error.
     *
     * @return true if this is a network error, false otherwise.
     */
    public boolean isNetworkError(){
        return mErrorType == ERROR_TYPE_NETWORK;
    }

    /**
     * Gets the message of this error.
     *
     * @return the message of this error.
     */
    public String getMessage(){
        return mMessage;
    }

    /**
     * Gets the HTTP status code if this is a server error.
     *
     * @return the HTTP status code of the server error if this is a server error, -1 otherwise.
     */
    public int returnStatusCode(){
        return mStatusCode;
    }
}
