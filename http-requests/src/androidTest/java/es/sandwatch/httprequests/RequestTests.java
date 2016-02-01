package es.sandwatch.httprequests;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.test.InstrumentationRegistry;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

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
    }

    public void setMobileDataEnabled(Context context, boolean enabled){
        try{
            final ConnectivityManager conman = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final Class conmanClass = Class.forName(conman.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.class);
            setMobileDataEnabledMethod.setAccessible(true);
            setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
        }
        catch (Exception x){
            fail(x.getMessage());
            x.printStackTrace();
        }
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

    @Test()
    public void networkErrorTest(){
        setWifiEnabled(InstrumentationRegistry.getContext(), false);
        //setMobileDataEnabled(InstrumentationRegistry.getContext(), false);

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

        }
        //setMobileDataEnabled(InstrumentationRegistry.getContext(), true);
    }

    @Test()
    public void getTest(){
        final CountDownLatch signal = new CountDownLatch(1);
        HttpRequest.get(new HttpRequest.RequestCallback(){
            @Override
            public void onRequestComplete(int requestCode, String result){
                assertTrue(true);
                signal.countDown();
            }

            @Override
            public void onRequestFailed(int requestCode, HttpRequestError error){
                fail("request shoudln't failed");
            }
        }, "http://http-requests.sandwatch.es/api/?text=text");

        try{
            signal.await();
        }
        catch (InterruptedException ix){
            fail("No interrupt expected");
            ix.printStackTrace();
        }
    }
}
