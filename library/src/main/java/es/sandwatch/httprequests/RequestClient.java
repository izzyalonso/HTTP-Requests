package es.sandwatch.httprequests;

import android.content.Context;
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


    void get(){

    }

    void post(){

    }

    void put(){

    }

    void delete(){

    }
}
