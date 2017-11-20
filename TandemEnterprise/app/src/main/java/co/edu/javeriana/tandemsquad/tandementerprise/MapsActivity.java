package co.edu.javeriana.tandemsquad.tandementerprise;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import co.edu.javeriana.tandemsquad.tandementerprise.Firebase.FirebaseAuthManager;
import co.edu.javeriana.tandemsquad.tandementerprise.Firebase.FirebaseDBManager;
import co.edu.javeriana.tandemsquad.tandementerprise.Location.GMapsManager;
import co.edu.javeriana.tandemsquad.tandementerprise.Location.LocationManager;
import co.edu.javeriana.tandemsquad.tandementerprise.Mundo.Enterprise;
import co.edu.javeriana.tandemsquad.tandementerprise.Permissions.PermissionsManager;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button btnEvents;
    private Button btnMarkers;
    private ImageButton btnLogout;

    private FirebaseAuthManager authManager;
    private FirebaseDBManager db;
    private Enterprise currentUser;
    private LocationManager locManager;

    private LatLng myLocation;
    private LatLng enterpriseLocation;

    private Intent eventIntent;

    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        activity = this;

        eventIntent = new Intent(MapsActivity.this, EventsActivity.class );
        db = new FirebaseDBManager(this);

        authManager = new FirebaseAuthManager( this )
        {
            @Override
            public void onSignInSuccess()
            {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        currentUser = db.getEnterprise( authManager.getUser().getUid() );
                    }
                }).start();
            }
        };

        locManager = new LocationManager( this )
        {
            @Override
            protected void onMyLocationRecieved( Location location )
            {
                if( location != null )
                {
                    mMap.clear();

                    LatLng latLng = new LatLng( location.getLatitude(), location.getLongitude() );
                    drawMyPoint( latLng );
                    myLocation = latLng;
                }
            }
        };

        initComponents();
        setupListeners();
    }

    public void initComponents()
    {
        btnEvents = (Button) findViewById(R.id.btnEvents);
        btnMarkers = (Button) findViewById(R.id.btnMarkers);
        btnLogout = (ImageButton) findViewById(R.id.btnLogout);
    }

    public void setupListeners()
    {
        btnEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                startActivity(eventIntent);
            }
        });

        btnMarkers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                authManager.signOut();
                db.writeEnterprise(currentUser);
                activity.finish();
            }
        });
    }

    private void locationAction( )
    {
        if (PermissionsManager.checkSelfPermission(this, PermissionsManager.FINE_LOCATION) && mMap != null)
        {
            if (locManager.askForGPS())
            {
                mMap.setMyLocationEnabled(true);
                locManager.getMyLocation();
            }
        }
    }

    private void drawMyPoint( LatLng latLng )
    {
        GMapsManager.addMarkerAndMove(latLng, "Tu estas aquí", "Pedalea", 15, mMap, R.drawable.bicycle);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (PermissionsManager.askPermissionWithJustification(this, PermissionsManager.FINE_LOCATION, getString(R.string.permission_gps)))
        {
            locationAction();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        authManager.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        authManager.stop();
    }
}
