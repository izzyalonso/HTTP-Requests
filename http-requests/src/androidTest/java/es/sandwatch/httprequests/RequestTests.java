package es.sandwatch.httprequests;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.test.InstrumentationRegistry;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 * Test collection for actual requests.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class RequestTests{
    @Before
    public void setUp(){
        HttpRequest.init(InstrumentationRegistry.getContext());
        HttpRequest.addHeader("contentType", "application/json");
    }

    private void setWifiEnabled(Context context, boolean state){
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        //The permission required to access this method is declared in the 'androidTest' manifest
        wifiManager.setWifiEnabled(state);
    }

    private boolean isWifiConnected(Context context){
        WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return wifi.isWifiEnabled() && networkInfo.isConnected();
    }

    //@Test()
    public void networkErrorTest(){
        setWifiEnabled(InstrumentationRegistry.getContext(), false);

        final CountDownLatch signal = new CountDownLatch(1);

        HttpRequest.request(HttpRequest.Method.GET, new HttpRequest.RequestCallback(){
            @Override
            public void onRequestComplete(int requestCode, String result){
                fail("Request shouldn't have completed");
                signal.countDown();
            }

            @Override
            public void onRequestFailed(int requestCode, HttpRequestError error){
                assertTrue(error.isNetworkError());
                signal.countDown();
            }
        }, "http://www.google.es", new JSONObject());

        try{
            signal.await();
        }
        catch (InterruptedException ix){
            fail("No interrupt expected");
            ix.printStackTrace();
        }

        setWifiEnabled(InstrumentationRegistry.getContext(), true);
        while (!isWifiConnected(InstrumentationRegistry.getContext())){
            //Do nothing here, just wait until the wifi is back up.
        }
    }

    @Test()
    public void getTest(){
        final CountDownLatch signal = new CountDownLatch(1);
        final String text = new BigInteger(128, new Random()).toString(32);
        HttpRequest.addUrlParameter("text", text);
        HttpRequest.get(new HttpRequest.RequestCallback(){
            @Override
            public void onRequestComplete(int requestCode, String result){
                assertEquals("GET /api/ Key 'text' with value '" + text + "'", result);
                signal.countDown();
            }

            @Override
            public void onRequestFailed(int requestCode, HttpRequestError error){
                fail("request shouldn't have failed");
            }
        }, "http://http-requests.sandwatch.es/api/");

        try{
            signal.await();
        }
        catch (InterruptedException ix){
            fail("No interrupt expected");
            ix.printStackTrace();
        }

        HttpRequest.removeHeader("text");
    }

    @Test()
    public void postTest(){
        final CountDownLatch signal = new CountDownLatch(1);
        final String text = new BigInteger(128, new Random()).toString(32);
        JSONObject postBody = new JSONObject();
        try{
            postBody.put("text", text);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }

        HttpRequest.post(new HttpRequest.RequestCallback(){
            @Override
            public void onRequestComplete(int requestCode, String result){
                assertEquals("POST /api/ text = " + text, result);
                signal.countDown();
            }

            @Override
            public void onRequestFailed(int requestCode, HttpRequestError error){
                fail(error.getMessage());
            }
        }, "http://http-requests.sandwatch.es/api/", postBody);

        try{
            signal.await();
        }
        catch (InterruptedException ix){
            fail("No interrupt expected");
            ix.printStackTrace();
        }
    }
}
