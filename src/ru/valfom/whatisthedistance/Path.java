package ru.valfom.whatisthedistance;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class Path {

	private GoogleMap map;
	
	private List<CustomMarker> markers = new ArrayList<CustomMarker>();
	private boolean showMarkers = true;
	private double distance;
	
	public Path(GoogleMap map) {
		
		this.map = map;
	}

	public void changePoint(Marker marker) {
		
		for (CustomMarker customMarker : markers) {
			
			if (customMarker.getMarker().equals(marker)) {
				
				Double prevDistance = null, nextDistance = null;
				int id = customMarker.getId();
				
				if (id > 0) {
					
					CustomMarker prevCustomMarker = markers.get(id - 1);
				
					if (prevCustomMarker.isPolyline()) {
						
						Polyline polyline = prevCustomMarker.getPolyline();
						
						List<LatLng> points = polyline.getPoints();
						
						LatLng prevPoint = points.get(0);
						LatLng curPoint = marker.getPosition();
						
						polyline.remove();
						
						Polyline polylineNew = map.addPolyline(new PolylineOptions()
			    				.add(prevPoint, curPoint)
			    				.width(5)
			    				.color(Color.RED)
			    				.geodesic(true));
						
						prevDistance = Utils.calculateDistance(prevPoint.latitude, prevPoint.longitude, curPoint.latitude, curPoint.longitude);
						
						prevCustomMarker.setPolyline(polylineNew);
						prevCustomMarker.setDistance(prevDistance);
					}
				}
				
				if (id < markers.size() - 1) {
					
						if (customMarker.isPolyline()) {
						
						Polyline polyline = customMarker.getPolyline();
						
						List<LatLng> points = polyline.getPoints();
						
						LatLng nextPoint = points.get(1);
						LatLng curPoint = marker.getPosition();
						
						polyline.remove();
						
						Polyline polylineNew = map.addPolyline(new PolylineOptions()
			    				.add(curPoint, nextPoint)
			    				.width(5)
			    				.color(Color.RED)
			    				.geodesic(true));
						
						nextDistance = Utils.calculateDistance(curPoint.latitude, curPoint.longitude, nextPoint.latitude, nextPoint.longitude);
						
						customMarker.setPolyline(polylineNew);
						customMarker.setDistance(nextDistance);
					}
				}
				
				distance = 0;
				
				for (int i = 0; i < markers.size(); i++) {
					
					if ((i == (id - 1)) || (i == id)) continue;
					
					distance += markers.get(i).getDistance();
				}
				
				if (prevDistance != null) distance += prevDistance;
				if (nextDistance != null) distance += nextDistance;
				
				break;
			}
		}
	}
	
	public void addMarker(Marker marker) {

		CustomMarker customMarker = new CustomMarker();
		
		customMarker.setMarker(marker);
		customMarker.setId(markers.size());
		
		if (markers.size() >= 1) {
			
			double curDistance;
			LatLng point = marker.getPosition();
			
			CustomMarker prevMarker = markers.get(markers.size() - 1);
			LatLng prevPoint = prevMarker.getMarker().getPosition();
			
			Polyline polyline = map.addPolyline(new PolylineOptions()
		    		.add(prevPoint, point)
		    		.width(5)
		    		.color(Color.RED)
		    		.geodesic(true));
			
			curDistance = Utils.calculateDistance(prevPoint.latitude, prevPoint.longitude, point.latitude, point.longitude);
			
			distance += curDistance;
			
			prevMarker.setPolyline(polyline);
			prevMarker.setDistance(curDistance);
		}
		
		markers.add(customMarker);
	}
	
	public void deleteLastMarker() {
		
		int markersCount = markers.size();
		
		if ((markersCount <= 2) && (!showMarkers)) changeMarkersVisibility();
		
		if (markersCount <= 1) {
			
			clear();
		} else {
			
			CustomMarker customMarker = markers.get(markersCount - 1);
			customMarker.getMarker().remove();
			markers.remove(markersCount - 1);
			CustomMarker prevCustomMarker = markers.get(markers.size() - 1);
			prevCustomMarker.getPolyline().remove();
			distance -= prevCustomMarker.getDistance();
			prevCustomMarker.setDistance(0);
		}
	}
	
	public double getDistance(double ratio) {
		
		return distance / ratio;
	}

	public boolean isShowMarkers() {
		
		return showMarkers;
	}

	public void changeMarkersVisibility() {

		if (markers.size() >= 2) {
			
			showMarkers = !showMarkers;
			
			for (CustomMarker customMarker : markers) {
				
				customMarker.getMarker().setVisible(showMarkers);
			}
		}
	}
	
	public void clear() {
		
		markers.clear();
		
		distance = 0;
		
		if (!showMarkers) changeMarkersVisibility();
	}
	
	public int getMarkersCount() {
		
		return markers.size();
	}
}
