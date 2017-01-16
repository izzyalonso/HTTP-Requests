package es.sandwatch.httprequests.sample;

import android.app.Application;

import es.sandwatch.httprequests.HttpRequest;


/**
 * Sample application class for HTTP-Requests.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class SampleApplication extends Application{
    @Override
    public void onCreate(){
        super.onCreate();
        //Always initialize the class in the application
        HttpRequest.init(this);
        //Add as many headers and url parameters as you desire, they will be sent
        //  in every request untill you remove them
        /*HttpRequest.addHeader("version", "1");
        HttpRequest.addUrlParameter("test", "true");*/
    }
}
