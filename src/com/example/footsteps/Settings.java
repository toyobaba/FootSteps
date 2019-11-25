package com.example.footsteps;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Settings extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	private ListPreference mLp;
	private EditTextPreference eTp;
	
	@SuppressWarnings("deprecation")
	@SuppressLint({ "NewApi", "NewApi" })
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
	       	        
		addPreferencesFromResource(R.xml.settings);
		mLp = (ListPreference)getPreferenceScreen().findPreference("sync_time");
		eTp = (EditTextPreference)getPreferenceScreen().findPreference("status");		
		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
		String pname = this.getPreferenceManager().getSharedPreferencesName();
		System.out.println(pname);
	}

	public void onSharedPreferenceChanged(SharedPreferences prefs, String key){
		if (key.equals("sync_time")){
			mLp.setSummary(prefs.getString(key, "10000"));
		}
		if (key.equals("status")){
			eTp.setSummary(prefs.getString(key, ""));
		}
	}
}