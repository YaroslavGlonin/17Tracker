package com.seventeentracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupManager;
import android.app.backup.SharedPreferencesBackupHelper;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.widget.AdapterView.OnItemLongClickListener;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.ActionMode.Callback;

import en.seventeentracker.R;

import com.google.android.gms.ads.*;
import com.seventeentracker.SQLiteAdapter;
import com.seventeentracker.GetRequstMain;
import com.google.analytics.tracking.android.EasyTracker;

public class MainActivity extends SherlockActivity implements AsyncTaskCompleteListener{
	EditText etPassword,etUsername;
	 private SQLiteAdapter mySQLiteAdapter;
	 SimpleCursorAdapter cursorAdapter;
	 Cursor cursor;
	 boolean refreshstart;
	 String name,track,digit20,digit13;
	 ListView listView;
	 String tracking,named,states;
	 int idt;
	 private AdView adView;
	 com.actionbarsherlock.view.ActionMode mMode;
	 com.actionbarsherlock.view.ActionMode.Callback mCallback;
	 
    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_PROGRESS);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
        //-----------listitem------------
        //---------------------
     // Создание экземпляра adView.
        adView = new AdView(this);
        adView.setAdUnitId("ca-app-pub-2638991009658616/6464067488");
        adView.setAdSize(AdSize.BANNER);

        // Поиск разметки LinearLayout (предполагается, что ей был присвоен
        // атрибут android:id="@+id/mainLayout").
        LinearLayout layout = (LinearLayout)findViewById(R.id.mainLayout);

        // Добавление в разметку экземпляра adView.
        layout.addView(adView);

        // Инициирование общего запроса.
        AdRequest adRequest = new AdRequest.Builder().build();

        // Загрузка adView с объявлением.
        adView.loadAd(adRequest);        		
        //------------------
        setSupportProgressBarIndeterminateVisibility(false);
        refreshstart=false;
        listView = (ListView) findViewById(R.id.listView1);
        mySQLiteAdapter = new SQLiteAdapter(this);
        mySQLiteAdapter.openToWrite();
        cursor = mySQLiteAdapter.queueAll();
        String[] from = new String[]{SQLiteAdapter.KEY_ID, SQLiteAdapter.KEY_CONTENT1, SQLiteAdapter.KEY_CONTENT2, SQLiteAdapter.KEY_CONTENT3, SQLiteAdapter.KEY_CONTENT4};
        int[] to = new int[]{R.id.id, R.id.name, R.id.track, R.id.dest, R.id.days};
        cursorAdapter =new CustomCursorAdapter(this, R.layout.listview_row, cursor, from, to);
        listView.setAdapter(cursorAdapter);
        listView.setAdapter(cursorAdapter);
        //registerForContextMenu(listView);
        mCallback = new Callback() {
            /** Invoked whenever the action mode is shown. This is invoked immediately after onCreateActionMode */
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }
            /** Called when user exits action mode */
            @Override
            public void onDestroyActionMode(ActionMode mode) {
               mMode = null;
            }
            /** This is called when the action mode is created. This is called by startActionMode() */
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.setTitle("Options");
                getSupportMenuInflater().inflate(R.menu.context_menu, menu);
                return true;
            }
            /** This is called when an item in the context menu is selected */
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action2:
                        //Toast.makeText(getBaseContext(),name+"     "+track, Toast.LENGTH_LONG).show();
                    	final int item_id = cursor.getInt(cursor.getColumnIndex(SQLiteAdapter.KEY_ID));
           	            String item_content1 = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_CONTENT1));
           	            String item_content2 = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_CONTENT2));
           	            //mode.setTitle(item_content1);
           	          	LayoutInflater inflater = getLayoutInflater();
                   		View alertLayout = inflater.inflate(R.layout.layout_custom_dialog_delete, null);
                   		etUsername = (EditText) alertLayout.findViewById(R.id.et_name);
                   		etPassword = (EditText) alertLayout.findViewById(R.id.et_track);
                   		etUsername.setText(item_content1);
                   		etPassword.setText(item_content2);
                   		AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                   		alert.setTitle("Delete item");
                   		alert.setIcon(R.drawable.delete);
                   		alert.setView(alertLayout);
                   		alert.setCancelable(false);
                   		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   			@Override
                   			public void onClick(DialogInterface dialog, int which) {
                   			}
                   		});
                   		alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                   			@Override
                   			public void onClick(DialogInterface dialog, int which) {
                   				String name = etUsername.getText().toString();
                   				String track = etPassword.getText().toString();
                   				mySQLiteAdapter.delete_byID(item_id);
           	                	updateList();
                   				Toast.makeText(getBaseContext(), "Deleted Name: " + name + " Track: " + track, Toast.LENGTH_SHORT).show();
                   			}
                   		});
                   		AlertDialog dialog = alert.create();
                   		dialog.show();
                        mode.finish();  // Automatically exists the action mode, when the user selects this action
                        break;
                    case R.id.action1:
                    	final int item_id1 = cursor.getInt(cursor.getColumnIndex(SQLiteAdapter.KEY_ID));
           	            String name = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_CONTENT1));
           	            String track = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_CONTENT2));
           	          	LayoutInflater inflater1 = getLayoutInflater();
                   		View alertLayout1 = inflater1.inflate(R.layout.layout_custom_dialog_edit, null);
                   		etUsername = (EditText) alertLayout1.findViewById(R.id.et_name);
                   		etPassword = (EditText) alertLayout1.findViewById(R.id.et_track);
                   		etUsername.setText(name);
                   		etPassword.setText(track);
                   		AlertDialog.Builder alert1 = new AlertDialog.Builder(MainActivity.this);
                   		alert1.setTitle("Edit item");
                   		alert1.setIcon(R.drawable.penicon);
                   		alert1.setView(alertLayout1);
                   		alert1.setCancelable(false);
                   		alert1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   			@Override
                   			public void onClick(DialogInterface dialog, int which) 
                   			{
                   			}
                   		});
                   		alert1.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                   			@Override
                   			public void onClick(DialogInterface dialog, int which) {
                   				String name = etUsername.getText().toString();
                   				String track = etPassword.getText().toString();
                   				if (checkstring(name,2) && checkstring(track,5))
                				{
                   				mySQLiteAdapter.update(item_id1, name, track);
           	                	updateList();
                   				Toast.makeText(getBaseContext(), "Update Name: " + name + " Track: " + track, Toast.LENGTH_SHORT).show();
                				}
                				else Toast.makeText(getBaseContext(), "Please enter the correct Name and Track", Toast.LENGTH_SHORT).show();
                   			}
                   		});
                   		AlertDialog dialog1 = alert1.create();
                   		dialog1.show();
                        mode.finish();  // Automatically exists the action mode, when the user selects this action
                        break;
                }
                return false;
            }
        };
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
       	 @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id) {        	   
       	    cursor = (Cursor) parent.getItemAtPosition(position);
       	    mMode = startActionMode(mCallback);
       	    return true;
       	
       	}});
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	  @Override
        	  public void onItemClick(AdapterView<?> parent, View view,int position, long id) 
        	  {
        		  Cursor cursor = (Cursor) parent.getItemAtPosition(position);
        		  String name = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_CONTENT1));
        		  String track = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_CONTENT2));
        		  final int id_key = cursor.getInt(cursor.getColumnIndex(SQLiteAdapter.KEY_ID));
        		  Intent intent = new Intent();
        		  intent.setClass(MainActivity.this,SecondActivity.class);
        		  intent.putExtra("Name", name);
        		  intent.putExtra("Track", track);//.putExtra("Track", track);
        		  //Log.d("---------->",id_key);
        		  intent.putExtra("ID", id_key);
        		  startActivity(intent);
        		  overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
        	  }
        	});
        try {
        	  ViewConfiguration config = ViewConfiguration.get(this);
        	  Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");

        	  if (menuKeyField != null) {
        	    menuKeyField.setAccessible(true);
        	    menuKeyField.setBoolean(config, false);
        	  }
        	}
        	catch (Exception e) {Log.d("sHasPermanentMenuKey",e.toString());}
    }
    @Override
    public void onStart() {
      super.onStart();
      getPrefs();
     //Log.d("x---------->","<----------------");
      updateList();
      EasyTracker.getInstance(this).activityStart(this);  // Add this method.
    }
    @Override
    public void onStop() {
      super.onStop();
      EasyTracker.getInstance(this).activityStop(this);  // Add this method.
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getSupportMenuInflater();
    	inflater.inflate(R.menu.items, menu);
    	getSupportMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        Context ctx = null;
		switch(item.getItemId()){
            case R.id.phone:
            	LayoutInflater inflater = getLayoutInflater();
        		View alertLayout = inflater.inflate(R.layout.layout_custom_dialog, null);
        		etUsername = (EditText) alertLayout.findViewById(R.id.et_name);
        		etPassword = (EditText) alertLayout.findViewById(R.id.et_track);
        		AlertDialog.Builder alert = new AlertDialog.Builder(this);
        		alert.setTitle("Add track number");
        		alert.setIcon(R.drawable.plus);
        		alert.setView(alertLayout);
        		alert.setCancelable(false);
        		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        			@Override
        			public void onClick(DialogInterface dialog, int which) {
        				Toast.makeText(getBaseContext(), "Abort", Toast.LENGTH_SHORT).show();
        			}
        		});
        		alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
        			@Override
        			public void onClick(DialogInterface dialog, int which) {
        				String name = etUsername.getText().toString();
        				String track = etPassword.getText().toString();
        				if (checkstring(name,2) && checkstring(track,5))
        				{
        				Toast.makeText(getBaseContext(), "Added Name: " + name + " Track: " + track, Toast.LENGTH_SHORT).show();
        				mySQLiteAdapter.insert(name, track,"Still no response from the server","-");
        				updateList();
        				}
        				else Toast.makeText(getBaseContext(), "Please enter the correct Name and Track", Toast.LENGTH_SHORT).show();
        			}
        		});
        		AlertDialog dialog = alert.create();
        		dialog.show();
            	break;
            case R.id.info:
            	AlertDialog.Builder alert1 = new AlertDialog.Builder(this);
            	alert1.setTitle("17Tracker lite version");
            	alert1.setMessage("Unofficial client for 17track.net. This is a release version of the application until it is able to not a lot ... A lot of plans to improve the application. Any suggestions or comments please contact us by e-mail. Thank you for using my application. Good luck! And do not forget to put the reviews!");
            	alert1.setPositiveButton("OK",null);
            	alert1.show();
            	//com.seventeentracker
            	/*Editor editor = getSharedPreferences("user_preferences",MODE_PRIVATE).edit();
                editor.putString("NameSave", "true");              
                editor.commit();
                TheBackupAgent.requestBackup(this);*/
                break;
            case R.id.settings:
            	//SharedPreferences mSharedPreferences = getSharedPreferences("user_preferences",Context.MODE_PRIVATE);
            	//Toast.makeText(getBaseContext(), mSharedPreferences.getString("NameSave", "false"), Toast.LENGTH_SHORT).show();
            	Intent settingsActivity = new Intent(getBaseContext(),
                        PreferenceActivity.class);
        startActivity(settingsActivity);
		  overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                break;
                /* case R.id.gamepad:
                break;*/
            case R.id.refresh:
            	if(!isOnline())
                {
                	AlertDialog.Builder alert2 = new AlertDialog.Builder(this);
                	alert2.setMessage("Check your internet connection");
                	//alert2.setMessage("Unofficial client for 17track.net. This is a beta version of the application until it is able to not a lot ... A lot of plans to improve the application. Any suggestions or comments please contact us by e-mail. Thank you for using my application. Good luck!");
                	alert2.setPositiveButton("OK",new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface dialog, int id) {
        					dialog.cancel();
        				}
        			});
                	alert2.show();
                }
            	else
            	{
            		if(!refreshstart)
            		{
            		refreshstart=true;
	            	setSupportProgressBarIndeterminateVisibility(true);
	            	if (cursor .moveToFirst()) {
	                        idt = cursor.getInt(cursor.getColumnIndex(SQLiteAdapter.KEY_ID));
	                        named = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_CONTENT1));
	                        states = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_CONTENT3));
	                        tracking = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_CONTENT2));
	                        try{
	                        	tracking=tracking.replace("\"", "");
	                            tracking=tracking.replace(" ", "");
	                		/*new GetRequstMain(this).execute("http://www.17track.net/r/handlertrack.ashx?"+
	                                "lo=www.17track.net&"+
	                                "pt=0&num="+tracking+"&"+
	                                "hs="+ md5(tracking+"{EDFCE98B-1CE6-4D87-8C4A-870D140B62BA}0{EDFCE98B-1CE6-4D87-8C4A-870D140B62BA}www.17track.net"));
	                        */
	                            digit20 = generateInt(20);
	                        	digit13=generateInt(13);
	                        	//GET /r/handlertrack.ashx?callback=jQuery21107374352796468884_1414255489102&num=ri070705302cn&pt=0&cm=0&cc=0&_=1414255489103 HTTP/1.1
	                        	new GetRequstMain(this).execute("http://www.17track.net/r/handlertrack.ashx?"+
	                        		"callback=jQuery"+digit20+"_"+digit13+"&"+
	                                "num="+tracking+"&"+
	                        		"pt=0&cm=0&cc=0&_="+digit13);
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
	            		Toast.makeText(getBaseContext(),"Sorry, to upgrade, you must add at least one track number", Toast.LENGTH_SHORT).show();
	            		setSupportProgressBarIndeterminateVisibility(false);
	            	}
            	}
            		else
            		{
            			Toast.makeText(getBaseContext(),"Sorry, you can not run the update twice", Toast.LENGTH_SHORT).show();
            		}
            }
        break;
        }
        return true;
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
	@Override
	public void onTaskComplete(String result) {
		Log.d("---------->","onTaskComplete");
		if (result!=null) parse(result);
		else Log.d("onTaskComplete", "Exception");
	}
	public static final String md5(final String s) {
	    final String MD5 = "MD5";
	    try {
	        // Create MD5 Hash
	        MessageDigest digest = java.security.MessageDigest
	                .getInstance(MD5);
	        digest.update(s.getBytes());
	        byte messageDigest[] = digest.digest();

	        // Create Hex String
	        StringBuilder hexString = new StringBuilder();
	        for (byte aMessageDigest : messageDigest) {
	            String h = Integer.toHexString(0xFF & aMessageDigest);
	            while (h.length() < 2)
	                h = "0" + h;
	            hexString.append(h);
	        }
	        return hexString.toString();

	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	    return "";
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
		             	  catch(Exception Ex) {mySQLiteAdapter.update_stat(idt,Ex.toString());mySQLiteAdapter.update_day(idt, "-");}
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
	             	  {mySQLiteAdapter.update_stat(idt,Ex.toString());mySQLiteAdapter.update_day(idt, "-");}
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
    	catch(Exception ex){/*mySQLiteAdapter.update_stat(idt,"Internal server error");mySQLiteAdapter.update_day(idt, "-");*/}
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
                   /* new GetRequstMain(this).execute("http://www.17track.net/r/handlertrack.ashx?"+
                            "lo=www.17track.net&"+
                            "pt=0&num="+tracking+"&"+
                            "hs="+ md5(tracking+"{EDFCE98B-1CE6-4D87-8C4A-870D140B62BA}0{EDFCE98B-1CE6-4D87-8C4A-870D140B62BA}www.17track.net"));
                    */
                        digit20 = generateInt(20);
                    	digit13=generateInt(13);
                    	//GET /r/handlertrack.ashx?callback=jQuery21107374352796468884_1414255489102&num=ri070705302cn&pt=0&cm=0&cc=0&_=1414255489103 HTTP/1.1
                    	new GetRequstMain(this).execute("http://www.17track.net/r/handlertrack.ashx?"+
                    		"callback=jQuery"+digit20+"_"+digit13+"&"+
                            "num="+tracking+"&"+
                    		"pt=0&cm=0&cc=0&_="+digit13);
                    }
                    catch(Exception Ex){Toast.makeText(getBaseContext(),"Your has a tracking number error or had not yet processed the postal service!", Toast.LENGTH_SHORT).show();}
                    
            }
        	else 
        	{
        		setSupportProgressBarIndeterminateVisibility(false);
        		updateList();
        		Toast.makeText(getBaseContext(), "Operation complete", Toast.LENGTH_SHORT).show();
        		refreshstart=false;
        	}
    	}
	}
	
    public Boolean checkstring(String str, int pos)
    {
    	if(str.length()<=pos) return false;
    	else return true;
    }
	private void Notification(String notificationTitle, String notificationMessage,int id) {
	    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	    android.app.Notification notification = new android.app.Notification(R.drawable.ic_launcher,"",
	    System.currentTimeMillis());

	    Intent notificationIntent = new Intent(this, MainActivity.class);
	    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
	    notification.setLatestEventInfo(MainActivity.this, notificationTitle, notificationMessage, pendingIntent);
	    notificationManager.notify(12000+id, notification);
	    try {
	        Uri notification1 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification1);
	        r.play();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
    boolean notifi, autoupdate,style;
    String ListPreference;

    private void getPrefs() {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            notifi = false;//prefs.getBoolean("checkboxPref", true);
            style = false;//prefs.getBoolean("checkboxPref2", true);
            autoupdate = false;//prefs.getBoolean("checkboxPref1", true);
            ListPreference = prefs.getString("listPref", "nr1");
    }
 
    public static String getStringFromFile (String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        fin.close();        
        return ret;
    }
    

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
          sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }
    @SuppressWarnings("deprecation")
	private void updateList()
    {
    	  cursor.requery();
    }
    private class CustomCursorAdapter extends SimpleCursorAdapter {
    	private final Context context;
    	//private final String[] values;
	     public CustomCursorAdapter(Context context, int layout, Cursor c,
	            String[] from, int[] to) {
	    	 
	        super(context, layout, c, from, to);
	        this.context = context;
	        
	    }
	     @Override
	 	public View getView(int position, View convertView, ViewGroup parent) {
	    	 View view = super.getView(position, convertView, parent); 
	 		ImageView imageView = (ImageView) view.findViewById(R.id.imagebox);
	 		TextView tv = (TextView) view.findViewById(R.id.dest);
	 		TextView tvtrack = (TextView) view.findViewById(R.id.track);
	 		Log.d("tv", tv.getText().toString());
	 		if (style)
	 		{
	 			imageView.getLayoutParams().height = 120;
		 		imageView.getLayoutParams().width = 120;
	 		/*if(position % 2 == 0){  
	 		    view.setBackgroundColor(Color.rgb(238, 233, 233));
	 		   }
	 		   else {
	 		    view.setBackgroundColor(Color.rgb(255, 255, 255));
	 		   }*/
	 		if(tv.getText().toString().contains("Deliver") || tv.getText().toString().contains("deliver"))
	 		{
	 			imageView.setImageResource(R.drawable.boxdelivery);
	 			tvtrack.setTextColor(Color.parseColor("#738b28"));
	 			tv.setTextColor(Color.parseColor("#738b28"));
	 			view.setBackgroundColor(Color.parseColor("#ebf2d3"));
	 		}
	 		else if(tv.getText().toString().contains("Import") || tv.getText().toString().contains("import"))
	 		{
	 			imageView.setImageResource(R.drawable.importimg);
	 			tvtrack.setTextColor(Color.parseColor("#a239c6"));
	 			tv.setTextColor(Color.parseColor("#a239c6"));
	 			view.setBackgroundColor(Color.parseColor("#ead1f2"));
	 			
	 		}
	 		else if(tv.getText().toString().contains("Export") || tv.getText().toString().contains("export"))
	 		{
	 			imageView.setImageResource(R.drawable.exoprt);
	 			tvtrack.setTextColor(Color.parseColor("#ad3d3d"));
	 			tv.setTextColor(Color.parseColor("#ad3d3d"));
	 			view.setBackgroundColor(Color.parseColor("#f6e6e1"));
	 		}
	 		else 
	 		{
	 			imageView.setImageResource(R.drawable.box);
	 			tv.setTextColor(Color.parseColor("#3981c6"));
	 			tvtrack.setTextColor(Color.parseColor("#3981c6"));
	 			view.setBackgroundColor(Color.parseColor("#e1e6f6"));
	 		}
	 		}
	 		else
	 		{
	 			imageView.getLayoutParams().height = 0;
		 		imageView.getLayoutParams().width = 0;
		 		tv.setTextColor(Color.parseColor("#737373"));
	 			tvtrack.setTextColor(Color.parseColor("#737373"));
	 			view.setBackgroundColor(Color.WHITE);
	 		}
	 		return view;
	 	}
	   
    }
    public static class TheBackupAgent extends BackupAgentHelper {
        // The name of the SharedPreferences file
        static final String PREFS = "user_preferences";

        // A key to uniquely identify the set of backup data
        static final String PREFS_BACKUP_KEY = "prefs";

        // Allocate a helper and add it to the backup agent
        @Override
        public void onCreate() {
        	Log.d("MyPrefsBackupAgent", "onCreate()");
            SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, PREFS);
            addHelper(PREFS_BACKUP_KEY, helper);
        }
        public static void requestBackup(Context context) 
        {
            BackupManager bm = new BackupManager(context);
            bm.dataChanged();
        }
    }
 
}