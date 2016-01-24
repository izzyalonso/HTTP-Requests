package es.sandwatch.httprequests;

import android.support.test.InstrumentationRegistry;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.assertNotEquals;


/**
 * Test suite for HttpRequests.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class Tests{
    @Before
    public void setUp(){
        HttpRequest.init(InstrumentationRegistry.getContext());
    }

    @Test(expected = IllegalStateException.class)
    public void illegalStateTest(){
        try{
            Field field = HttpRequest.class.getDeclaredField("sRequestQueue");
            field.setAccessible(true);
            field.set(null, null);
            HttpRequest.request(HttpRequest.Method.GET, null, "", new JSONObject());
        }
        catch (NoSuchFieldException nsfx){
            nsfx.printStackTrace();
        }
        catch (IllegalAccessException iax){
            iax.printStackTrace();
        }
    }

    @Test
    public void validRequestCodeTest(){
        int requestCode = HttpRequest.request(HttpRequest.Method.GET, null, "", new JSONObject());
        assertNotEquals(requestCode, -1);
    }
}
