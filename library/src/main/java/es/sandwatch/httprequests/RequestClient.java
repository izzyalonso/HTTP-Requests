package es.sandwatch.httprequests;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;


/**
 * Class from where all requests are fired.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class RequestClient{
    private Context context;
    private RequestCallback callback;


    /**
     * Constructor.
     *
     * @param context a reference to the context.
     * @param callback the default callback.
     */
    public RequestClient(@NonNull Context context, @NonNull RequestCallback callback){
        this.context = context;
        this.callback = callback;
    }


    public HttpRequest get(@NonNull String endpoint){
        return null;
    }

    public HttpRequest post(@NonNull String endpoint, @NonNull String body){
        return null;
    }

    public HttpRequest post(@NonNull String endpoint, @NonNull RequestBodySender sender){
        return null;
    }

    public HttpRequest put(@NonNull String endpoint, @NonNull String body){
        return null;
    }

    public HttpRequest put(@NonNull String endpoint, @NonNull RequestBodySender sender){
        return null;
    }

    public HttpRequest delete(@NonNull String endpoint){
        return null;
    }


    private class Worker extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params){
            return null;
        }
    }
}
