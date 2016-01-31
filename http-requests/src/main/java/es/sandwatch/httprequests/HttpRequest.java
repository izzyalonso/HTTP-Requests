package es.sandwatch.httprequests;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;


/**
 * Makes network requests using the HTTP protocol.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public final class HttpRequest{
    private static final String TAG = "HttpRequest";

    private static final int DEFAULT_REQUEST_TIMEOUT = 10*1000;
    private static final int DEFAULT_REQUEST_RETRIES = 0;
    private static final float DEFAULT_RETRY_BACKOFF = 1.5f;

    private static final String DEFAULT_ENCODING = "UTF-8";


    //Retry policy values
    private static int sRequestTimeout = DEFAULT_REQUEST_TIMEOUT;
    private static int sRequestRetries = DEFAULT_REQUEST_RETRIES;
    private static float sRetryBackoff = DEFAULT_RETRY_BACKOFF;

    //Encoding
    private static String sEncoding = DEFAULT_ENCODING;

    //Request headers
    private static Map<String, String> sRequestHeaders;
    private static Map<String, String> sRequestUrlParams;

    //requestCode -> HttpRequest
    private static Map<Integer, HttpRequest> sRequestMap;
    private static RequestQueue sRequestQueue;


    /**
     * Overrides the existing request timeout value.
     *
     * @param requestTimeout the new request timeout value in milliseconds.
     */
    public static void setRequestTimeout(int requestTimeout){
        sRequestTimeout = requestTimeout;
    }

    /**
     * Overrides the existing maximum retry number.
     *
     * @param requestRetries the new maximum number of retries.
     */
    public static void setRequestRetries(int requestRetries){
        sRequestRetries = requestRetries;
    }

    /**
     * Overrides the existing backoff value. The backoff is the increase in the timeout value
     * after a request fails. When that happens, the new request timeout is calculated as the
     * product of the current request timeout times the backoff.
     *
     * @param retryBackoff the timeout backoff.
     */
    public static void setRetryBackoff(float retryBackoff){
        sRetryBackoff = retryBackoff;
    }

    /**
     * Overrides the existing charset used to parse the network response.
     *
     * @param encoding the name of the new encoding to be used.
     */
    public static void setEncoding(String encoding){
        if (Charset.availableCharsets().containsKey(encoding)){
            sEncoding = encoding;
        }
    }

    /**
     * Initialises the library.
     *
     * @param context a reference to the context.
     */
    public static void init(@NonNull Context context){
        if (sRequestQueue == null){
            sRequestQueue = Volley.newRequestQueue(context);
        }
    }

    /**
     * Tells whether the system has been initialised.
     *
     * @return true if the system is initialised, false otherwise.
     */
    private static boolean isInitialised(){
        return sRequestQueue != null;
    }

    /**
     * Checks whether the class has been initialised, if not, throws an exception.
     */
    private static void checkInitialisation(){
        if (!isInitialised()){
            throw new IllegalStateException("HttpRequest needs to be initialised before used.");
        }
    }

    /**
     * Adds a header to the header list to be sent with every request.
     *
     * @param header the header name.
     * @param value the value of the header.
     */
    public static void addHeader(String header, String value){
        if (sRequestHeaders == null){
            sRequestHeaders = new HashMap<>();
        }
        sRequestHeaders.put(header, value);
    }

    /**
     * Removes a header from the header list to be sent with every request.
     *
     * @param header the header name.
     * @return true if the header was removed, false otherwise.
     */
    public static boolean removeHeader(String header){
        return sRequestHeaders != null && sRequestHeaders.remove(header) != null;
    }

    /**
     * Adds a parameter to the URLs to send requests to.
     *
     * @param parameter the key of the parameter.
     * @param value the value of the parameter.
     */
    public static void addUrlParameter(String parameter, String value){
        if (sRequestUrlParams == null){
            sRequestUrlParams = new HashMap<>();
        }
        sRequestUrlParams.put(parameter, value);
    }

    /**
     * Removes a parameter from the list of parameters to add to the URLs to send requests to.
     *
     * @param parameter the key of the parameter.
     * @return true if the parameter was removed, false otherwise.
     */
    public static boolean removeUrlParameter(String parameter){
        return sRequestUrlParams != null && sRequestUrlParams.remove(parameter) != null;
    }


    /*----------------------------------------------------------*
     * HELPER METHODS. THESE CREATE REQUESTS OF SPECIFIC TYPES. *
     *----------------------------------------------------------*/

    /**
     * Makes a GET request using the default timeout.
     *
     * @param callback the callback object.
     * @param url the url to send the request to.
     * @return a request code.
     */
    public static int get(@NonNull RequestCallback callback, @NonNull String url){
        return request(Method.GET, callback, url, null, sRequestTimeout);
    }

    /**
     * Makes a GET request.
     *
     * @param callback the callback object.
     * @param url the url to send the request to.
     * @param timeout the timeout in milliseconds of this request.
     * @return a request code.
     */
    public static int get(@NonNull RequestCallback callback, @NonNull String url, int timeout){
        return request(Method.GET, callback, url, null, timeout);
    }

    /**
     * Makes a POST request using the default timeout.
     *
     * @param callback the callback object.
     * @param url the url to send the request to.
     * @param body the body of this request.
     * @return a request code.
     */
    public static int post(@Nullable RequestCallback callback, @NonNull String url,
                           @NonNull JSONObject body){

        return request(Method.POST, callback, url, body, sRequestTimeout);
    }

    /**
     * Makes a POST request.
     *
     * @param callback the callback object.
     * @param url the url to send the request to.
     * @param body the body of this request.
     * @param timeout the timeout in milliseconds of this request.
     * @return a request code.
     */
    public static int post(@Nullable RequestCallback callback, @NonNull String url,
                           @NonNull JSONObject body, int timeout){

        return request(Method.POST, callback, url, body, timeout);
    }

    /**
     * Makes a PUT request using the default timeout.
     *
     * @param callback the callback object.
     * @param url the url to send the request to.
     * @param body the body of this request.
     * @return a request code.
     */
    public static int put(@Nullable RequestCallback callback, @NonNull String url,
                          @NonNull JSONObject body){

        return request(Method.PUT, callback, url, body, sRequestTimeout);
    }

    /**
     * Makes a PUT request.
     *
     * @param callback the callback object.
     * @param url the url to send the request to.
     * @param body the body of this request.
     * @param timeout the timeout in milliseconds of this request.
     * @return a request code.
     */
    public static int put(@Nullable RequestCallback callback, @NonNull String url,
                          @NonNull JSONObject body, int timeout){

        return request(Method.PUT, callback, url, body, timeout);
    }

    /**
     * Makes a DELETE request using the default timeout.
     *
     * @param callback the callback object.
     * @param url the url to send the request to.
     * @return a request code.
     */
    public static int delete(@Nullable RequestCallback callback, @NonNull String url){
        return request(Method.DELETE, callback, url, null, sRequestTimeout);
    }

    /**
     * Makes a DELETE request.
     *
     * @param callback the callback object.
     * @param url the url to send the request to.
     * @param timeout the timeout in milliseconds of this request.
     * @return a request code.
     */
    public static int delete(@Nullable RequestCallback callback, @NonNull String url, int timeout){
        return request(Method.DELETE, callback, url, null, timeout);
    }

    /**
     * Cancels a request if the request is still active.
     *
     * @param requestCode the request code of the request to cancel.
     * @return true if the request was cancelled successfully, false otherwise.
     */
    public static boolean cancel(int requestCode){
        if (sRequestMap != null){
            HttpRequest request = sRequestMap.remove(requestCode);
            if (request != null){
                request.mRequest.cancel();
                return true;
            }
        }
        return false;
    }


    /*--------------------------------------------------------------------------------*
     * THE FOLLOWING METHOD, request(), IS THE CORE OF THIS CLASS. EVERY REQUEST TYPE *
     * METHOD SHOULD WRAP IT TO CREATE THE DESIRED REQUEST.                           *
     *--------------------------------------------------------------------------------*/

    /**
     * Creates a request with the default timeout.
     *
     * @param method the HTTP method of this request.
     * @param callback the callback object.
     * @param url the url to make the request to.
     * @param body the body of the request.
     * @return the request code.
     */
    public static int request(Method method, @Nullable RequestCallback callback, @NonNull String url,
                              @Nullable JSONObject body){

        return request(method, callback, url, body, sRequestTimeout);
    }

    /**
     * Creates a request.
     *
     * @param method the HTTP method of this request.
     * @param callback the callback object.
     * @param url the url to make the request to.
     * @param body the body of the request.
     * @param timeout a request timeout value.
     * @return the request code.
     */
    public static int request(Method method, @Nullable RequestCallback callback, @NonNull String url,
                              @Nullable JSONObject body, int timeout){

        //If the class has not yet been initialised the request can't be carried out and
        //  an Exception is thrown
        checkInitialisation();

        //Generate the request code
        final int requestCode = RequestCodeGenerator.generate();

        //Create the request object and put it in the map
        HttpRequest request = new HttpRequest(callback, body);
        if (sRequestMap == null){
            sRequestMap = new HashMap<>();
        }
        sRequestMap.put(requestCode, request);

        url = processUrl(url);

        //Request a string response from the provided URL
        StringRequest volleyRequest = new StringRequest(
                //Method and url
                method.getMethod(), url,

                //Response listener, called if the request succeeds
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        HttpRequest request = sRequestMap.remove(requestCode);
                        if (request.mCallback != null){
                            request.mCallback.onRequestComplete(requestCode, response);
                        }
                    }
                },

                //Error listener, called if the request fails
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        handleError(requestCode, error);
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                if (sRequestHeaders == null){
                    return new HashMap<>();
                }
                return sRequestHeaders;
            }

            @Override
            public String getBodyContentType(){
                return "application/json";
            }

            @Override
            public byte[] getBody() throws AuthFailureError{
                return sRequestMap.get(requestCode).mBody.toString().getBytes();
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response){
                try{
                    String utf8String = new String(response.data, sEncoding);
                    return Response.success(utf8String, HttpHeaderParser.parseCacheHeaders(response));
                }
                catch (UnsupportedEncodingException uex){
                    return Response.error(new VolleyError("Internal error"));
                }
            }
        };

        //Create and set the retry policy
        volleyRequest.setRetryPolicy(new DefaultRetryPolicy(timeout, sRequestRetries, sRetryBackoff));

        //Set the volley request to the request object
        request.setRequest(volleyRequest);

        //Add the request to the queue
        sRequestQueue.add(volleyRequest);

        return requestCode;
    }

    /**
     * Adds the parameters to the URL
     *
     * @param url the url to append the parameters to.
     * @return the processed url.
     */
    private static String processUrl(@NonNull String url){
        if (sRequestUrlParams != null){
            for (Map.Entry<String, String> parameter : sRequestUrlParams.entrySet()){
                url += !url.contains("?") ? "?" : "&";
                url += parameter.getKey() + "=" + parameter.getValue();
            }
        }
        return url;
    }

    /**
     * Creates the HttpRequestError object and delivers it to the callback if there is one.
     *
     * @param requestCode the request code of the subject request.
     * @param error the VolleyError to generate the error from.
     */
    private static void handleError(int requestCode, VolleyError error){
        HttpRequest request = sRequestMap.remove(requestCode);
        if (request.mCallback != null){
            request.mCallback.onRequestFailed(requestCode, new HttpRequestError(error));
        }
    }


    /*----------------------------------------------*
     * HttpRequest OBJECT ATTRIBUTES AND METHODS *
     *----------------------------------------------*/

    private final RequestCallback mCallback;
    private final JSONObject mBody;

    private StringRequest mRequest;


    /**
     * Constructor.
     *
     * @param callback the callback object for this request.
     * @param body the body of the request.
     */
    private HttpRequest(@Nullable RequestCallback callback, @Nullable JSONObject body){
        mCallback = callback;
        if (body == null){
            body = new JSONObject();
        }
        mBody = body;
    }

    /**
     * Associates the volley request with this HttpRequest. Useful to cancel requests.
     *
     * @param request the volley request.
     */
    private void setRequest(@NonNull StringRequest request){
        mRequest = request;
    }


    /**
     * Enumeration containing all the allowed methods.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public enum Method{
        GET(Request.Method.GET),
        POST(Request.Method.POST),
        PUT(Request.Method.PUT),
        DELETE(Request.Method.DELETE);

        int mMethod;


        /**
         * Constructor. Sets the method mapping so that Volley can understand it.
         *
         * @param method Volley's representation of the method.
         */
        Method(int method){
            mMethod = method;
        }

        /**
         * Getter for the Volley's representation of the method.
         *
         * @return Volley's representation of the method.
         */
        private int getMethod(){
            return mMethod;
        }
    }


    /**
     * Callback interface for HttpRequest.
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
}
