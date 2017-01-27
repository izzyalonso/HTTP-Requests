package es.sandwatch.httprequests;


import android.support.annotation.NonNull;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;


/**
 * Class in charge of establishing default values.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public final class HttpRequests{
    private static final String TAG = "HttpRequests";

    //Defaults
    private static final int DEFAULT_REQUEST_TIMEOUT = 10*1000;
    private static final int DEFAULT_REQUEST_RETRIES = 0;
    private static final float DEFAULT_RETRY_BACKOFF = 1.5f;

    private static final String DEFAULT_ENCODING = "UTF-8";


    //Retry policy values
    private static int requestTimeout = DEFAULT_REQUEST_TIMEOUT;
    private static int requestRetries = DEFAULT_REQUEST_RETRIES;
    private static float retryBackoff = DEFAULT_RETRY_BACKOFF;

    //Default request headers and url parameters
    private static Map<String, String> requestUrlParameters;
    private static Map<String, String> requestHeaders;

    //Encoding
    private static String encoding = DEFAULT_ENCODING;


    /**
     * Sets a default request timeout.
     *
     * @param requestTimeout the default request timeout value in milliseconds.
     */
    public static void setDefaultRequestTimeout(int requestTimeout){
        HttpRequests.requestTimeout = requestTimeout;
    }

    /**
     * Sets a default maximum retry number.
     *
     * @param requestRetries the default maximum number of retries.
     */
    public static void setDefaultRequestRetries(int requestRetries){
        HttpRequests.requestRetries = requestRetries;
    }

    /**
     * Sets a default backoff value. The backoff is the increase in the timeout value after a
     * request fails. When that happens, the new request timeout is calculated as the product
     * of the current request timeout times the backoff.
     *
     * @param retryBackoff the timeout backoff.
     */
    public static void setDefaultRetryBackoff(float retryBackoff){
        HttpRequests.retryBackoff = retryBackoff;
    }

    /**
     * Adds a parameter to the list of URL parameters to be sent with every request. If the
     * parameter already exists the value gets replaced.
     *
     * @param parameter the key of the parameter.
     * @param value the value of the parameter.
     */
    public static void addPersistingUrlParameter(String parameter, String value){
        if (requestUrlParameters == null){
            requestUrlParameters = new HashMap<>();
        }
        requestUrlParameters.put(parameter, value);
    }

    /**
     * Removes a parameter from the list of parameters to add to the URLs to send requests to.
     *
     * @param parameter the key of the parameter.
     */
    public static void removePersistingUrlParameter(String parameter){
        if (requestUrlParameters != null){
            requestUrlParameters.remove(parameter);
        }
    }

    /**
     * Adds a header to the header list to be sent with every request. If the header already
     * exists the value gets replaced.
     *
     * @param header the header name.
     * @param value the value of the header.
     */
    public static void addPersistingHeader(String header, String value){
        if(requestHeaders == null){
            requestHeaders = new HashMap<>();
        }
        requestHeaders.put(header, value);
    }

    /**
     * Removes a header from the persistent header list.
     *
     * @param header the header to be removed.
     */
    public static void removePersistingHeader(String header){
        if (requestHeaders != null){
            requestHeaders.remove(header);
        }
    }

    /**
     * Sets a default charset used to parse the network response. TODO explain valid encodings
     *
     * @param encoding the name of the new encoding to be used.
     * @return true if the encoding was accepted, false otherwise.
     */
    public static boolean setEncoding(String encoding){
        if (Charset.availableCharsets().containsKey(encoding)){
            HttpRequests.encoding = encoding;
            return true;
        }
        return false;
    }

    /**
     * Getter for the default request timeout.
     *
     * @return the default request timeout.
     */
    static int getDefaultRequestTimeout(){
        return requestTimeout;
    }

    /**
     * Getter for the default request retry number.
     *
     * @return the default number of times a request should retry executing when it fails due
     * to a timeout.
     */
    static int getDefaultRequestRetries(){
        return requestRetries;
    }

    /**
     * Getter for the default timeout backoff.
     *
     * @return the default timeout backoff.
     */
    static float getDefaultRetryBackoff(){
        return retryBackoff;
    }

    @NonNull
    static Map<String, String> getPersistentRequestUrlParameters(){
        if (requestUrlParameters == null){
            requestUrlParameters = new HashMap<>();
        }
        return requestUrlParameters;
    }

    @NonNull
    static Map<String, String> getPersistentRequestHeaders(){
        if (requestHeaders == null){
            requestHeaders = new HashMap<>();
        }
        return requestHeaders;
    }

    /**
     * Getter for the default encoding.
     *
     * @return the default encoding.
     */
    static String getDefaultEncoding(){
        return encoding;
    }


    /**
     * Constructor. Never to be used.
     */
    private HttpRequests(){
        throw new IllegalStateException("This class cannot be instantiated.");
    }
}
