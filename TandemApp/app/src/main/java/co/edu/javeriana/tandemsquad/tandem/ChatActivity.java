package co.edu.javeriana.tandemsquad.tandem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import co.edu.javeriana.tandemsquad.tandem.adapters.ChatAdapter;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseAuthentication;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseDatabase;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseStorage;
import co.edu.javeriana.tandemsquad.tandem.negocio.Mensaje;
import co.edu.javeriana.tandemsquad.tandem.negocio.Usuario;
import co.edu.javeriana.tandemsquad.tandem.utilities.Utils;

public class ChatActivity extends NavigationActivity {
    private FireBaseAuthentication fireBaseAuthentication;
    private FireBaseStorage fireBaseStorage;
    private FireBaseDatabase fireBaseDatabase;

    private ListView chatsContainer;
    private EditText chatInput;
    private Button sendButton;
    private ChatAdapter chatAdapter;
    private List<Mensaje> chats;
    private ValueEventListener eventListener;

    private ProgressDialog dialog;

    private Usuario currentUser;
    private Usuario otherUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.activity_chat);
        View contentView = stub.inflate();

        initComponents();
        setButtonActions();

        dialog = null;

        fireBaseDatabase = new FireBaseDatabase(this);
        final String otherUserId = getIntent().getStringExtra("receiver");

        fireBaseAuthentication = new FireBaseAuthentication(this) {
            @Override
            public void onSignInSuccess() {
                setToolbarData(fireBaseAuthentication, fireBaseStorage);
                if (dialog == null || !dialog.isShowing()) {
                    dialog = ProgressDialog.show(ChatActivity.this, "Cargando mensajes", "Espera un momento por favor...", false, false);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            currentUser = fireBaseDatabase.getUser(fireBaseAuthentication.getUser().getUid());
                            otherUser = fireBaseDatabase.getUser(otherUserId);
                            if (currentUser != null && otherUser != null) {
                                final List<Mensaje> mensajes = fireBaseDatabase.getMessages(currentUser.getId());

                                eventListener = fireBaseDatabase.addMessageListener(currentUser.getId(), new FireBaseDatabase.AsyncEventListener<Mensaje>() {
                                    @Override
                                    public void onActionPerformed(final Mensaje item) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                displayAndAddMessage(item);
                                            }
                                        });
                                    }
                                });

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

    private void loadDummyHistory() {
        chats = new LinkedList<>();

        Mensaje a = new Mensaje("Hi ther", new Usuario("1", "2", "3"), new Usuario("2", "3", "4"), new GregorianCalendar(), true);
        chats.add(a);

        chatAdapter = new ChatAdapter(ChatActivity.this, new LinkedList<Mensaje>());
        chatsContainer.setAdapter(chatAdapter);

        for (int i = 0; i < chats.size(); i++) {
            displayMessage(chats.get(i));
        }
    }

    private void updateAndCheckMessages(List<Mensaje> mensajes) {

        chats = new LinkedList<>();
        for (Mensaje mensaje : mensajes) {
            if (mensaje.getReceptor().getId().equals(currentUser.getId()) || mensaje.getEmisor().getId().equals(currentUser.getId())
                    && (mensaje.getReceptor().getId().equals(otherUser.getId()) || mensaje.getEmisor().getId().equals(otherUser.getId()))) {
                chats.add(mensaje);
            }
        }
        chatAdapter = new ChatAdapter(ChatActivity.this, new LinkedList<Mensaje>());
        chatsContainer.setAdapter(chatAdapter);
        for (int i = 0; i < chats.size(); i++) {
            displayMessage(chats.get(i));
        }
    }

    public void displayAndAddMessage(Mensaje message) {
        chats.add(message);
        displayMessage(message);
    }

    public void displayMessage(Mensaje message) {
        chatAdapter.add(message);
        chatAdapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        chatsContainer.setSelection(chatsContainer.getCount() - 1);
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        if (otherUser != null) {
            getSupportActionBar().setTitle(otherUser.getNombre());
        }
        chatsContainer = (ListView) findViewById(R.id.chats_container);
        chatInput = (EditText) findViewById(R.id.chat_edit);
        sendButton = (Button) findViewById(R.id.chat_send_button);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chatInput.getText().length() > 0) {
                    sendButton.setEnabled(false);
                    fireBaseDatabase.writeMessage(new Mensaje(chatInput.getText().toString(), currentUser, otherUser));
                    chatInput.setText("");
                    sendButton.setEnabled(true);
                }
            }
        });

        loadDummyHistory();
    }

    @Override
    protected void onStart() {
        super.onStart();
        fireBaseAuthentication.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        fireBaseDatabase.removeMessageEventListener(eventListener, currentUser.getId());
        fireBaseAuthentication.stop();
    }

    @Override
    protected void logout() {
        fireBaseAuthentication.signOut();
        Intent mainIntent = new Intent(this, LoginActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
    }
}