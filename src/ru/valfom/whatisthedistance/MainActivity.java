package ru.valfom.whatisthedistance;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
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
	
	private double distance;
	private TextView tvDistance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		
		tvDistance = (TextView) findViewById(R.id.tvDistance);
		
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
					
					CustomMarker prevMarker = markers.get(markers.size() - 1);
					LatLng prevPoint = prevMarker.getMarker().getPosition();
					
					Polyline polyline = map.addPolyline(new PolylineOptions()
				    		.add(prevPoint, point)
				    		.width(5)
				    		.color(Color.RED)
				    		);
					
					distance += calculateDistance(prevPoint.latitude, prevPoint.longitude, point.latitude, point.longitude);
					
					tvDistance.setText(String.format("%.3f", distance / 1000));
					
					prevMarker.setPolyline(polyline);
				}
				
				markers.add(customMarker);
			}
		});
    }
	
	private double calculateDistance(double llat1, double llong1, double llat2, double llong2) {

		// http://gis-lab.info/qa/great-circles.html

		int rad = 6372795;

		double lat1 = llat1 * Math.PI / 180;
		double lat2 = llat2 * Math.PI / 180;
		double long1 = llong1 * Math.PI / 180;
		double long2 = llong2 * Math.PI / 180;

		double cl1 = Math.cos(lat1);
		double cl2 = Math.cos(lat2);
		double sl1 = Math.sin(lat1);
		double sl2 = Math.sin(lat2);
		double delta = long2 - long1;
		double cdelta = Math.cos(delta);
		double sdelta = Math.sin(delta);

		double y = Math.sqrt(Math.pow(cl2 * sdelta, 2) + Math.pow(cl1 * sl2 - sl1 * cl2 * cdelta, 2));
		double x = sl1 * sl2 + cl1 * cl2 * cdelta;
		double ad = Math.atan2(y, x);
		double dist = ad * rad;

		return dist;
	}

	public double round(double d, int p) {

    	return new BigDecimal(d).setScale(p, RoundingMode.HALF_UP).doubleValue();
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
