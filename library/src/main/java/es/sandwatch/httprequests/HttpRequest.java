package es.sandwatch.httprequests;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

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



    //requestCode -> HttpRequest
    private static Map<Integer, HttpRequest> sRequestMap;



    /*--------------------------------------------------------------------------------*
     * THE FOLLOWING METHOD, request(), IS THE CORE OF THIS CLASS. EVERY REQUEST TYPE *
     * METHOD SHOULD WRAP IT TO CREATE THE DESIRED REQUEST.                           *
     *--------------------------------------------------------------------------------*/

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
        HttpRequest request = new HttpRequest(Method.GET, "");
        if (sRequestMap == null){
            sRequestMap = new HashMap<>();
        }
        sRequestMap.put(requestCode, request);

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
                    String utf8String = new String(response.data, "UTF-8");
                    return Response.success(utf8String, HttpHeaderParser.parseCacheHeaders(response));
                }
                catch (UnsupportedEncodingException uex){
                    return Response.error(new VolleyError("Internal error"));
                }
            }
        };

        //Create and set the retry policy
        volleyRequest.setRetryPolicy(new DefaultRetryPolicy(timeout, 0, 1));

        //Set the volley request to the request object
        //request.setRequest(volleyRequest);

        return requestCode;
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

    //This are the attributes that are required for a request to be fired
    private final Method method;
    private final String url;
    private final int requestCode;

    private int timeout;
    private int retries;
    private float backoff;

    private Map<String, String> urlParameters;
    private Map<String, String> headers;

    private String encoding;

    private int attempt;

    private boolean executed;


    /**
     * Constructor.
     *
     * @param method the request method.
     * @param url the url of the request.
     */
    HttpRequest(@NonNull Method method, @NonNull String url){
        this.method = method;
        this.url = url;
        requestCode = RequestCodeGenerator.generate();

        timeout = HttpRequests.getDefaultRequestTimeout();
        retries = HttpRequests.getDefaultRequestRetries();
        backoff = HttpRequests.getDefaultRetryBackoff();
        attempt = 0;

        urlParameters = new HashMap<>();
        Map<String, String> persistentParameters = HttpRequests.getPersistentRequestUrlParameters();
        for (Map.Entry<String, String> entry:persistentParameters.entrySet()){
            urlParameters.put(entry.getKey(), entry.getValue());
        }
        headers = new HashMap<>();
        Map<String, String> persistentHeaders = HttpRequests.getPersistentRequestHeaders();
        for (Map.Entry<String, String> entry:persistentHeaders.entrySet()){
            headers.put(entry.getKey(), entry.getValue());
        }

        encoding = HttpRequests.getDefaultEncoding();

        executed = false;
    }

    public HttpRequest addUrlParameter(String parameter, String value){
        if (!executed){
            urlParameters.put(parameter, value);
        }
        return this;
    }

    public HttpRequest removeUrlParameter(String parameter){
        if (!executed){
            urlParameters.remove(parameter);
        }
        return this;
    }

    public HttpRequest addHeader(String header, String value){
        if (!executed){
            headers.put(header, value);
        }
        return this;
    }

    public HttpRequest removeHeader(String header){
        if (!executed){
            headers.remove(header);
        }
        return this;
    }

    public HttpRequest setTimeout(int timeout){
        if (!executed){
            this.timeout = timeout;
        }
        return this;
    }

    public HttpRequest setRetries(int retries){
        if (!executed){
            this.retries = retries;
        }
        return this;
    }

    public HttpRequest setBackoff(float backoff){
        if (!executed){
            this.backoff = backoff;
        }
        return this;
    }

    public int execute(){
        return requestCode;
    }

    public void cancel(){

    }

    Method getMethod(){
        return method;
    }

    String getUrl(){
        String url = this.url;
        for (Map.Entry<String, String> parameter:urlParameters.entrySet()){
            url += url.contains("?") ? "&" : "?";
            url += parameter.getKey() + "=" + parameter.getValue();
        }
        return url;
    }

    int getRequestCode(){
        return requestCode;
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
