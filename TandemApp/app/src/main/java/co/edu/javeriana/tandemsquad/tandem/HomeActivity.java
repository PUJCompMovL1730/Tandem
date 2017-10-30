package co.edu.javeriana.tandemsquad.tandem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageButton;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;

import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseAuthentication;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseDatabase;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseStorage;
import co.edu.javeriana.tandemsquad.tandem.google.GoogleMapConstants;
import co.edu.javeriana.tandemsquad.tandem.google.GoogleMapController;
import co.edu.javeriana.tandemsquad.tandem.location.LocationController;
import co.edu.javeriana.tandemsquad.tandem.negocio.Marcador;
import co.edu.javeriana.tandemsquad.tandem.negocio.Recorrido;
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
    private boolean otherPath;
    private boolean travelStarted;
    private int optionSelected;

    private ImageButton share;
    private ImageButton type;
    private ImageButton members;
    private ImageButton track;
    private ImageButton photoHistory;

    private LatLng myLocation;

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
                if(location != null && !otherPath) {
                    googleMap.clear();
                    LatLng latLng =  new LatLng(location.getLatitude(), location.getLongitude());
                    drawMyPoint(latLng);
                    myLocation = latLng;

                    if(place != null) {
                        drawPath(latLng, place.getLatLng(), place.getName().toString(), place.getAddress().toString());
                    }
                }
            }
        };
    }

    private void drawPath(LatLng latLng1, LatLng latLng2, String title, String snippet) {
        googleMap.clear();
        drawMyPoint(latLng1);

        CameraPosition.Builder cameraPosition = CameraPosition.builder();
        cameraPosition.target(latLng2);
        cameraPosition.zoom(GoogleMapConstants.ZOOM_STREET);
        cameraPosition.bearing(0);

        Utils.drawPathBetween(latLng1, latLng2, googleMap);
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition.build()), 1500, null);
        GoogleMapController.addMarker(latLng2, title, snippet, googleMap, R.drawable.pin);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CHECK_SETTINGS:
                locationAction();
                break;
        }
    }

    @Override
    protected void initComponents() {

        super.initComponents();
        otherPath = false;
        travelStarted = false;
        optionSelected = -1;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(this);

        place = null;

        share = (ImageButton) findViewById(R.id.home_share);
        type = (ImageButton) findViewById(R.id.home_type);
        members = (ImageButton) findViewById(R.id.home_members);
        track = (ImageButton) findViewById(R.id.home_begin);
        photoHistory = (ImageButton) findViewById(R.id.home_history);
    }

    @Override
    protected void setButtonActions() {
        super.setButtonActions();

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });

        type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type();
            }
        });

        members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                members();
            }
        });

        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                track();
            }
        });

        photoHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoHistory();
            }
        });

    }

    private void share() {
        Snackbar.make(this.getCurrentFocus(), "Compartir este viaje para que se unan", Snackbar.LENGTH_LONG).show();
    }

    private void type()
    {
        if( place != null )
        {
            if( travelStarted )
            {
                Snackbar.make(this.getCurrentFocus(), "Ya has iniciado el recorrido.", Snackbar.LENGTH_LONG).show();
            }
            else
            {
                showTravelOptionsDialog();
                travelStarted = true;
            }
        }
        else
        {
            Snackbar.make(this.getCurrentFocus(), "Primero debes buscar un reccorido.", Snackbar.LENGTH_LONG).show();
        }
    }

    private void members()
    {
        if( place != null )
        {

        }
        else
        {
            Snackbar.make(this.getCurrentFocus(), "Primero debes buscar un reccorido.", Snackbar.LENGTH_LONG).show();
        }
    }

    private void track() {
        Snackbar.make(this.getCurrentFocus(), "Comenzar viaje, wuuuuu la mera hierba", Snackbar.LENGTH_LONG).show();
    }

    private void photoHistory() {
        Snackbar.make(this.getCurrentFocus(), "Tomar una foto y subir a historia", Snackbar.LENGTH_LONG).show();
    }

    public void showTravelOptionsDialog()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.travel_opts_title)
                .setSingleChoiceItems(R.array.travel_dialog_opts, -1, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        optionSelected = which;
                    }
                })
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if( optionSelected == 0 )
                        {
                            // TODO: Obtener Usuario
                            Marcador mInicio = new Marcador(myLocation, Marcador.Tipo.INICIO, "Inicio de recorrido.");
                            Marcador mFinal = new Marcador(place.getLatLng(), Marcador.Tipo.FIN, "Fin de recorrido.");
                            Recorrido r = new Recorrido(mInicio, mFinal, Recorrido.Estado.CASUAL);
                            // TODO: Agregar al historial del usuario
                        }
                        else if( optionSelected == 1 )
                        {
                            // TODO: Obtener Usuario
                            Marcador mInicio = new Marcador(myLocation, Marcador.Tipo.INICIO, "Inicio de recorrido.");
                            Marcador mFinal = new Marcador(place.getLatLng(), Marcador.Tipo.FIN, "Fin de recorrido.");
                            Recorrido r = new Recorrido(mInicio, mFinal, Recorrido.Estado.PUBLICADO);
                            // TODO: Agregar al historial del usuario
                            // TODO: Manejar en BD
                        }
                        else if( optionSelected == 2 )
                        {
                            // TODO: Obtener Usuario
                            Marcador mInicio = new Marcador(myLocation, Marcador.Tipo.INICIO, "Inicio de recorrido.");
                            Marcador mFinal = new Marcador(place.getLatLng(), Marcador.Tipo.FIN, "Fin de recorrido.");
                            Recorrido r = new Recorrido(mInicio, mFinal, Recorrido.Estado.VIAJE);
                            // TODO: Agregar al historial del usuario
                            // TODO: Manejar en BD
                        }
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setPadding(0, 0, 0, 0);

        this.googleMap.getUiSettings().setCompassEnabled(true);
        this.googleMap.getUiSettings().setZoomGesturesEnabled(true);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);
        this.googleMap.getUiSettings().setMapToolbarEnabled(true);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            otherPath = extras.getBoolean("draw", false);
        }
        if(otherPath) {
            double lat1 = extras.getDouble("lat1");
            double lon1 = extras.getDouble("lon1");
            double lat2 = extras.getDouble("lat2");
            double lon2 = extras.getDouble("lon2");
            Log.i("JAJAJA", lat1 + " " + lon1 + " " + lat2 + " " + lon2);

            LatLng latLng1 = new LatLng(lat1, lon1);
            LatLng latLng2 = new LatLng(lat2, lon2);
            drawPath(latLng1, latLng2, "Destino", "Vamos!");
        } else {
            LatLng bogota = new LatLng(4.711000, -74.072094);
            drawMyPoint(bogota);
        }

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
        otherPath = false;
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
