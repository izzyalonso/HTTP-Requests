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
import static org.junit.Assert.fail;


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
            fail(iax.getMessage());
            iax.printStackTrace();
        }
        catch (NoSuchFieldException nsfx){
            fail(nsfx.getMessage());
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
            Method method = HttpRequest.class.getDeclaredMethod("isInitialised");
            method.setAccessible(true);
            assertTrue((Boolean)method.invoke(null));
        }
        catch (Exception x){
            fail(x.getMessage());
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

    @Test
    public void headersTest(){
        HttpRequest.addHeader("othello", "test");
        assertTrue(HttpRequest.removeHeader("othello"));
    }

    @Test
    public void urlParametersTest(){
        HttpRequest.addUrlParameter("hamlet", "test");
        assertTrue(HttpRequest.removeUrlParameter("hamlet"));
    }

    @Test
    public void processUrlTest(){
        HttpRequest.addUrlParameter("process1", "someValue");
        HttpRequest.addUrlParameter("process2", "otherValue");
        try{
            Method method = HttpRequest.class.getDeclaredMethod("processUrl", String.class);
            method.setAccessible(true);
            String url = (String)method.invoke(null, "http://www.test.es/");
            assertTrue(url.contains("process1=someValue"));
            assertTrue(url.contains("process2=otherValue"));
            assertTrue(url.startsWith("http://www.test.es/?"));
            assertTrue(url.contains("&"));
        }
        catch (Exception x){
            fail(x.getMessage());
            x.printStackTrace();
        }
    }
}
