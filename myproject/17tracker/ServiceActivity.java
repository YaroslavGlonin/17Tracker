package com.seventeentracker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import en.seventeentracker.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

public class ServiceActivity extends Service {

	private static final String TAG = "17tracker";
	public int time_delay;
	public int id_main_notif;
	Cursor cursor;
	 static boolean isrunable;
	 String name,track,digit20,digit13;
	 ListView listView;
	 String tracking,named,states;
	 private SQLiteAdapter mySQLiteAdapter;
	 int idt;
    private Timer timer = new Timer();
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	Handler handler=new Handler();  
	int count =0;
	@Override
	public void onCreate() {
		id_main_notif=15001;
		//Toast.makeText(this, "Congrats! MyService Created", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate");
		mySQLiteAdapter = new SQLiteAdapter(this);
        mySQLiteAdapter.openToWrite();
        cursor = mySQLiteAdapter.queueAll();
        getPrefs();
        //time_delay=100;
        if(autoupdate)
        {
	        Notification_background("17Tracker is running","Touch for more information or to stop app.",id_main_notif);
			//handler.post(updateTextRunnable);
	        handler.postDelayed(updateTextRunnable, time_delay);
        }
	}
	
	Runnable updateTextRunnable=new Runnable(){ 
		@Override
		  public void run() {  
		      count++;
		     // time_delay=1000;
		     // getPrefs();
		      time_delay=60000;
		      Log.d("Notification_background", String.valueOf(time_delay));
		      //Notification(" new status","",count);
		     // Runrefresh();
		      handler.postDelayed(this, time_delay);//time_delay);
		     }  
		 }; 
	public static boolean servicerun()
	{
		return isrunable;
	}
	@Override
	public void onStart(Intent intent, int startId) {
		getPrefs();
		isrunable=true;
		//Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onStart");	
	}
	
	@Override
	public void onDestroy() {
		handler.removeCallbacks(updateTextRunnable);
		stopForeground(true);
		isrunable=false;
		Log.d(TAG, "onDestroy");
	}
	AsyncHttpClient client = new AsyncHttpClient();
	public void Runrefresh()
	{
    	if(!isOnline())
        {
    		//Notification("17Tracker update warning","No Internet connection is available",idt);
        }
    	else
    	{
        	if (cursor .moveToFirst()) {
                    idt = cursor.getInt(cursor.getColumnIndex(SQLiteAdapter.KEY_ID));
                    named = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_CONTENT1));
                    states = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_CONTENT3));
                    tracking = cursor.getString(cursor
                            .getColumnIndex(SQLiteAdapter.KEY_CONTENT2));
                    try{
                    tracking=tracking.replace("\"", "");
                    tracking=tracking.replace(" ", "");
                    
                    digit20 = generateInt(20);
                	digit13=generateInt(13);
            		/*new GetRequstServ().execute("http://www.17track.net/r/handlertrack.ashx?"+
                    		"callback=jQuery"+digit20+"_"+digit13+"&"+
                            "num="+tracking+"&"+
                    		"pt=0&cm=0&cc=0&_="+digit13);*/
                	client.setTimeout(30000);
        			client.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:25.0) Gecko/20100101 Firefox/25.0");
        			client.get("http://www.17track.net/r/handlertrack.ashx?"+
                    		"callback=jQuery"+digit20+"_"+digit13+"&"+
                            "num="+tracking+"&"+
                    		"pt=0&cm=0&cc=0&_="+digit13, new AsyncHttpResponseHandler() {

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							// TODO Auto-generated method stub
							String resurs;
							try {
								resurs = new String(arg2, "UTF-8");
								parse (resurs);
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
        				
        			});
                    }
                    catch(Exception Ex)
                    {
                    	mySQLiteAdapter.update_stat(idt,"Your has a tracking number error or had not yet processed the postal service!");
            	    	mySQLiteAdapter.update_day(idt, "-");
                    }
                    	//Toast.makeText(getBaseContext(),"Your has a tracking number error or had not yet processed the postal service!", Toast.LENGTH_SHORT).show();
            }
        	else
        	{
        		Notification("17Tracker update warning","Sorry, to upgrade, you must add at least one track number",idt);
        		//Toast.makeText(getBaseContext(),"Sorry, to upgrade, you must add at least one track number", Toast.LENGTH_SHORT).show();
        	}
    	}
	}
	 private String generateInt(int digits) {
		    StringBuilder str = new StringBuilder();
		    Random random = new Random();
		    for(int i = 0; i < digits; i++) {
		        str.append(random.nextInt(10));
		    }
		    return str.toString();
		}
	 public boolean isOnline() {
		    ConnectivityManager cm =
		        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo netInfo = cm.getActiveNetworkInfo();
		    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
		        return true;
		    }
		    return false;
		}
		private void Notification(String notificationTitle, String notificationMessage,int id) {
		    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		    android.app.Notification notification = new android.app.Notification(R.drawable.ic_launcher,"",
		    System.currentTimeMillis());

		    Intent notificationIntent = new Intent(this, MainActivity.class);
		    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		    notification.setLatestEventInfo(ServiceActivity.this, notificationTitle, notificationMessage, pendingIntent);
		    notificationManager.notify(12000+id, notification);
		    try {
		        Uri notification1 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification1);
		        r.play();
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}
		private void Notification_background(String notificationTitle, String notificationMessage,int id) {
		    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		    android.app.Notification notification = new android.app.Notification(R.drawable.ic_launcher,"",
		    System.currentTimeMillis());

		    Intent notificationIntent = new Intent(this, MainActivity.class);
		    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		    notification.setLatestEventInfo(ServiceActivity.this, notificationTitle, notificationMessage, pendingIntent);
		    startForeground(12000+id, notification);
		}
		/*@Override
		public void onTaskComplete(String result) {
			Log.d("---------->","onTaskComplete");
			if (result!=null) parse(result);
			else Log.d("onTaskComplete", "Exception");
		}*/
	    public Boolean checkstring(String str, int pos)
	    {
	    	if(str.length()<=pos) return false;
	    	else return true;
	    }
	    public void parse (String result)
		{
	    	String sjons= "";
	    	String rezult=null;
	    	try{
	    		sjons=result;
	    		sjons=sjons.replace("jQuery"+digit20+"_"+digit13, "");
	    		sjons=sjons.replace("(", "");
	    		JSONObject reader = new JSONObject(sjons);
	    		JSONObject obj=null;
	    		JSONObject dat  = reader.getJSONObject("dat");
	    		String dest = dat.getString("z2");
	    		String destx = dat.getString("z1");    	    
	    	    if (dest.length()>5)
	    	    {
	        		//Log.d("y---------->"+dest.length(),dest);
	        	    JSONArray arrayx=new JSONArray(dest);
	        	       for(int i = 0; i < arrayx.length(); ++i) 
	                   {
	                      obj = arrayx.getJSONObject(i);
	                      //mySQLiteAdapterData.insert(obj.getString("b"), obj.getString("a"));
	                      //updateList();
		   	               if(i==0) 
		            	  	{
			             	  try
			             	  {
				             	 Log.d("---------->",idt+"   "+obj.getString("c")+" "+obj.getString("z"));
				             	rezult=obj.getString("a")+" "+ obj.getString("c")+" "+obj.getString("z");
				             	if(states.equals(rezult))
				             	{
				             	 mySQLiteAdapter.update_stat(idt,rezult);
				             	}
				             	else
				             	{
				             		mySQLiteAdapter.update_stat(idt,rezult);
				             		if(notifi)Notification(named + " new status",rezult,idt);
				             		else Toast.makeText(getBaseContext(),named+" new status", Toast.LENGTH_SHORT).show();
				             	}
			             	  }
			             	  catch(Exception Ex) {}//mySQLiteAdapter.update_stat(idt,"Internal server error");mySQLiteAdapter.update_day(idt, "-");}
		            	  }
	                   }
	        	       try 
	     	        	{
		               SimpleDateFormat dfDate  = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		      	        String mydate = obj.getString("a");
		      	        java.util.Date d = null;
		      	        java.util.Date d1 = null;
		      	        Calendar cal = Calendar.getInstance();
	     	              d = dfDate.parse(mydate);
	     	              d1 = dfDate.parse(dfDate.format(cal.getTime()));//Returns 15/10/2012
		      	         int diffInDays = (int) ((d.getTime() - d1.getTime())/ (1000 * 60 * 60 * 24));
		      	         mySQLiteAdapter.update_day(idt, String.valueOf(diffInDays).replaceAll("-", ""));
	     	             } 
	     	        catch (java.text.ParseException e) {mySQLiteAdapter.update_day(idt, "-");}
	    	    }
	    	    else if (destx.length()>5)
	    	    {
	    	    	JSONArray array=new JSONArray(destx);
		            for(int i = 0; i < array.length(); ++i) 
		            {
		               obj = array.getJSONObject(i);
		               if(i==0) 
	             	  	{
		             	  try
		             	  {
			             	 Log.d("---------->",idt+"   "+obj.getString("c")+" "+obj.getString("z"));
			             	rezult=obj.getString("a")+" "+ obj.getString("c")+" "+obj.getString("z");
			             	if(states.equals(rezult))
			             	{
			             	 mySQLiteAdapter.update_stat(idt,rezult);
			             	}
			             	else
			             	{
			             		mySQLiteAdapter.update_stat(idt,rezult);
			             		if(notifi)Notification(named + " new status",rezult,idt);
			             		else Toast.makeText(getBaseContext(),named+" new status", Toast.LENGTH_SHORT).show();
			             	}
		             	  }
		             	  catch(Exception Ex)
		             	  {}// {mySQLiteAdapter.update_stat(idt,"Internal server error");mySQLiteAdapter.update_day(idt, "-");}
	             	  }
		            }
		            try 
	  	        	{
		            SimpleDateFormat dfDate  = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	      	        String mydate = obj.getString("a");
	      	        java.util.Date d = null;
	      	        java.util.Date d1 = null;
	      	        Calendar cal = Calendar.getInstance();
	  	              d = dfDate.parse(mydate);
	  	              d1 = dfDate.parse(dfDate.format(cal.getTime()));//Returns 15/10/2012
		      	         int diffInDays = (int) ((d.getTime() - d1.getTime())/ (1000 * 60 * 60 * 24));
	      	         mySQLiteAdapter.update_day(idt, String.valueOf(diffInDays).replaceAll("-", ""));
	  	             } 
	  	        catch (java.text.ParseException e) {mySQLiteAdapter.update_day(idt, "-");}
	    	    }
	    	    else
	    	    {
	    	    	
	    	    	mySQLiteAdapter.update_stat(idt,"Your has a tracking number error or had not yet processed the postal service!");
	    	    	mySQLiteAdapter.update_day(idt, "-");
	    	    }
	    	}
	    	catch(Exception ex){}//{mySQLiteAdapter.update_stat(idt,"Internal server error");mySQLiteAdapter.update_day(idt, "-");}
	    	finally
	    	{
	    		cursor.moveToNext();
	        	if (cursor.isAfterLast() == false) {
	        		
	                    idt = cursor.getInt(cursor.getColumnIndex(SQLiteAdapter.KEY_ID));
	                    named = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_CONTENT1));
	                    states = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_CONTENT3));
	                    tracking = cursor.getString(cursor
	                            .getColumnIndex(SQLiteAdapter.KEY_CONTENT2));
	                    try
	                    {
	                    	tracking=tracking.replace("\"", "");
	                        tracking=tracking.replace(" ", "");
	                        digit20 = generateInt(20);
	                    	digit13=generateInt(13);
	                		/*new GetRequstServ().execute("http://www.17track.net/r/handlertrack.ashx?"+
	                        		"callback=jQuery"+digit20+"_"+digit13+"&"+
	                                "num="+tracking+"&"+
	                        		"pt=0&cm=0&cc=0&_="+digit13);*/
	                    	client.setTimeout(30000);
	            			client.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:25.0) Gecko/20100101 Firefox/25.0");
	            			client.get("http://www.17track.net/r/handlertrack.ashx?"+
	                        		"callback=jQuery"+digit20+"_"+digit13+"&"+
	                                "num="+tracking+"&"+
	                        		"pt=0&cm=0&cc=0&_="+digit13, new AsyncHttpResponseHandler() {

	    						@Override
	    						public void onFailure(int arg0, Header[] arg1,
	    								byte[] arg2, Throwable arg3) {
	    							// TODO Auto-generated method stub
	    							
	    						}

	    						@Override
	    						public void onSuccess(int arg0, Header[] arg1,
	    								byte[] arg2) {
	    							// TODO Auto-generated method stub
	    							String resurs;
	    							try {
	    								resurs = new String(arg2, "UTF-8");
	    								parse (resurs);
	    							} catch (UnsupportedEncodingException e) {
	    								// TODO Auto-generated catch block
	    								e.printStackTrace();
	    							}
	    							
	    						}
	            				
	            			});
	                    }
	                    catch(Exception Ex){Toast.makeText(getBaseContext(),"Your has a tracking number error or had not yet processed the postal service!", Toast.LENGTH_SHORT).show();}
	                    
	            }
	        	else 
	        	{
	        		updateList();
	        	}
	    	}
		}
	    @SuppressWarnings("deprecation")
		private void updateList()
	    {
	    	  cursor.requery();
	    }
	    boolean notifi, autoupdate;
	    String ListPreference;

	    private void getPrefs() {
	    	try
            {
	            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	    		//SharedPreferences prefs = this.getSharedPreferences("Pref name", Context.MODE_PRIVATE);
	    		//Context ctx = getApplicationContext();
	    		//SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
	            notifi = prefs.getBoolean("checkboxPref", true);
	            autoupdate = prefs.getBoolean("checkboxPref1", true);
	            
	            ListPreference = prefs.getString("listPref", "nr1");
	            time_delay= Integer.parseInt(ListPreference)*60*60*1000;
	        }
	        catch(Exception Ex){time_delay=4*60*60*1000;notifi =true;Log.d("Timee", "Error");}
	            Log.d("11111111= ", String.valueOf(time_delay));
	            
	    }
	    private MediaPlayer mMediaPlayer;
	    private void playSound(Context context, Uri alert) {
	            mMediaPlayer = new MediaPlayer();
	            try {
	                mMediaPlayer.setDataSource(context, alert);
	                final AudioManager audioManager = (AudioManager) context
	                        .getSystemService(Context.AUDIO_SERVICE);
	                if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
	                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
	                    mMediaPlayer.prepare();
	                    mMediaPlayer.start();
	                }
	            } catch (IOException e) {
	                System.out.println("OOPS");
	            }
	        }
	    public class GetRequstServ extends AsyncTask<String, String, String> {	    	
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
	        	Log.d("---------->","onTaskComplete");
				if (result!=null) 
					{ parse(result);}
				
	            super.onPostExecute(result);            
	        }

	    }

}
