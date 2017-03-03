package com.seventeentracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import en.seventeentracker.R;

import com.seventeentracker.SQLiteAdapter;
import com.seventeentracker.GetRequst;
public class SecondActivity extends SherlockActivity implements AsyncTaskCompleteListener
{
	TextView et1,et2;
	private SQLiteAdapterData mySQLiteAdapterData;
	private SQLiteAdapter mySQLiteAdapter;
	SimpleCursorAdapter cursorAdapter,cursorAdapter1;
	Cursor cursor,cursor1;
	 ListView listView;
	 String name,track,digit20,digit13;
	 Integer idt;
	 private AdView adView;
	@SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        //-----------------actionbar----------
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        //-----------------db-----------------
        if(!isOnline())
        {
        	AlertDialog.Builder alert2 = new AlertDialog.Builder(this);
        	alert2.setMessage("Check your internet connection");
        	//alert2.setMessage("Unofficial client for 17track.net. This is a beta version of the application until it is able to not a lot ... A lot of plans to improve the application. Any suggestions or comments please contact us by e-mail. Thank you for using my application. Good luck!");
        	alert2.setPositiveButton("OK",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
					onBackPressed();
				}
			});
        	alert2.show();
        }
        else
        {
      //---------------------
        // Создание экземпляра adView.
           adView = new AdView(this);
           adView.setAdUnitId("ca-app-pub-2638991009658616/6464067488");
           adView.setAdSize(AdSize.BANNER);

           // Поиск разметки LinearLayout (предполагается, что ей был присвоен
           // атрибут android:id="@+id/mainLayout").
           LinearLayout layout = (LinearLayout)findViewById(R.id.secondLayout);

           // Добавление в разметку экземпляра adView.
           layout.addView(adView);

           // Инициирование общего запроса.
           AdRequest adRequest = new AdRequest.Builder().build();

           // Загрузка adView с объявлением.
           adView.loadAd(adRequest);        		
           //------------------
        listView = (ListView) findViewById(R.id.listView2);
        mySQLiteAdapterData = new SQLiteAdapterData (this);
        mySQLiteAdapterData.openToWrite();
        cursor = mySQLiteAdapterData.queueAll();
        String[] from = new String[]{SQLiteAdapter.KEY_ID, SQLiteAdapter.KEY_CONTENT1, SQLiteAdapter.KEY_CONTENT2};
        int[] to = new int[]{R.id.id, R.id.name_second, R.id.track_second};
        cursorAdapter =new SimpleCursorAdapter(this, R.layout.listview_row_second, cursor, from, to);
        listView.setAdapter(cursorAdapter);
        //------------------------------------
        //------------------------------------
        mySQLiteAdapter = new SQLiteAdapter(this);
        mySQLiteAdapter.openToWrite();
        cursor1 = mySQLiteAdapter.queueAll();
        //------------------------------------
        Intent intent = getIntent();
        name = intent.getStringExtra("Name");
        track = intent.getStringExtra("Track");
        idt = intent.getIntExtra("ID",1);
        TextView t = (TextView)findViewById(R.id.secondname);
        t.setText(name);
        TextView t1 = (TextView)findViewById(R.id.secondtrack);
        t1.setText(track);
        track = track.replace(" ", "");
	    getSupportActionBar().setTitle(name);
	    getSupportActionBar().setSubtitle(track);
        mySQLiteAdapterData.deleteAll();
        try
        {
        	track=track.replace("\"", "");
        	/*new GetRequst(this).execute("http://www.17track.net/r/handlertrack.ashx?"+
                "lo=www.17track.net&"+
                "pt=0&num="+track+"&"+
                "hs="+ md5(track+"{EDFCE98B-1CE6-4D87-8C4A-870D140B62BA}0{EDFCE98B-1CE6-4D87-8C4A-870D140B62BA}www.17track.net"));
        */
        	digit20 = generateInt(20);
        	digit13=generateInt(13);
        	//GET /r/handlertrack.ashx?callback=jQuery21107374352796468884_1414255489102&num=ri070705302cn&pt=0&cm=0&cc=0&_=1414255489103 HTTP/1.1
        	new GetRequst(this).execute("http://www.17track.net/r/handlertrack.ashx?"+
        		"callback=jQuery"+digit20+"_"+digit13+"&"+
                "num="+track+"&"+
        		"pt=0&cm=0&cc=0&_="+digit13);
        }
        catch(Exception Ex){Toast.makeText(getBaseContext(),"Your has a tracking number error or had not yet processed the postal service!", Toast.LENGTH_SHORT).show();}
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
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	        	onBackPressed();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public void onTaskComplete(String result) {
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
    	try{
    		sjons=result;
    		sjons=sjons.replace("jQuery"+digit20+"_"+digit13, "");
    		sjons=sjons.replace("(", "");
    		JSONObject reader = new JSONObject(sjons);
    		JSONObject obj=null;
    	/*	if (reader.getInt("ret")==1)Log.d("---------->","trueeeeeeeeeeeeeee");
    		else Log.d("---------->","falseeeeeeeeeeeeeee");*/
    		JSONObject dat  = reader.getJSONObject("dat");
    		//String dest = dat.getString("y");
    		//String destx = dat.getString("x");
    		String dest = dat.getString("z2");
    		String destx = dat.getString("z1"); 
    	    if (dest.length()>5)
    	    {
        		//Log.d("y---------->"+dest.length(),dest);
        	    JSONArray arrayx=new JSONArray(dest);
        	       for(int i = 0; i < arrayx.length(); ++i) 
                   {
                      obj = arrayx.getJSONObject(i);
                      mySQLiteAdapterData.insert(obj.getString("c")+" "+obj.getString("z"), obj.getString("a"));
                      updateList();
	   	               if(i==0) 
	            	  	{
		             	  try
		             	  {
			             	 Log.d("---------->",idt+"   "+obj.getString("z"));
			             	 mySQLiteAdapter.update_stat(idt,obj.getString("a")+" "+ obj.getString("c")+" "+obj.getString("z"));
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
    	    	//Log.d("x---------->"+destx.length(),destx);
    	    	JSONArray array=new JSONArray(destx);
	            for(int i = 0; i < array.length(); ++i) 
	            {
	               obj = array.getJSONObject(i);
	               mySQLiteAdapterData.insert(obj.getString("c")+" "+obj.getString("z"), obj.getString("a"));
	               updateList();
	               if(i==0) 
             	  	{
	             	  try
	             	  {
		             	 Log.d("---------->",idt+"   "+obj.getString("c")+" "+obj.getString("z"));
		             	 mySQLiteAdapter.update_stat(idt,obj.getString("a")+" "+obj.getString("c")+" "+obj.getString("z"));
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
    	    	Toast.makeText(getBaseContext(), "Your has a tracking number error or had not yet processed the postal service!", Toast.LENGTH_SHORT).show();
    	    	//onBackPressed();
    	    }
    	}
    	catch(Exception ex){mySQLiteAdapter.update_stat(idt,ex.toString());mySQLiteAdapter.update_day(idt, "-");}
        //Toast.makeText(getBaseContext(), "You selected Computer", Toast.LENGTH_SHORT).show();
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
	
	@Override
	public void onBackPressed() 
	{
	    super.onBackPressed();
	    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
	    mySQLiteAdapterData.deleteAll();
	}
    @SuppressWarnings("deprecation")
	private void updateList()
    {
    	  cursor.requery();
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
}
