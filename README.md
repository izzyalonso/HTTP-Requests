# HTTP-Requests

An Android library to perform HTTP requests in a simple fashion.


### Importing the library

```groovy
compile 'es.sandwatch:http-requests:1.0.0'
```

### Using the library

##### Initialization

In you Application class' onCreate() method, include this line to initialize the library:

```java
HttpRequest.init(getApplicationContext());
```

Additionaly, you can set permanent headers and url parameters:

```java
HttpRequest.addHeader("Authorization", "header content");
HttpRequest.addUrlParameter("app_version", "1.0.0");
```

These parameters and headers will be sent with every request until you decide to remove them:

```java
HttpRequest.removeHeader("Authorization");
HttpRequest.removeUrlParameter("app_version");
```

By default, the request response is delivered in UTF-8 format, but you can change this behavior using the following method:

```java
HttpRequest.setEncoding("A valid encoding name");
```

Finally, some of the parameters you can tweak are the request timeout, the number of retries, and the retry backoff.

```java
HttpRequest.setRequestTimeout(20000);
HttpRequest.setRequestRetries(4);
HttpRequest.setRequestBackoff(2);
```

The default values are 10 seconds for the request timeout, no retries, and a backoff of 1.5 of the previous attempt's timeout.


##### Making requests

Making requests with this library is simple. Once you initialized the HttpRequest class, you can use the get(), post(), put(), or delete() methods to make a request. The first argument all the methods is an instance of the HttpRequest.RequestCallback interface. Except for the methods associated with GET, this parameter is optional, since you may not care about the server response, if you don't wish to provide a callback pass null. The second parameter of these functions is a URL, which is mandatory. The third parameter is, in the case of the methods associated with POST and PUT, the request body, passed as a JSONObject.

When calling any of the request-performing methods of the class, a request code will be issued. This request code will sere you to identify particular requests later on, when the callback is called, if you are making your particular activity, fragment, adapter, or else implement HttpRequests.RequestCallback. Here is a sample activity that performs four different requests:

```java
public class RequestActivity extends AppCompatActivity implements HttpRequest.RequestCallback{
    private int mGetRequest;
    private int mPostRequest;
    private int mPutRequest;
    private int mDeleteRequest;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        
        //Set layout, grab UI components, etc... skipped in this example
        
        JSONObject postBody = new JSONObject();
        JSONObject putBody = new JSONObject();
        //Add parameters to the json objects
        
        //Make the requests
        mGetRequest = HttpRequest.get(this, "https://...");
        mPostRequest = HttpRequest.post(this, "https://...", postBody);
        mPutRequest = HttpRequest.put(this, "https://...", putBody);
        mDeleteRequest = HttpRequest.delete(this, "https://...");
    }
    
    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetRequest){
            ...
        }
        else if (requestCode == mPostRequest){
            ...
        }
        else if (requestCode == mPutRequest){
            ...
        }
        else if (requestCode == mDeleteRequest){
            ...
        }
    }
    
    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){
        if (requestCode == mGetRequest){
            ...
        }
        else if (requestCode == mPostRequest){
            ...
        }
        else if (requestCode == mPutRequest){
            ...
        }
        else if (requestCode == mDeleteRequest){
            ...
        }
    }
}
```
