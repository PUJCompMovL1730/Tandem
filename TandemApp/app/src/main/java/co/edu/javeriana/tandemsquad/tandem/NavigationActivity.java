package co.edu.javeriana.tandemsquad.tandem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseAuthentication;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseStorage;
import de.hdodenhof.circleimageview.CircleImageView;

public abstract class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected DrawerLayout drawer;
    protected ImageButton drawerAction;
    protected TextView viewName;
    protected TextView viewEmail;
    protected TextView viewUsername;
    protected CircleImageView viewImage;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_navigation);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.navigation_menu);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    protected void initComponents() {
        drawerAction = (ImageButton) findViewById(R.id.toolbar_drawer);
        viewName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_name);
        viewEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_email);
        viewUsername = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_username);
        viewImage = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.nav_image);
    }

    protected void setButtonActions() {
        drawerAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
    }

    protected void setToolbarData(FireBaseAuthentication fireBaseAuthentication, FireBaseStorage fireBaseStorage) {
        if(fireBaseAuthentication.isAnUserSignedIn()) {
            FirebaseUser user = fireBaseAuthentication.getUser();
            viewName.setText(user.getDisplayName());
            viewEmail.setText(user.getEmail());
            if(user.getPhotoUrl() != null) {
                fireBaseStorage.downloadFile(user);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent = null;
        drawer.closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {
            case R.id.navigation_home:
                intent = new Intent(this, HomeActivity.class);
                break;
            case R.id.navigation_profile:
                intent = new Intent(this, ProfileActivity.class);
                break;
            case R.id.navigation_travels:
                intent = new Intent(this, HistorialActivity.class);
                break;
            case R.id.navigation_friends:
                intent = new Intent(this, FriendsActivity.class);
                break;
            case R.id.navigation_messages:
                intent = new Intent(this, ChatsActivity.class);
                break;
            case R.id.navigation_groups:
                intent = new Intent(this, RecorridosActivity.class);
                break;
            case R.id.navigation_settings:
                intent = new Intent(this, ProfileActivity.class);
                break;
            case R.id.navigation_logout:
                logout();
                break;
        }
        if(intent != null) {
            startActivity(intent);
        }
        return true;
    }

    protected abstract void logout();

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }
}