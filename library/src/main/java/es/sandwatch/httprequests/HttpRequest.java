package es.sandwatch.httprequests;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
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



    //requestCode -> HttpRequest
    private static Map<Integer, HttpRequest> sRequestMap;
    private static RequestQueue sRequestQueue;


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
        return request(Method.GET, callback, url, null, DEFAULT_REQUEST_TIMEOUT);
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

        return request(Method.POST, callback, url, body, DEFAULT_REQUEST_TIMEOUT);
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

        return request(Method.PUT, callback, url, body, DEFAULT_REQUEST_TIMEOUT);
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
        return request(Method.DELETE, callback, url, null, DEFAULT_REQUEST_TIMEOUT);
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
                //request.mRequest.cancel();
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

        return request(method, callback, url, body, DEFAULT_REQUEST_TIMEOUT);
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

        //Generate the request code
        final int requestCode = RequestCodeGenerator.generate();

        //Create the request object and put it in the map
        HttpRequest request = new HttpRequest(Method.GET, callback);
        if (sRequestMap == null){
            sRequestMap = new HashMap<>();
        }
        sRequestMap.put(requestCode, request);

        url = processUrl(url);

        //Request a string response from the provided URL
        StringRequest volleyRequest = new StringRequest(
                //Method and url
                0, url,

                //Response listener, called if the request succeeds
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        HttpRequest request = sRequestMap.remove(requestCode);
                        /*if (request.mCallback != null){
                            request.mCallback.onRequestComplete(requestCode, response);
                        }*/
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
                return new HashMap<>();
            }

            @Override
            public String getBodyContentType(){
                return "application/json";
            }

            @Override
            public byte[] getBody() throws AuthFailureError{
                return new JSONObject().toString().getBytes();
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response){
                try{
                    String utf8String = new String(response.data, DEFAULT_ENCODING);
                    return Response.success(utf8String, HttpHeaderParser.parseCacheHeaders(response));
                }
                catch (UnsupportedEncodingException uex){
                    return Response.error(new VolleyError("Internal error"));
                }
            }
        };

        //Create and set the retry policy
        volleyRequest.setRetryPolicy(new DefaultRetryPolicy(timeout, DEFAULT_REQUEST_RETRIES, DEFAULT_RETRY_BACKOFF));

        //Set the volley request to the request object
        //request.setRequest(volleyRequest);

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
        /*if (sRequestUrlParams != null){
            for (Map.Entry<String, String> parameter : sRequestUrlParams.entrySet()){
                url += !url.contains("?") ? "?" : "&";
                url += parameter.getKey() + "=" + parameter.getValue();
            }
        }*/
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
        /*if (request.mCallback != null){
            request.mCallback.onRequestFailed(requestCode, new HttpRequestError(error));
        }*/
    }


    /*-------------------------------------------*
     * HttpRequest OBJECT ATTRIBUTES AND METHODS *
     *-------------------------------------------*/

    private final Method method;
    private final RequestCallback callback;

    private Map<String, String> urlParameters;
    private Map<String, String> headers;

    private int timeout;
    private int retries;
    private float backoff;

    private int attempt;

    private boolean executed;


    /**
     * Constructor.
     *
     * @param callback the callback object for this request.
     */
    HttpRequest(@NonNull Method method, @NonNull RequestCallback callback){
        this.method = method;
        this.callback = callback;
        urlParameters = new HashMap<>();
        headers = new HashMap<>();
        attempt = 0;
        executed = false;
    }

    public void addUrlParameter(String parameter, String value){
        if (!executed){
            urlParameters.put(parameter, value);
        }
    }

    public void removeUrlParameter(String parameter){
        if (!executed){
            urlParameters.remove(parameter);
        }
    }

    public void addHeader(String header, String value){
        if (!executed){
            headers.put(header, value);
        }
    }

    public void removeHeader(String header){
        if (!executed){
            headers.remove(header);
        }
    }

    public void setTimeout(int timeout){
        if (!executed){
            this.timeout = timeout;
        }
    }

    public void setRetries(int retries){
        if (!executed){
            this.retries = retries;
        }
    }

    public void setBackoff(float backoff){
        if (!executed){
            this.backoff = backoff;
        }
    }


    /**
     * Contains all the allowed methods.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    enum Method{
        GET, POST, PUT, DELETE
    }
}
