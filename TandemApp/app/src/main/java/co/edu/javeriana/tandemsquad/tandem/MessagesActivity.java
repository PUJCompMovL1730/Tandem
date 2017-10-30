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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import co.edu.javeriana.tandemsquad.tandem.adapters.MessageAdapter;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseAuthentication;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseDatabase;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseStorage;
import co.edu.javeriana.tandemsquad.tandem.negocio.Mensaje;
import co.edu.javeriana.tandemsquad.tandem.negocio.Usuario;
import co.edu.javeriana.tandemsquad.tandem.utilities.Utils;

public class MessagesActivity extends NavigationActivity {
    private FireBaseAuthentication fireBaseAuthentication;
    private FireBaseStorage fireBaseStorage;
    private FireBaseDatabase fireBaseDatabase;

    private ListView messagesContainer;
    private List<Mensaje> messages;
    private MessageAdapter messageAdapter;

    private ProgressDialog dialog;

    private Usuario currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapsInitializer.initialize(getApplicationContext());

        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.activity_messages);
        View contentView = stub.inflate();

        initComponents();
        setButtonActions();

        dialog = null;
        fireBaseDatabase = new FireBaseDatabase(this);

        fireBaseAuthentication = new FireBaseAuthentication(this) {
            @Override
            public void onSignInSuccess() {
                setToolbarData(fireBaseAuthentication, fireBaseStorage);

                dialog = ProgressDialog.show(MessagesActivity.this, "Cargando mensajes", "Espera un momento por favor...", false, false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        currentUser = fireBaseDatabase.getUser(fireBaseAuthentication.getUser().getUid());
                        if (currentUser != null) {
                            final List<Mensaje> mensajes = fireBaseDatabase.getMessages(currentUser.getId());

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateAndCheckMessages(mensajes);
                                }
                            });
                        }
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                }).start();
            }
        };

        fireBaseStorage = new FireBaseStorage(this) {
            @Override
            protected void onDownloadFileSuccess(Task<FileDownloadTask.TaskSnapshot> task, File file) {
                Uri uri = Uri.fromFile(file);
                Bitmap image = (Bitmap) Utils.getImageFormUri(MessagesActivity.this, uri);
                viewImage.setImageBitmap(image);
            }
        };
    }

    private void updateAndCheckMessages(List<Mensaje> mensajes) {
        Map<String, Mensaje> filter = new HashMap<>();
        for (Mensaje mensaje : mensajes) {
            if (mensaje.getReceptor().getId().equals(currentUser.getId())) {
                if (!filter.containsKey(mensaje.getEmisor().getId())) {
                    filter.put(mensaje.getEmisor().getId(), mensaje);
                } else {
                    filter.put(mensaje.getEmisor().getId(), mensaje);
                }
            } else {
                if (!filter.containsKey(mensaje.getReceptor().getId())) {
                    filter.put(mensaje.getReceptor().getId(), mensaje);
                } else {
                    filter.put(mensaje.getReceptor().getId(), mensaje);
                }
            }
        }

        messages = new LinkedList<>(filter.values());
        messageAdapter = new MessageAdapter(this, messages);
        messagesContainer.setAdapter(messageAdapter);
    }

    @Override
    protected void initComponents() {
        super.initComponents();

        messagesContainer = (ListView) findViewById(R.id.chats_list_view);
        messages = new LinkedList<>();

        messageAdapter = new MessageAdapter(this, messages);
        messagesContainer.setAdapter(messageAdapter);
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
