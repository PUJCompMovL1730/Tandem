package co.edu.javeriana.tandemsquad.tandem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
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

import co.edu.javeriana.tandemsquad.tandem.adapters.ChatAdapter;
import co.edu.javeriana.tandemsquad.tandem.adapters.TravelAdapter;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseAuthentication;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseStorage;
import co.edu.javeriana.tandemsquad.tandem.negocio.Chat;
import co.edu.javeriana.tandemsquad.tandem.negocio.Travel;
import co.edu.javeriana.tandemsquad.tandem.utilities.Utils;

public class ChatsActivity extends NavigationActivity {

    private FireBaseAuthentication fireBaseAuthentication;
    private FireBaseStorage fireBaseStorage;

    private ListView chats;
    private List<Chat> listChats;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapsInitializer.initialize(getApplicationContext());

        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.activity_chats);
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
                Bitmap image = (Bitmap) Utils.getImageFormUri(ChatsActivity.this, uri);
                viewImage.setImageBitmap(image);
            }
        };
    }

    @Override
    protected void initComponents() {
        super.initComponents();

        chats = (ListView) findViewById(R.id.chats_list_view);
        listChats= new ArrayList<>();
        chatAdapter = new ChatAdapter(this, listChats);
        chats.setAdapter(chatAdapter);
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
