package co.edu.javeriana.tandem;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageButton;

public class ProfileActivity extends BaseNavigationActivity {

  ImageButton drawerButton;
  FloatingActionButton editButton;
  Intent editProfileIntent;

  ViewStub stub;
  View contentView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    //Obtener el stub y actualizarlo con el layout requerido
    stub = (ViewStub) findViewById(R.id.layout_stub);
    stub.setLayoutResource(R.layout.activity_profile_content);
    contentView = stub.inflate();

    drawerButton = (ImageButton) findViewById(R.id.sideBarProfile);
    editButton = (FloatingActionButton) findViewById(R.id.edit_profile);

    drawerButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        drawer.openDrawer(GravityCompat.START);
      }
    });
    editButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        editProfileIntent = new Intent(getBaseContext(), EditProfileActivity.class);
        startActivity(editProfileIntent);
      }
    });

  }

}
