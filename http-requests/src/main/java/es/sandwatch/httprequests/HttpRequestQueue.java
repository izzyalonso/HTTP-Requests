package es.sandwatch.httprequests;

import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Queue;


/**
 * Created by isma on 12/23/15.
 */
public class HttpRequestQueue{
    private Queue<RequestModel> mQueue;


    public HttpRequestQueue(){
        mQueue = new LinkedList<>();
    }

    public int execute(){
        return 0;
    }


    private class RequestModel{
        private final int mMethod;
        private final String mUrl;
        private final JSONObject mBody;
        private final int mTimeout;


        private RequestModel(int method, String url){
            this(method, url, new JSONObject(), -1);
        }

        private RequestModel(int method, String url, int timeout){
            this(method, url, new JSONObject(), timeout);
        }

        private RequestModel(int method, String url, JSONObject body){
            this(method, url, body, -1);
        }

        private RequestModel(int method, String url, JSONObject body, int timeout){
            mMethod = method;
            mUrl = url;
            mBody = body;
            mTimeout = timeout;
        }
    }
}
