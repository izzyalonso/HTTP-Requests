package es.sandwatch.httprequests;

import android.support.annotation.NonNull;

import com.android.volley.Request;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


/**
 * Executes a number of requests sequentially.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public final class HttpRequestQueue implements HttpRequest.RequestCallback{
    private Queue<RequestModel> mQueue;
    private List<String> mResults;
    private RequestQueueCallback mCallback;

    private int mRequestCode;
    private boolean mExecuted;
    private int mCurrentRequest;


    /**
     * Constructor.
     *
     * @param callback the callback object.
     */
    public HttpRequestQueue(@NonNull RequestQueueCallback callback){
        mQueue = new LinkedList<>();
        mResults = new ArrayList<>();
        mCallback = callback;
        mExecuted = false;
        mCurrentRequest = 0;
    }

    public void addGet(@NonNull String url){
        if (!mExecuted){
            mQueue.add(new RequestModel(Request.Method.GET, url));
        }
    }

    public void addGet(@NonNull String url, int timeout){
        if (!mExecuted){
            mQueue.add(new RequestModel(Request.Method.GET, url, timeout));
        }
    }

    public void addPost(@NonNull String url, @NonNull JSONObject body){
        if (!mExecuted){
            mQueue.add(new RequestModel(Request.Method.POST, url, body));
        }
    }

    public void addPost(@NonNull String url, @NonNull JSONObject body, int timeout){
        if (!mExecuted){
            mQueue.add(new RequestModel(Request.Method.POST, url, body, timeout));
        }
    }

    public void addPut(@NonNull String url, @NonNull JSONObject body){
        if (!mExecuted){
            mQueue.add(new RequestModel(Request.Method.PUT, url, body));
        }
    }

    public void addPut(@NonNull String url, @NonNull JSONObject body, int timeout){
        if (!mExecuted){
            mQueue.add(new RequestModel(Request.Method.PUT, url, body, timeout));
        }
    }

    public void addDelete(@NonNull String url){
        if (!mExecuted){
            mQueue.add(new RequestModel(Request.Method.POST, url));
        }
    }

    public void addDelete(@NonNull String url, int timeout){
        if (!mExecuted){
            mQueue.add(new RequestModel(Request.Method.POST, url, timeout));
        }
    }

    public int execute(){
        if (!mExecuted){
            mExecuted = true;
            serveNextRequest();
            mRequestCode = RequestCodeGenerator.generate();
        }
        return mRequestCode;
    }

    private void serveNextRequest(){
        if (mQueue.isEmpty()){
            mCallback.onQueueCompleted(mRequestCode, mResults);
        }
        else{
            RequestModel request = mQueue.remove();
            if (request.mTimeout == -1){
                HttpRequest.request(request.mMethod, this, request.mUrl, request.mBody);
            }
            else{
                HttpRequest.request(request.mMethod, this, request.mUrl, request.mBody,
                        request.mTimeout);
            }
        }
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        mCurrentRequest++;
        mResults.add(result);
        serveNextRequest();
    }

    @Override
    public void onRequestFailed(int requestCode, String message){
        mCallback.onQueueRequestFailed(mRequestCode, mCurrentRequest, message);
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


    public interface RequestQueueCallback{
        void onQueueCompleted(int requestCode, List<String> results);
        void onQueueRequestFailed(int requestCode, int which, String message);
    }
}
