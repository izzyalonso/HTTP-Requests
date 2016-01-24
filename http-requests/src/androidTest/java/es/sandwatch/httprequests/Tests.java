package es.sandwatch.httprequests;

import android.support.test.InstrumentationRegistry;

import org.json.JSONObject;
import org.junit.Test;


/**
 * Test suite for HttpRequests.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class Tests{
    @Test(expected = IllegalStateException.class)
    public void illegalStateTest(){
        HttpRequest.request(HttpRequest.Method.GET, null, "", new JSONObject());
    }
}
