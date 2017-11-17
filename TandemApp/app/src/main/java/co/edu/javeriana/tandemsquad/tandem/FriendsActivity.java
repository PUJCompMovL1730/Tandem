package co.edu.javeriana.tandemsquad.tandem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.ListView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import co.edu.javeriana.tandemsquad.tandem.adapters.UserAdapter;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseAuthentication;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseDatabase;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseStorage;
import co.edu.javeriana.tandemsquad.tandem.negocio.Usuario;
import co.edu.javeriana.tandemsquad.tandem.utilities.Utils;

public class FriendsActivity extends NavigationActivity {

    private FireBaseAuthentication fireBaseAuthentication;
    private FireBaseStorage fireBaseStorage;
    private FireBaseDatabase fireBaseDatabase;

    private ListView users;
    private List<Usuario> listUsers;
    private UserAdapter userAdapter;

    private Usuario currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.activity_friends);
        View contentView = stub.inflate();

        initComponents();
        setButtonActions();

        fireBaseDatabase = new FireBaseDatabase(this);

        fireBaseAuthentication = new FireBaseAuthentication(this) {
            @Override
            public void onSignInSuccess() {
                setToolbarData(fireBaseAuthentication, fireBaseStorage);

                currentUser = fireBaseDatabase.getUser(fireBaseAuthentication.getUser().getUid());
                if( currentUser != null )
                {
                    updateFriendsAdapter();
                }
            }
        };

        fireBaseStorage = new FireBaseStorage(this) {
            @Override
            protected void onDownloadFileSuccess(Task<FileDownloadTask.TaskSnapshot> task, File file) {
                Uri uri = Uri.fromFile(file);
                Bitmap image = (Bitmap) Utils.getImageFormUri(FriendsActivity.this, uri);
                viewImage.setImageBitmap(image);
            }
        };
    }

    public void updateFriendsAdapter()
    {
        List<Usuario> amigos = currentUser.getAmigos();

        if( amigos != null )
        {
            userAdapter = new UserAdapter(this, amigos);
        }
        else
        {
            amigos = new ArrayList<>();
            userAdapter = new UserAdapter(this, amigos);
        }

        users.setAdapter(userAdapter);
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        getSupportActionBar().setTitle(R.string.activity_label_friends);

        users = (ListView) findViewById(R.id.friends_list_view);
        listUsers = new ArrayList<>();
        userAdapter = new UserAdapter(this, listUsers);
        users.setAdapter(userAdapter);
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
        Intent mainIntent = new Intent(this, LoginActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
    }
}
