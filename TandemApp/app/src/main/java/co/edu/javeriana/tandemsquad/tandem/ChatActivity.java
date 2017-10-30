package co.edu.javeriana.tandemsquad.tandem;

import android.app.ProgressDialog;
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
import java.util.LinkedList;
import java.util.List;

import co.edu.javeriana.tandemsquad.tandem.adapters.MessageAdapter;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseAuthentication;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseDatabase;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseStorage;
import co.edu.javeriana.tandemsquad.tandem.negocio.Mensaje;
import co.edu.javeriana.tandemsquad.tandem.negocio.Usuario;
import co.edu.javeriana.tandemsquad.tandem.utilities.Utils;

public class ChatActivity extends NavigationActivity {
    private FireBaseAuthentication fireBaseAuthentication;
    private FireBaseStorage fireBaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapsInitializer.initialize(getApplicationContext());

        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.activity_chat);
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
                Bitmap image = (Bitmap) Utils.getImageFormUri(ChatActivity.this, uri);
                viewImage.setImageBitmap(image);
            }
        };
    }

    @Override
    protected void initComponents() {
        super.initComponents();
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
