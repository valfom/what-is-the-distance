package ru.valfom.whatisthedistance;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends FragmentActivity {

	private GoogleMap map;
	
	private List<CustomMarker> markers = new ArrayList<CustomMarker>();
	
	private long lastBackPressTime = 0;
	private Toast toastOnExit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		
		setUpMapIfNeeded();
	}
	
	private void setUpMapIfNeeded() {
		
        if (map == null)
        	map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

        if (map == null) return;

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);
        
        map.setOnMapLongClickListener(new OnMapLongClickListener() {
			
			@Override
			public void onMapLongClick(LatLng point) {
				
				Marker marker = map.addMarker(new MarkerOptions()
						.position(point));
				
				CustomMarker customMarker = new CustomMarker();
				
				customMarker.setMarker(marker);
				
				if (markers.size() > 0) {
					
					LatLng prevPoint = markers.get(markers.size() - 1).getMarker().getPosition();
					
					Polyline polyline = map.addPolyline(new PolylineOptions()
				    		.add(prevPoint, point)
				    		.width(5)
				    		.color(Color.RED)
				    		);
					
					markers.get(markers.size() - 1).setPolyline(polyline);
				}
				
				markers.add(customMarker);
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
			
		default:
			break;
		}

		return true;
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
