package ru.valfom.whatisthedistance;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity {

	static final LatLng MOSCOW = new LatLng(55.751424, 37.618358);

	private GoogleMap map;

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

        map.addMarker(new MarkerOptions()
				.position(MOSCOW)
				.title("Moscow")
				.snippet("The capital of Russia")
				.draggable(true));
        
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(MOSCOW, 10));
        
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}
}
