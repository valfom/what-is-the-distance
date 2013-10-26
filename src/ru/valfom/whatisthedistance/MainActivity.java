package ru.valfom.whatisthedistance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity {

	private GoogleMap map;
	
	private TextView tvDistance;
	private TextView tvDistanceUnit;
	
	private double ratio = 1000;
	
	private Path path;
	
	private long lastBackPressTime = 0;
	private Toast toastOnExit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		
		tvDistance = (TextView) findViewById(R.id.tvDistance);
		tvDistanceUnit = (TextView) findViewById(R.id.tvDistanceUnit);
		
		setUpMapIfNeeded();
	}
	
	@Override
	protected void onResume() {

		super.onResume();
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		String mapType = sharedPreferences.getString("lMapType", getString(R.string.settings_normal));
		
		if (mapType.equals(getString(R.string.settings_normal))) map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		else if (mapType.equals(getString(R.string.settings_satellite))) map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		else if (mapType.equals(getString(R.string.settings_hybrid))) map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		
		String units = sharedPreferences.getString("lUnits", getString(R.string.km));
		
		if (units.equals(getString(R.string.km))) {
			
			tvDistanceUnit.setText(R.string.km);
			ratio = 1000;
			
		} else if (units.equals(getString(R.string.mi))) {
			
			tvDistanceUnit.setText(R.string.mi);
			ratio = 1609.344;
		}
		
		tvDistance.setText(String.format("%.3f", path.getDistance(ratio)));
	}

	private void setUpMapIfNeeded() {
		
        if (map == null)
        	map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

        if (map == null) return;
        
        path = new Path(map);
        
        map.setMapType(GoogleMap.MAP_TYPE_NONE);
        map.setMyLocationEnabled(true);
        
        map.setOnMapClickListener(new OnMapClickListener() {
			
			@Override
			public void onMapClick(LatLng point) {
				
				Marker marker = map.addMarker(new MarkerOptions()
						.position(point)
						.draggable(true)
						.visible(path.isShowMarkers()));
		
				path.addMarker(marker);
		
		tvDistance.setText(String.format("%.3f", path.getDistance(ratio)));
			}
		});
        
        map.setOnMapLongClickListener(new OnMapLongClickListener() {
			
			@Override
			public void onMapLongClick(LatLng point) {

				path.changeMarkersVisibility();
			}
		});
        
        map.setOnMarkerDragListener(new OnMarkerDragListener() {
			
			@Override
			public void onMarkerDragStart(Marker marker) {
				
				onMarkerDrag(marker);
			}
			
			@Override
			public void onMarkerDragEnd(Marker marker) {}
			
			@Override
			public void onMarkerDrag(Marker marker) {
				
				path.changePoint(marker);
				
				tvDistance.setText(String.format("%.3f", path.getDistance(ratio)));
			}
		});
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch (item.getItemId()) {

		case R.id.action_settings:
			Intent settings = new Intent(this, SettingsActivity.class);
			startActivity(settings);
			break;
		case R.id.action_clear_map:
			clearMap();
			break;
		case R.id.action_delete_last_marker:
			
			int markersCount = path.getMarkersCount();
			
			if (markersCount == 1) {
				
				clearMap();
				
			} else if (markersCount > 1) {
				
				path.deleteLastMarker();
				
				tvDistance.setText(String.format("%.3f", path.getDistance(ratio)));
			}
			
			break;
			
		default:
			break;
		}

		return true;
	}
	
	public void clearMap() {
		
		map.clear();
		tvDistance.setText(getString(R.string.default_value_distance));
		path.clear();
	}
	
	@Override
	public void onBackPressed() {

		if (lastBackPressTime < (System.currentTimeMillis() - 2000)) {

		    toastOnExit = Toast.makeText(this, getString(R.string.message_on_exit), Toast.LENGTH_SHORT);
		    toastOnExit.show();
		    lastBackPressTime = System.currentTimeMillis();

		  } else {

			  if (toastOnExit != null) toastOnExit.cancel();

			  super.onBackPressed();
		 }
	}
}
