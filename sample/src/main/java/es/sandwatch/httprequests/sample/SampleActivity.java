package es.sandwatch.httprequests.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


/**
 * Sample activity for HTTP-Requests.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class SampleActivity
        extends AppCompatActivity
        implements View.OnClickListener, HttpRequest.RequestCallback{

    private static final String GET_URL = "http://http-requests.sandwatch.es/api/";


    private EditText mInput;
    private Button mSend;
    private ProgressBar mProgress;
    private TextView mOutput;

    private int mRequestCode;
    private boolean mRequestInProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        //Grab UI components
        mInput = (EditText)findViewById(R.id.sample_input);
        mSend = (Button)findViewById(R.id.sample_button);
        mProgress = (ProgressBar)findViewById(R.id.sample_progress);
        mOutput = (TextView)findViewById(R.id.sample_output);

        //Listener for the button
        mSend.setOnClickListener(this);

        //Init flags
        mRequestCode = -1;
        mRequestInProgress = false;
    }

    @Override
    public void onClick(View view){
        if (view.getId() == R.id.sample_button){
            if (!mRequestInProgress){
                String input = mInput.getText().toString().trim();
                mRequestCode = HttpRequest.get(this, GET_URL + "?text=" + input);
                mProgress.setVisibility(View.VISIBLE);
                mSend.setText(R.string.button_cancel);
                mOutput.setText("");
            }
            else{
                HttpRequest.cancel(mRequestCode);
                mRequestCode = -1;
                mProgress.setVisibility(View.INVISIBLE);
                mSend.setText(R.string.button_send);
            }
            mRequestInProgress = !mRequestInProgress;
        }
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mRequestCode){
            mOutput.setText(result);
            mProgress.setVisibility(View.INVISIBLE);
            mSend.setText(R.string.button_send);
            mRequestInProgress = false;
        }
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){
        if (requestCode == mRequestCode){
            mOutput.setText(error.toString());
            mProgress.setVisibility(View.INVISIBLE);
            mSend.setText(R.string.button_send);
            mRequestInProgress = false;
        }
    }
}
