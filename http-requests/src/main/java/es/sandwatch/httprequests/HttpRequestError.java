package es.sandwatch.httprequests;

import android.util.Log;

import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;


/**
 * Created by isma on 12/25/15.
 */
public class HttpRequestError{
    private static final String TAG = "HttpRequestError";

    private static final int ERROR_TYPE_SERVER = 1;
    private static final int ERROR_TYPE_NETWORK = 2;
    private static final int ERROR_TYPE_OTHER = 3;

    private final VolleyError mOriginalError;
    private final int mErrorType;
    private final String mMessage;


    public HttpRequestError(VolleyError error){
        mOriginalError = error;

        Log.d(TAG, error.toString());
        NetworkResponse response = error.networkResponse;
        if (error instanceof ServerError && response != null && response.data != null){
            mErrorType = ERROR_TYPE_SERVER;
            mMessage = new String(response.data);
            Log.d(TAG, "Server error: " + response.statusCode);
        }
        else if (error instanceof NoConnectionError || error instanceof NetworkError){
            mErrorType = ERROR_TYPE_NETWORK;
            mMessage = "Offline, check your internet connection";
        }
        else{
            mErrorType = ERROR_TYPE_OTHER;
            mMessage = error.getMessage();
        }
    }
}
