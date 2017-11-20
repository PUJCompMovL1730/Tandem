package co.edu.javeriana.tandemsquad.tandem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.ListView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import co.edu.javeriana.tandemsquad.tandem.adapters.UserNotFriendAdapter;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseAuthentication;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseDatabase;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseStorage;
import co.edu.javeriana.tandemsquad.tandem.negocio.Usuario;
import co.edu.javeriana.tandemsquad.tandem.utilities.Utils;

public class AddFriendsActivity extends NavigationActivity implements UserNotFriendAdapter.OnAddFriendButtonPressedListener{

    private FireBaseAuthentication fireBaseAuthentication;
    private FireBaseStorage fireBaseStorage;
    private FireBaseDatabase fireBaseDatabase;

    private ListView usersListView;
    private List<Usuario> usersNotFriendsList;
    private UserNotFriendAdapter userAdapter;
    private Iterator<Usuario> iterator;

    private ProgressDialog dialog;

    private Usuario currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.activity_add_friends);
        View contentView = stub.inflate();

        initComponents();
        setButtonActions();

        fireBaseDatabase = new FireBaseDatabase(this);


        fireBaseAuthentication = new FireBaseAuthentication(this) {
            @Override
            public void onSignInSuccess() {
                setToolbarData(fireBaseAuthentication, fireBaseStorage);
                dialog = ProgressDialog.show(AddFriendsActivity.this, "Cargando usuarios", "Espera un momento por favor...", false, false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        currentUser = fireBaseDatabase.getUser(fireBaseAuthentication.getUser().getUid());
                        if (currentUser != null) {
                            final List<Usuario> usuarios = fireBaseDatabase.getAllUsers();
                            final List<Usuario> friendsList = currentUser.getAmigos();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateUsersAdapter(usuarios, friendsList);
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
                Bitmap image = (Bitmap) Utils.getImageFormUri(AddFriendsActivity.this, uri);
                viewImage.setImageBitmap(image);
            }
        };
    }

    public void updateUsersAdapter(List<Usuario> usuarios, List<Usuario> friendsList) {
        usersNotFriendsList.addAll(usuarios);

        iterator = usersNotFriendsList.iterator();

        while (iterator.hasNext()){
            Usuario usuario = iterator.next();
            for(Usuario friend : friendsList) {
                if(usuario.getId().equals(friend.getId())){
                    iterator.remove();
                }
            }
            if(usuario.getId().equals(currentUser.getId())){
                iterator.remove();
            }
        }

        usersListView.setAdapter(userAdapter);
    }

    @Override
    protected void initComponents() {
        //super.initComponents();

        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_arrow_back);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(R.string.activity_label_add_friends);

        usersListView = (ListView) findViewById(R.id.not_friends_list_view);
        usersNotFriendsList = new ArrayList<>();
        userAdapter = new UserNotFriendAdapter(this, usersNotFriendsList, this);
        usersListView.setAdapter(userAdapter);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAddFriendButtonPressed(Usuario selectedUser) {
        currentUser.agregarAmigo(selectedUser);
        Snackbar.make(getCurrentFocus(), selectedUser.getNombre() + " fue añadido a tu lista de amigos", Snackbar.LENGTH_LONG).show();
        fireBaseDatabase.updateFriendsList(currentUser);
    }
}