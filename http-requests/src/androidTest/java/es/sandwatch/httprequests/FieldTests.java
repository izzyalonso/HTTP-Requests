package es.sandwatch.httprequests;

import org.junit.Test;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;


/**
 * Test collection for HttpRequest fields.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class FieldTests{
    @Test
    public void setTimeoutTest(){
        HttpRequest.setRequestTimeout(159753);
        try{
            Field field = HttpRequest.class.getDeclaredField("sRequestTimeout");
            field.setAccessible(true);
            int timeout = (int)field.get(null);
            HttpRequest.setRequestTimeout(10*1000);
            assertEquals(timeout, 159753);
        }
        catch (Exception x){
            x.printStackTrace();
        }
    }

    @Test
    public void setRetriesTest(){
        HttpRequest.setRequestRetries(5);
        try{
            Field field = HttpRequest.class.getDeclaredField("sRequestRetries");
            field.setAccessible(true);
            int retries = (int)field.get(null);
            HttpRequest.setRequestRetries(0);
            assertEquals(retries, 5);
        }
        catch (Exception x){
            x.printStackTrace();
        }
    }

    @Test
    public void setBackoffTest(){
        HttpRequest.setRetryBackoff(2f);
        try{
            Field field = HttpRequest.class.getDeclaredField("sRetryBackoff");
            field.setAccessible(true);
            float retries = (float)field.get(null);
            HttpRequest.setRequestRetries(0);
            assertEquals(retries, 2f, 0.01);
        }
        catch (Exception x){
            x.printStackTrace();
        }
    }

    @Test
    public void encodingsTest(){
        try{
            int count = 0;
            Field field = HttpRequest.class.getField("sEncoding");
            field.setAccessible(true);
            for (String encoding:Charset.availableCharsets().keySet()){
                HttpRequest.setEncoding(encoding);
                assertEquals(encoding, field.get(null));
                count++;
            }
            assertEquals(count, Charset.availableCharsets().size());

            String illegalEncoding;
            do{
                illegalEncoding = new BigInteger(128, new Random()).toString(32);
            }
            while (!Charset.availableCharsets().containsKey(illegalEncoding));
            HttpRequest.setEncoding(illegalEncoding);
            assertNotEquals(illegalEncoding, field.get(null));
        }
        catch (Exception x){
            x.printStackTrace();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void headerMapTest(){
        HttpRequest.addHeader("header1", "test");
        HttpRequest.addHeader("header2", "true");
        try{
            Field field = HttpRequest.class.getDeclaredField("sRequestHeaders");
            field.setAccessible(true);
            Map<String, String> headerMap = (Map<String, String>)field.get(null);
            assertTrue(
                    headerMap.containsKey("header1") &&
                            headerMap.get("header1").equals("test") &&
                            headerMap.containsKey("header2") &&
                            headerMap.get("header2").equals("true")
            );
        }
        catch (Exception x){
            x.printStackTrace();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void headerMapReplacementTest(){
        HttpRequest.addHeader("success", "false");
        HttpRequest.addHeader("success", "true");
        try{
            Field field = HttpRequest.class.getDeclaredField("sRequestHeaders");
            field.setAccessible(true);
            Map<String, String> headerMap = (Map<String, String>)field.get(null);
            assertTrue(
                    headerMap.containsKey("success") &&
                            headerMap.get("success").equals("true")
            );
        }
        catch (Exception x){
            x.printStackTrace();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void headerMapRemoveTest(){
        HttpRequest.addHeader("headerToRemove", "test");
        try{
            Field field = HttpRequest.class.getDeclaredField("sRequestHeaders");
            field.setAccessible(true);
            Map<String, String> headerMap = (Map<String, String>)field.get(null);
            assertTrue(headerMap.containsKey("headerToRemove"));
            HttpRequest.removeHeader("headerToRemove");
            assertFalse(headerMap.containsKey("headerToRemove"));
        }
        catch (Exception x){
            x.printStackTrace();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void urlParameterMapTest(){
        HttpRequest.addUrlParameter("header1", "test");
        HttpRequest.addUrlParameter("header2", "true");
        try{
            Field field = HttpRequest.class.getDeclaredField("sRequestUrlParams");
            field.setAccessible(true);
            Map<String, String> urlParameterMap = (Map<String, String>)field.get(null);
            assertTrue(
                    urlParameterMap.containsKey("header1") &&
                            urlParameterMap.get("header1").equals("test") &&
                            urlParameterMap.containsKey("header2") &&
                            urlParameterMap.get("header2").equals("true")
            );
        }
        catch (Exception x){
            x.printStackTrace();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void urlParameterMapReplacementTest(){
        HttpRequest.addUrlParameter("success", "false");
        HttpRequest.addUrlParameter("success", "true");
        try{
            Field field = HttpRequest.class.getDeclaredField("sRequestUrlParams");
            field.setAccessible(true);
            Map<String, String> urlParameterMap = (Map<String, String>)field.get(null);
            assertTrue(
                    urlParameterMap.containsKey("success") &&
                            urlParameterMap.get("success").equals("true")
            );
        }
        catch (Exception x){
            x.printStackTrace();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void urlParameterMapRemoveTest(){
        HttpRequest.addUrlParameter("parameterToRemove", "test");
        try{
            Field field = HttpRequest.class.getDeclaredField("sRequestUrlParams");
            field.setAccessible(true);
            Map<String, String> urlParameterMap = (Map<String, String>)field.get(null);
            assertTrue(urlParameterMap.containsKey("parameterToRemove"));
            HttpRequest.removeUrlParameter("parameterToRemove");
            assertFalse(urlParameterMap.containsKey("parameterToRemove"));
        }
        catch (Exception x){
            x.printStackTrace();
        }
    }
}
