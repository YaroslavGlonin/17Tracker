package com.seventeentracker;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;










import com.actionbarsherlock.view.MenuItem;

import en.seventeentracker.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;
 
public class PreferenceActivity extends SherlockPreferenceActivity {
        @SuppressWarnings("deprecation")
		@Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                addPreferencesFromResource(R.xml.preferences);
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                ActionBar ab = getSupportActionBar();
                ab.setDisplayHomeAsUpEnabled(true);
                // Get the custom preference
                final Preference CheckPref = (Preference) findPreference("checkboxPref1");
                CheckPref.setEnabled(false);
                final Preference CheckPref1 = (Preference) findPreference("checkboxPref");
                CheckPref1.setEnabled(false);
                final Preference CheckPref2 = (Preference) findPreference("listPref");
                CheckPref2.setEnabled(false);
                final Preference CheckPref3 = (Preference) findPreference("checkboxPref2");
                CheckPref3.setEnabled(false);
               /* CheckPref.setOnPreferenceChangeListener( new OnPreferenceChangeListener() {
                            @Override
                            public boolean onPreferenceChange(Preference preference, Object newValue) {
                            	if("true".equals(newValue.toString()))
                            	{
                            		StartService();
                            		Log.d("Exelent!","StartService()");
                            	}
                            	else {StopService();Log.d("FALSEE!","StopService()");}
                        return true;
                    }
                });*/
                Preference customPref = (Preference) findPreference("customPref");
                customPref
                                .setOnPreferenceClickListener(new OnPreferenceClickListener() {
 
                                        public boolean onPreferenceClick(Preference preference) {
                                                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                                try {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                } catch (android.content.ActivityNotFoundException anfe) {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                                                }
                                                return true;
                                        }
 
                                });
                Preference customPref1 = (Preference) findPreference("customPref1");
                customPref1
                                .setOnPreferenceClickListener(new OnPreferenceClickListener() {
 
                                        public boolean onPreferenceClick(Preference preference) {
                                                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                                try {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName+"full")));
                                                } catch (android.content.ActivityNotFoundException anfe) {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName+"full")));
                                                }
                                                return true;
                                        }
 
                                });
        }
        public void StartService() 
        {
        	startService(new Intent(this, ServiceActivity.class));
        }
        public void StopService() 
        {
        	stopService(new Intent(this, ServiceActivity.class));
        }
    	@Override
    	public void onBackPressed() 
    	{
    	    super.onBackPressed();
    	    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    	    return;
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
}