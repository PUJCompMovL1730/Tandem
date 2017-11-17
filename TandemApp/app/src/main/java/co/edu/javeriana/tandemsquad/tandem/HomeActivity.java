package co.edu.javeriana.tandemsquad.tandem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.util.Calendar;

import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseAuthentication;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseDatabase;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseStorage;
import co.edu.javeriana.tandemsquad.tandem.google.GoogleMapConstants;
import co.edu.javeriana.tandemsquad.tandem.google.GoogleMapController;
import co.edu.javeriana.tandemsquad.tandem.location.LocationController;
import co.edu.javeriana.tandemsquad.tandem.negocio.Marcador;
import co.edu.javeriana.tandemsquad.tandem.negocio.Recorrido;
import co.edu.javeriana.tandemsquad.tandem.negocio.Usuario;
import co.edu.javeriana.tandemsquad.tandem.permissions.Permissions;
import co.edu.javeriana.tandemsquad.tandem.utilities.Utils;

import static co.edu.javeriana.tandemsquad.tandem.utilities.ActivityResult.REQUEST_CHECK_SETTINGS;

public class HomeActivity extends NavigationActivity implements OnMapReadyCallback, PlaceSelectionListener, CreateMarkerDialog.OnMarkerCreatedListener, CreatePublicTravelDialog.OnTravelCreatedListener {

  private static final double BOGOTA_LOWER_BOUND_LATITUDE = 4.465505;
  private static final double BOGOTA_LOWER_BOUND_LONGITUDE = -74.233671;
  private static final double BOGOTA_UPPER_BOUND_LATITUDE = 4.836203;
  private static final double BOGOTA_UPPER_BOUND_LONGITUDE = -73.956167;

  private GoogleMap googleMap;

  protected FireBaseAuthentication fireBaseAuthentication;
  protected FireBaseStorage fireBaseStorage;
  protected LocationController locationController;
  private FireBaseDatabase fireBaseDatabase;
  private Place searchPlace;

  private boolean isFabOpen;
  private FloatingActionButton fabAddElement, fabInstantTravel, fabPlanTravel, fabAddStory, fabAddMarker;
  private Animation fabOpen, fabClose, rotateForward, rotateBackward, btnOpen, btnClose;
  private Button stopTravelButton;

  private ImageView drawerIcon;

  private boolean otherPath;
  private boolean travelStarted;
  private int optionSelected;

  private Usuario currentUser;

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

    fireBaseDatabase = new FireBaseDatabase(this);

    fireBaseAuthentication = new FireBaseAuthentication(this) {
      @Override
      public void onSignInSuccess() {
        setToolbarData(fireBaseAuthentication, fireBaseStorage);
        new Thread(new Runnable() {
          @Override
          public void run() {
            currentUser = fireBaseDatabase.getUser(fireBaseAuthentication.getUser().getUid());
          }
        }).start();
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
        if (location != null && !otherPath) {
          googleMap.clear();
          LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
          drawMyPoint(latLng);
          myLocation = latLng;

          if (searchPlace != null) {
            drawPath(latLng, searchPlace.getLatLng(), searchPlace.getName().toString(), searchPlace.getAddress().toString());
          }
        }
      }
    };
  }

  //Mostrar ruta en el mapa
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
    switch (requestCode) {
      case REQUEST_CHECK_SETTINGS:
        locationAction();
        break;
    }
  }

  protected void initComponents() {
    otherPath = false;
    travelStarted = false;
    optionSelected = -1;

    //Floating Buttons
    isFabOpen = false;
    fabAddElement = (FloatingActionButton)findViewById(R.id.fab_add_element);
    fabInstantTravel = (FloatingActionButton)findViewById(R.id.fab_instant_travel);
    fabPlanTravel= (FloatingActionButton)findViewById(R.id.fab_plan_travel);
    fabAddStory = (FloatingActionButton)findViewById(R.id.fab_add_story);
    fabAddMarker = (FloatingActionButton)findViewById(R.id.fab_add_marker);

    //Animaciones
    fabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
    fabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
    rotateForward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
    rotateBackward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
    btnOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.btn_open);
    btnClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.btn_close);

    stopTravelButton = (Button)findViewById(R.id.finish_travel);

    //Mapa
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);

    //Búsqueda de Google Places
    PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
    autocompleteFragment.setBoundsBias(new LatLngBounds(new LatLng(BOGOTA_LOWER_BOUND_LATITUDE, BOGOTA_LOWER_BOUND_LONGITUDE),
        new LatLng(BOGOTA_UPPER_BOUND_LATITUDE, BOGOTA_UPPER_BOUND_LONGITUDE)));
    autocompleteFragment.setHint(getString(R.string.search_bar_hint));
    drawerIcon = (ImageView)((LinearLayout) autocompleteFragment.getView()).getChildAt(0);
    drawerIcon.setImageDrawable(getDrawable(R.drawable.icon_menu_black));
    drawerIcon.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        drawer.openDrawer(GravityCompat.START);
      }
    });
    autocompleteFragment.setOnPlaceSelectedListener(this);

    searchPlace = null;
  }

  //Inicializar los Floating Buttons
  @Override
  protected void setButtonActions(){
    super.setButtonActions();

    //Floating Button de añadir elemento (+)
    fabAddElement.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(isFabOpen){
          hideAllFloatingButtons();
        } else {
          showAllFloatingButtons();
        }
      }
    });

    //Floating Button para crear un recorrido "instantáneo" (privado y que se realiza de una vez)
    fabInstantTravel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(searchPlace == null){ //No se ha seleccionado ningún lugar para el recorrido
          Snackbar.make(getCurrentFocus(), R.string.empty_search_place, Snackbar.LENGTH_LONG).show();
        } else {
          if(travelStarted){ //Existe un recorrido en curso
            Snackbar.make(getCurrentFocus(), R.string.create_instant_travel_during_travel,Snackbar.LENGTH_LONG).show();
          } else { //Crear el recorrido
            hideAllFloatingButtons();
            createInstantTravel();
          }
        }
      }
    });

    //Floating Button para crear un recorrido "frecuente"(público con una frecuencia[dia y hora])
    // o viaje(público con una única fecha)
    fabPlanTravel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //No se ha seleccionado ningún lugar para el recorrido
        if(searchPlace == null){
          Snackbar.make(getCurrentFocus(), R.string.empty_search_place, Snackbar.LENGTH_LONG).show();
        } else { //Crear el recorrido
          hideAllFloatingButtons();
          planTravel();
        }
      }
    });

    //Floating Button de publicar historia
    fabAddStory.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //TODO Tomar foto y publicar historia
      }
    });

    //Floating Button de añadir marcador
    fabAddMarker.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        hideAllFloatingButtons();
        addMarker();
      }
    });

    //Ocultar el botón de finalizar recorrido
    stopTravelButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        travelStarted = false;
        //TODO Change status in DB to "FINALIZADO"
        stopTravelButton.startAnimation(btnClose);
        stopTravelButton.setClickable(false);
      }
    });
  }

  //Esconder los Floating Buttons
  private void hideAllFloatingButtons(){
    fabAddElement.startAnimation(rotateBackward);
    toggleFloatingButton(fabInstantTravel, fabClose, false);
    toggleFloatingButton(fabPlanTravel, fabClose, false);
    toggleFloatingButton(fabAddStory, fabClose, false);
    toggleFloatingButton(fabAddMarker, fabClose, false);
    isFabOpen = false;
  }

  // Desplegar los Floating Buttons
  private void showAllFloatingButtons() {
    fabAddElement.startAnimation(rotateForward);
    toggleFloatingButton(fabInstantTravel, fabOpen, true);
    toggleFloatingButton(fabPlanTravel, fabOpen, true);
    toggleFloatingButton(fabAddStory, fabOpen, true);
    toggleFloatingButton(fabAddMarker, fabOpen, true);
    isFabOpen = true;
  }

  //Cambiar el estado de un Floating Button con la animación dada
  private void toggleFloatingButton(FloatingActionButton button, Animation animation, boolean clickable){
    button.startAnimation(animation);
    button.setClickable(clickable);
  }

  //Crear un recorrido "instantáneo" (privado y que se realiza de una vez)
  private void createInstantTravel(){
    Marcador mInicio = new Marcador(myLocation, Marcador.Tipo.INICIO, "Inicio de recorrido.");
    Marcador mFinal = new Marcador(searchPlace.getLatLng(), Marcador.Tipo.FIN, "Fin de recorrido.");
    Recorrido r = new Recorrido(mInicio, mFinal);
    r.setEstado(Recorrido.Estado.EN_CURSO);
    r.agregarParticipante(currentUser);
    currentUser.agregarRecorrido(r);
    travelStarted = true;
    stopTravelButton.startAnimation(btnOpen);
    stopTravelButton.setClickable(true);
  }

  //Crear un recorrido público (frequente o viaje)
  private void planTravel(){
    FragmentManager fragmentManager = getSupportFragmentManager();
    CreatePublicTravelDialog newFragment = new CreatePublicTravelDialog();
    newFragment.setOnTravelCreatedListener(this);

    //Enviar datos al diálogo
    Bundle bundle = new Bundle();
    bundle.putString("placeName", searchPlace.getName().toString());
    Marcador mInicio = new Marcador(myLocation, Marcador.Tipo.INICIO, "Inicio de recorrido.");
    Marcador mFinal = new Marcador(searchPlace.getLatLng(), Marcador.Tipo.FIN, "Fin de recorrido.");
    bundle.putDouble("mInicioLat", myLocation.latitude);
    bundle.putDouble("mInicioLng", myLocation.longitude);
    bundle.putDouble("mFinalLat", searchPlace.getLatLng().latitude);
    bundle.putDouble("mFinalLng", searchPlace.getLatLng().longitude);
    newFragment.setArguments(bundle);

    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
    transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
  }

  //Crear un marcador
  private void addMarker(){
    CreateMarkerDialog createMarkerDialog = new CreateMarkerDialog(HomeActivity.this, HomeActivity.this);
    createMarkerDialog.show();
  }

  private void members() {
    if (searchPlace != null && travelStarted) {
      Intent intent = new Intent(HomeActivity.this, TravelMembersActivity.class);
      startActivity(intent);
    } else {
      Snackbar.make(this.getCurrentFocus(), "Primero debes buscar un reccorido para poder ver los miembros.", Snackbar.LENGTH_LONG).show();
    }
  }

  private void track() {
    if (searchPlace != null) {
      travelStarted = true;
      Snackbar.make(this.getCurrentFocus(), "Se ha iniciado el viaje. Pedalea seguro :D", Snackbar.LENGTH_LONG).show();
    } else {
      Snackbar.make(this.getCurrentFocus(), "Primero debes buscar un recorrido para poder iniciar el viaje.", Snackbar.LENGTH_LONG).show();
    }
  }

  private void photoHistory() {
    Snackbar.make(this.getCurrentFocus(), "Tomar una foto y subir a historia", Snackbar.LENGTH_LONG).show();
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    this.googleMap = googleMap;
    this.googleMap.setPadding(0, 0, 0, 0);

    //this.googleMap.getUiSettings().setCompassEnabled(true);
    //this.googleMap.getUiSettings().setZoomGesturesEnabled(true);
    //this.googleMap.getUiSettings().setZoomControlsEnabled(true);
    //this.googleMap.getUiSettings().setMapToolbarEnabled(true);

    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      otherPath = extras.getBoolean("draw", false);
    }
    if (otherPath) {
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

    if (Permissions.askPermissionWithJustification(this, Permissions.FINE_LOCATION, getString(R.string.permission_gps))) {
      locationAction();
    }
  }

  //Asociar recorrido al usuario
  @Override
  public void onTravelCreated(Recorrido newTravel) {
    newTravel.agregarParticipante(currentUser);
    currentUser.agregarRecorrido(newTravel);
    fireBaseDatabase.addTravel(newTravel);
    Snackbar.make(getCurrentFocus(), R.string.published_travel, Snackbar.LENGTH_LONG).show();
  }

  //Obtener ubicación actual
  private void locationAction() {
    if (Permissions.checkSelfPermission(this, Permissions.FINE_LOCATION) && googleMap != null) {
      if (locationController.askForGPS()) {
        googleMap.setMyLocationEnabled(true);
        locationController.getMyLocation();
      }
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (!Permissions.permissionGranted(requestCode, permissions, grantResults)) {
      return;
    }
    switch (requestCode) {
      case Permissions.FINE_LOCATION:
        locationAction();
        break;
    }
  }

  //Selección de un destino en la búsqueda de Google Places
  @Override
  public void onPlaceSelected(Place place) {
    this.searchPlace = place;
    otherPath = false;
    locationController.getMyLocation();
  }

  //Creación de un marcador
  public void onMarkerCreated(String type, String description){
    //TODO Crear el marcador
    Snackbar.make(getCurrentFocus(), type + ": " + description, Snackbar.LENGTH_LONG).show();
  }

  @Override
  public void onError(Status status) {
  }

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
    Intent mainIntent = new Intent(this, LoginActivity.class);
    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    startActivity(mainIntent);
    finish();
  }

  @Override
  public void onBackPressed(){
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      finish();
    }
  }

  private void drawMyPoint(LatLng latLng) {
    GoogleMapController.addMarkerAndMove(latLng, "Tu estas aquí", "Pedalea", GoogleMapConstants.ZOOM_STREET, googleMap, R.drawable.bicycle);
    //googleMap.addCircle(new CircleOptions().center(latLng).radius(500).fillColor(Color.argb(100, 109, 184, 242)));
  }
}
