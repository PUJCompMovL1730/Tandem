package co.edu.javeriana.tandem;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends BaseNavigationActivity implements OnMapReadyCallback {

    static final int GOOGLE_PERMISSION = 12;
    GoogleMap mMap;
    ImageButton drawerButton;
    LatLng javeriana;
    BitmapDescriptor icon;
    Map<String, MarkerOptions> markers;

    ViewStub stub;
    View contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Obtener el stub y actualizarlo con el layout requerido
        stub = (ViewStub) findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.activity_home_content);
        contentView = stub.inflate();


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        drawerButton = (ImageButton) findViewById(R.id.sideBar);
        drawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        javeriana = new LatLng(4.626951, -74.064160);
        icon = Utils.getBitmapDescriptor(getBaseContext(), R.drawable.tandem, 120, 59);
        markers = new HashMap<>();
        markers.put("Universidad Javeriana", new MarkerOptions().position(javeriana).title("Universidad Javeriana").snippet("Jaaaa perro").icon(icon));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setMyLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case GOOGLE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setMyLocation();
                }
        }
    }

    private void setMyLocation() {
        int permission_FINE = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permission_COARSE = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        LatLng place = javeriana;
        MarkerOptions marker = markers.get("Universidad Javeriana");

        if (permission_FINE == PackageManager.PERMISSION_GRANTED && permission_COARSE == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            Location location;
            LocationManager locationManagerCt = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            location = locationManagerCt.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                place = new LatLng(location.getLatitude(), location.getLongitude());
                marker = new MarkerOptions().position(place).title("Tu ubicación").icon(icon);
            } else {
                Toast.makeText(getBaseContext(), "Null perro", Toast.LENGTH_SHORT).show();
            }
        } else {
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, GOOGLE_PERMISSION);
        }

        Snackbar.make(findViewById(android.R.id.content), marker.getTitle(), Snackbar.LENGTH_SHORT).show();
        mMap.addMarker(marker);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place, 16));
    }
}
