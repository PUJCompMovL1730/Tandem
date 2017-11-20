package co.edu.javeriana.tandemsquad.tandem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.ListView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import co.edu.javeriana.tandemsquad.tandem.adapters.FriendAdapter;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseAuthentication;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseDatabase;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseStorage;
import co.edu.javeriana.tandemsquad.tandem.negocio.Usuario;
import co.edu.javeriana.tandemsquad.tandem.utilities.Utils;

public class FriendsActivity extends NavigationActivity {

  private FireBaseAuthentication fireBaseAuthentication;
  private FireBaseStorage fireBaseStorage;
  private FireBaseDatabase fireBaseDatabase;

  private ListView friendsListView;
  private List<Usuario> friendsList;
  private FriendAdapter userAdapter;

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
        if (currentUser != null) {
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

  public void updateFriendsAdapter() {
    friendsList = currentUser.getAmigos();

    if (friendsList != null) {
      userAdapter = new FriendAdapter(this, friendsList);
    } else {
      friendsList = new ArrayList<>();
      userAdapter = new FriendAdapter(this, friendsList);
    }

    friendsListView.setAdapter(userAdapter);
  }

  @Override
  protected void initComponents() {
    super.initComponents();
    getSupportActionBar().setTitle(R.string.activity_label_friends);

    friendsListView = (ListView) findViewById(R.id.friends_list_view);
    friendsList = new ArrayList<>();
    userAdapter = new FriendAdapter(this, friendsList);
    friendsListView.setAdapter(userAdapter);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu){
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.add_friend_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        drawer.openDrawer(GravityCompat.START);
        return true;
      case R.id.menu_settings:
        break;
      case R.id.menu_logout:
        logout();
        break;
      case R.id.menu_add_friend:
        startActivity(new Intent(this, AddFriendsActivity.class));
        break;
    }
    return super.onOptionsItemSelected(item);
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
