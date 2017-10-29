package co.edu.javeriana.tandemsquad.tandem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;

import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseAuthentication;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseDatabase;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseStorage;
import co.edu.javeriana.tandemsquad.tandem.google.GoogleMapConstants;
import co.edu.javeriana.tandemsquad.tandem.google.GoogleMapController;
import co.edu.javeriana.tandemsquad.tandem.location.LocationController;
import co.edu.javeriana.tandemsquad.tandem.negocio.Marcador;
import co.edu.javeriana.tandemsquad.tandem.negocio.Usuario;
import co.edu.javeriana.tandemsquad.tandem.permissions.Permissions;
import co.edu.javeriana.tandemsquad.tandem.utilities.Utils;

public class HomeActivity extends NavigationActivity implements OnMapReadyCallback, PlaceSelectionListener {

    private GoogleMap googleMap;
    protected FireBaseAuthentication fireBaseAuthentication;
    protected FireBaseStorage fireBaseStorage;
    protected LocationController locationController;
    private FireBaseDatabase fireBaseDatabase;

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
                super.onMyLocationRecieved(location);
            }
        };
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        //autocompleteFragment.setOnPlaceSelectedListener(this);
        fireBaseDatabase = new FireBaseDatabase(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setPadding(0, 0, 0, 0);

        LatLng bogota = new LatLng(4.711000, -74.072094);
        CameraPosition.Builder cameraPosition = CameraPosition.builder();
        cameraPosition.target(bogota);
        cameraPosition.zoom(GoogleMapConstants.ZOOM_CITY);
        cameraPosition.bearing(0);

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition.build()), 1500, null);

        this.googleMap.getUiSettings().setCompassEnabled(true);
        this.googleMap.getUiSettings().setZoomGesturesEnabled(true);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);
        this.googleMap.getUiSettings().setMapToolbarEnabled(true);

        if(Permissions.askPermissionWithJustification(this, Permissions.FINE_LOCATION, getString(R.string.permission_gps))) {
            locationAction();
        }
    }

    private void locationAction() {
        if(Permissions.checkSelfPermission(this, Permissions.FINE_LOCATION) && googleMap != null) {
            googleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemClicked = item.getItemId();
        switch (itemClicked) {
            case R.id.menu_logout:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(!Permissions.permissionGranted(requestCode, permissions, grantResults)) {
            return;
        }
        switch (requestCode) {
            case Permissions.FINE_LOCATION:
                locationController.askForGPS();
                locationAction();
                break;
        }
    }

    @Override
    public void onPlaceSelected(Place place) {
        LatLng bogota = place.getLatLng();
        CameraPosition.Builder cameraPosition = CameraPosition.builder();
        cameraPosition.target(bogota);
        cameraPosition.zoom(GoogleMapConstants.ZOOM_STREET);
        cameraPosition.bearing(0);

        GoogleMapController.addMarker(bogota, place.getName().toString(), place.getAddress().toString(), googleMap);
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition.build()), 1500, null);
    }

    @Override
    public void onError(Status status) {}

    @Override
    protected void onStart() {
        super.onStart();
        fireBaseAuthentication.start();
        if(Permissions.askPermissionWithJustification(this, Permissions.FINE_LOCATION, getString(R.string.permission_gps))) {
            locationController.askForGPS();
        }
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


}
