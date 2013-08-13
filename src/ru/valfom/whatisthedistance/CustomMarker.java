package ru.valfom.whatisthedistance;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

public class CustomMarker {

	private int id;
	private Marker marker;
	private Polyline polyline;
	
	public int getId() {
		
		return id;
	}

	public void setId(int id) {
		
		this.id = id;
	}

	public boolean isPolyline() {
		
		return (polyline == null) ? false : true;
	}
	
	public Marker getMarker() {
		
		return marker;
	}

	public void setMarker(Marker marker) {
		
		this.marker = marker;
	}

	public Polyline getPolyline() {
		
		return polyline;
	}
	
	public void setPolyline(Polyline polyline1) {
		
		this.polyline = polyline1;
	}
}
