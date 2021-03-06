package com.seventeentracker;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
public class GetRequstMain extends AsyncTask<String, String, String> {
	
	private Activity activity;
	private ProgressDialog dialog;
	private AsyncTaskCompleteListener callback;

	public GetRequstMain(Activity act) {
		this.activity = act;
		this.callback = (AsyncTaskCompleteListener)act;
	}
	
    @Override
    protected String doInBackground(String... uri) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(uri[0]);
        	// Depends on your web service
        	httpget.setHeader("Content-type", "application/json");
        HttpResponse response;
        String responseString = null;
        try {
            response = httpclient.execute(httpget);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
            } else {
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }
        return responseString;
    }
    
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
		callback.onTaskComplete(result);
        
    }

}
