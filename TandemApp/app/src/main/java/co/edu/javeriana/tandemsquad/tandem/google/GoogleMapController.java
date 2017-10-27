package co.edu.javeriana.tandemsquad.tandem.google;

import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapController {
    public static void addMarker(Location location, String title, String snippet, GoogleMap googleMap) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        addMarker(latLng, title, snippet, googleMap);
    }

    public static void addMarker(LatLng latLng, String title, String snippet, GoogleMap googleMap) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(title);
        if(snippet != null) {
            markerOptions.snippet(snippet);
        }
        googleMap.addMarker(markerOptions);
    }

    public static void addMarkerAndMove(Location location, String title, String snippet, int zoom, GoogleMap googleMap) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        addMarkerAndMove(latLng, title, snippet, zoom, googleMap);
    }

    public static void addMarkerAndMove(LatLng latLng, String title, String snippet, int zoom, GoogleMap googleMap) {
        addMarker(latLng, title, snippet, googleMap);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }
}
