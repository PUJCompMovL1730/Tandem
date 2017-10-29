package co.edu.javeriana.tandemsquad.tandem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.internal.PlaceEntity;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;

import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseAuthentication;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseDatabase;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseStorage;
import co.edu.javeriana.tandemsquad.tandem.google.GoogleMapConstants;
import co.edu.javeriana.tandemsquad.tandem.google.GoogleMapController;
import co.edu.javeriana.tandemsquad.tandem.google.pathTracking.DirectionsJSONParser;
import co.edu.javeriana.tandemsquad.tandem.location.LocationController;
import co.edu.javeriana.tandemsquad.tandem.negocio.Marcador;
import co.edu.javeriana.tandemsquad.tandem.negocio.Usuario;
import co.edu.javeriana.tandemsquad.tandem.permissions.Permissions;
import co.edu.javeriana.tandemsquad.tandem.utilities.Utils;

import static co.edu.javeriana.tandemsquad.tandem.utilities.ActivityResult.REQUEST_CHECK_SETTINGS;

public class HomeActivity extends NavigationActivity implements OnMapReadyCallback, PlaceSelectionListener {

    private GoogleMap googleMap;
    protected FireBaseAuthentication fireBaseAuthentication;
    protected FireBaseStorage fireBaseStorage;
    protected LocationController locationController;
    private FireBaseDatabase fireBaseDatabase;
    private Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapsInitializer.initialize(getApplicationContext());

        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.activity_home);
        View contentView = stub.inflate();

        initComponents();
        setButtonActions();

        fireBaseAuthentication = new FireBaseAuthentication(this) {
            @Override
            public void onSignInSuccess() {
                setToolbarData(fireBaseAuthentication, fireBaseStorage);
            }
        };

        fireBaseStorage = new FireBaseStorage(this) {
            @Override
            protected void onDownloadFileSuccess(Task<FileDownloadTask.TaskSnapshot> task, File file) {
                Uri uri = Uri.fromFile(file);
                Bitmap image = (Bitmap) Utils.getImageFormUri(HomeActivity.this, uri);
                viewImage.setImageBitmap(image);
            }
        };

        locationController = new LocationController(this) {
            @Override
            protected void onMyLocationRecieved(Location location) {
                if(location != null) {
                    googleMap.clear();
                    LatLng latLng =  new LatLng(location.getLatitude(), location.getLongitude());
                    drawMyPoint(latLng);

                    if(place != null) {
                        LatLng lastPlace = place.getLatLng();
                        CameraPosition.Builder cameraPosition = CameraPosition.builder();
                        cameraPosition.target(lastPlace);
                        cameraPosition.zoom(GoogleMapConstants.ZOOM_STREET);
                        cameraPosition.bearing(0);

                        Utils.drawPathBetween(latLng, lastPlace, googleMap);
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition.build()), 1500, null);
                        GoogleMapController.addMarker(lastPlace, place.getName().toString(), place.getAddress().toString(), googleMap, R.drawable.pin);
                    }
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK && requestCode != REQUEST_CHECK_SETTINGS) {
            return;
        }
        switch (requestCode){
            case REQUEST_CHECK_SETTINGS:
                locationAction();
                break;
        }
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(this);
        fireBaseDatabase = new FireBaseDatabase(this);
        place = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setPadding(0, 0, 0, 0);

        this.googleMap.getUiSettings().setCompassEnabled(true);
        this.googleMap.getUiSettings().setZoomGesturesEnabled(true);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);
        this.googleMap.getUiSettings().setMapToolbarEnabled(true);

        LatLng bogota = new LatLng(4.711000, -74.072094);
        drawMyPoint(bogota);

        if(Permissions.askPermissionWithJustification(this, Permissions.FINE_LOCATION, getString(R.string.permission_gps))) {
            locationAction();
        }
    }

    private void locationAction() {
        if(Permissions.checkSelfPermission(this, Permissions.FINE_LOCATION) && googleMap != null) {
            if(locationController.askForGPS()) {
                googleMap.setMyLocationEnabled(true);
                locationController.getMyLocation();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(!Permissions.permissionGranted(requestCode, permissions, grantResults)) {
            return;
        }
        switch (requestCode) {
            case Permissions.FINE_LOCATION:
                locationAction();
                break;
        }
    }

    @Override
    public void onPlaceSelected(Place place) {
        this.place = place;
        locationController.getMyLocation();
    }

    @Override
    public void onError(Status status) {}

    @Override
    protected void onStart() {
        super.onStart();
        fireBaseAuthentication.start();
        locationAction();
    }

    @Override
    protected void onStop() {
        super.onStop();
        fireBaseAuthentication.stop();
    }

    @Override
    protected void logout() {
        fireBaseAuthentication.signOut();
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
    }

    private void drawMyPoint(LatLng latLng) {
        GoogleMapController.addMarkerAndMove(latLng, "Tu estas aquí", "Pedalea", GoogleMapConstants.ZOOM_STREET, googleMap, R.drawable.bicycle);
        googleMap.addCircle(new CircleOptions().center(latLng).radius(500).fillColor(Color.argb(100, 109, 184, 242)));
    }
}
