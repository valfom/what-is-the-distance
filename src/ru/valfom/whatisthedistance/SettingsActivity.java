package ru.valfom.whatisthedistance;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuItem;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	public static final String KEY_MEASUREMENT_UNITS = "lUnits";
	
	private ListPreference lMeasurementUnits;
	
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		addPreferencesFromResource(R.xml.preferences);
		
		lMeasurementUnits = (ListPreference) getPreferenceScreen().findPreference(KEY_MEASUREMENT_UNITS);
	}
	
	@SuppressWarnings("deprecation")
	@Override
    protected void onPause() {
        
    	super.onPause();
          
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);    
    }
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {

		super.onResume();

		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        lMeasurementUnits.setSummary(sharedPreferences.getString(KEY_MEASUREMENT_UNITS, 
        		getString(R.string.settings_measurement_units)));
	}
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    	if (key.equals(KEY_MEASUREMENT_UNITS)) {
        	
    		lMeasurementUnits.setSummary(sharedPreferences.getString(KEY_MEASUREMENT_UNITS, 
            		getString(R.string.settings_measurement_units)));
    	}
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch (item.getItemId()) {
        
	        case android.R.id.home:

	        	onBackPressed();

	            return true;

	        default:
	        	return super.onMenuItemSelected(featureId, item);
		}
	}
}
