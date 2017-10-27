package co.edu.javeriana.tandemsquad.tandem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.ListView;

import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import co.edu.javeriana.tandemsquad.tandem.adapters.TravelAdapter;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseAuthentication;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseStorage;
import co.edu.javeriana.tandemsquad.tandem.negocio.Travel;
import co.edu.javeriana.tandemsquad.tandem.utilities.Utils;

public class TravelsActivity extends NavigationActivity {

    private FireBaseAuthentication fireBaseAuthentication;
    private FireBaseStorage fireBaseStorage;

    private ListView travels;
    private List<Travel> listTravels;
    private TravelAdapter travelAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapsInitializer.initialize(getApplicationContext());

        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.activity_travels);
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
                Bitmap image = (Bitmap) Utils.getImageFormUri(TravelsActivity.this, uri);
                viewImage.setImageBitmap(image);
            }
        };

        Travel t1 = new Travel(4.711000, -74.072094, 4.92311200, -73.89798);
        Travel t2 = new Travel(4.711000, -74.072094, 4.92311200, -73.89798);
        travelAdapter.add(t1);
        travelAdapter.add(t2);
        travelAdapter.add(t1);
        travelAdapter.add(t2);
        travelAdapter.add(t1);
        travelAdapter.add(t2);
        travelAdapter.add(t1);
        travelAdapter.add(t2);
        travelAdapter.add(t1);
        travelAdapter.add(t2);
        travelAdapter.add(t1);
        travelAdapter.add(t2);
        travelAdapter.add(t1);
        travelAdapter.add(t2);
    }

    @Override
    protected void initComponents() {
        super.initComponents();

        travels = (ListView) findViewById(R.id.travels_list_view);
        listTravels = new ArrayList<>();
        travelAdapter = new TravelAdapter(this, listTravels);
        travels.setAdapter(travelAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fireBaseAuthentication.start();
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
