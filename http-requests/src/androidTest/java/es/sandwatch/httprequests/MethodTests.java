package es.sandwatch.httprequests;

import android.support.test.InstrumentationRegistry;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Test collection for HttpRequests methods.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class MethodTests{
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
        catch (IllegalAccessException iax){
            iax.printStackTrace();
        }
        catch (NoSuchFieldException nsfx){
            System.out.println(nsfx.getMessage());
            nsfx.printStackTrace();
        }
    }

    @Test
    public void initialisationTest(){
        try{
            Field field = HttpRequest.class.getDeclaredField("sRequestQueue");
            field.setAccessible(true);
            field.set(null, null);
            HttpRequest.init(InstrumentationRegistry.getContext());
            Method method = HttpRequest.class.getMethod("isInitialised");
            assertTrue((Boolean)method.invoke(null));
        }
        catch (Exception x){
            x.printStackTrace();
        }
    }

    @Test
    public void validRequestCodeTest(){
        int requestCode = HttpRequest.request(HttpRequest.Method.GET, null, "", new JSONObject());
        assertNotEquals(requestCode, -1);
    }

    @Test
    public void validRequestCodesTest(){
        int requestCode1 = HttpRequest.request(HttpRequest.Method.GET, null, "", new JSONObject());
        int requestCode2 = HttpRequest.request(HttpRequest.Method.GET, null, "", new JSONObject());
        assertEquals(requestCode1, requestCode2 - 1);
    }
}
