package co.edu.javeriana.tandemsquad.tandementerprise.Location;

import android.app.Activity;
import android.content.IntentSender;
import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.security.Permissions;

import co.edu.javeriana.tandemsquad.tandementerprise.Permissions.PermissionsManager;

public class LocationManager
{
    public static final int REQUEST_CHECK_SETTINGS = 4;

    private Activity activity;
    private FusedLocationProviderClient locationProvider;
    private LocationRequest locationRequest;

    public LocationManager( Activity activity )
    {
        this.activity = activity;

        locationProvider = LocationServices.getFusedLocationProviderClient( activity );
        locationRequest = createLocationRequest( 5000, 5000, LocationRequest.PRIORITY_HIGH_ACCURACY );
    }

    private LocationRequest createLocationRequest( long timeToRefresh, long maxTimeToRefresh, int priority )
    {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval( timeToRefresh );
        mLocationRequest.setFastestInterval( maxTimeToRefresh );
        mLocationRequest.setPriority( priority );

        return mLocationRequest;
    }

    public final boolean askForGPS()
    {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        SettingsClient settingsClient = LocationServices.getSettingsClient(activity);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnCompleteListener(activity, new OnCompleteListener<LocationSettingsResponse>()
        {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task)
            {
                if(!task.isSuccessful())
                {
                    Exception exception = task.getException();
                    int statusCode = ((ApiException) exception).getStatusCode();

                    switch(statusCode)
                    {
                        case CommonStatusCodes.RESOLUTION_REQUIRED:
                            try
                            {
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                resolvable.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                            }
                            catch(IntentSender.SendIntentException sendEx)
                            {

                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            }
        });
        return true;
    }

    public final void getMyLocation( )
    {
        if (PermissionsManager.checkSelfPermission(activity, PermissionsManager.FINE_LOCATION)) {
            locationProvider.getLastLocation().addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    onMyLocationRecieved(location);
                }
            });
        }
    }

    protected void onMyLocationRecieved( Location location )
    {

    }
}
