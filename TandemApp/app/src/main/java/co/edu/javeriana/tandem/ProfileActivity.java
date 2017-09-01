package co.edu.javeriana.tandem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageButton;

public class ProfileActivity extends BaseNavigationActivity {

  Button friendsButton, groupButton;
  ImageButton drawerButton;

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

    drawerButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        drawer.openDrawer(GravityCompat.START);
      }
    });
  }

}
