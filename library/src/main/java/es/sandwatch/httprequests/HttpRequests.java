package es.sandwatch.httprequests;


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


    //Initialization state
    private static boolean initialized = false;

    //Default request headers and url parameters
    static Map<String, String> requestUrlParameters;
    static Map<String, String> requestHeaders;

    //Retry policy values
    static int requestTimeout = DEFAULT_REQUEST_TIMEOUT;
    static int requestRetries = DEFAULT_REQUEST_RETRIES;
    static float retryBackoff = DEFAULT_RETRY_BACKOFF;

    //Encoding
    static String encoding = DEFAULT_ENCODING;



    /**
     * Initializes the library. Call in Application.onCreate().
     */
    public static void init(){
        requestUrlParameters = new HashMap<>();
        requestHeaders = new HashMap<>();

        initialized = true;
    }

    /**
     * Tells whether the library has been initialized.
     *
     * @return true if the library is initialized, false otherwise.
     */
    static boolean isInitialized(){
        return initialized;
    }

    /**
     * Checks whether the library has been initialized, if not, throws an exception.
     */
    static void checkInitialization(){
        if (!isInitialized()){
            throw new IllegalStateException("HttpRequests needs to be initialized before used.");
        }
    }

    /**
     * Sets a default request timeout.
     *
     * @param requestTimeout the default request timeout value in milliseconds.
     */
    public static void setDefaultRequestTimeout(int requestTimeout){
        checkInitialization();
        HttpRequests.requestTimeout = requestTimeout;
    }

    /**
     * Sets a default maximum retry number.
     *
     * @param requestRetries the default maximum number of retries.
     */
    public static void setDefaultRequestRetries(int requestRetries){
        checkInitialization();
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
        checkInitialization();
        HttpRequests.retryBackoff = retryBackoff;
    }

    /**
     * Sets a default charset used to parse the network response. TODO explain valid encodings
     *
     * @param encoding the name of the new encoding to be used.
     * @return true if the encoding was accepted, false otherwise.
     */
    public static boolean setEncoding(String encoding){
        checkInitialization();
        if (Charset.availableCharsets().containsKey(encoding)){
            HttpRequests.encoding = encoding;
            return true;
        }
        return false;
    }

    /**
     * Adds a header to the header list to be sent with every request. If the header already
     * exists the value gets replaced.
     *
     * @param header the header name.
     * @param value the value of the header.
     */
    public static void addPersistingHeader(String header, String value){
        checkInitialization();
        requestHeaders.put(header, value);
    }

    /**
     * Removes a header from the persistent header list.
     *
     * @param header the header to be removed.
     */
    public static void removePersistingHeader(String header){
        checkInitialization();
        requestHeaders.remove(header);
    }

    /**
     * Adds a parameter to the list of URL parameters to be sent with every request. If the
     * parameter already exists the value gets replaced.
     *
     * @param parameter the key of the parameter.
     * @param value the value of the parameter.
     */
    public static void addPersistingUrlParameter(String parameter, String value){
        checkInitialization();
        requestUrlParameters.put(parameter, value);
    }

    /**
     * Removes a parameter from the list of parameters to add to the URLs to send requests to.
     *
     * @param parameter the key of the parameter.
     */
    public static void removePersistingUrlParameter(String parameter){
        checkInitialization();
        requestUrlParameters.remove(parameter);
    }
}
