package co.edu.javeriana.tandem;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MessagesActivity extends BaseNavigationActivity {

  ActionBarDrawerToggle actionBar;
  Toolbar toolbar;

  ViewStub stub;
  View contentView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    //Obtener el stub y actualizarlo con el layout requerido
    stub = (ViewStub) findViewById(R.id.layout_stub);
    stub.setLayoutResource(R.layout.activity_messages_content);
    contentView = stub.inflate();

    toolbar = (Toolbar) findViewById(R.id.messages_toolbar);
    getMenuInflater().inflate(R.menu.toolbar_options, toolbar.getMenu());
    toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
    toolbar.setTitle("Mensajes");

    actionBar = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

    LinearLayout chat1 = (LinearLayout) findViewById(R.id.chat1);
    chat1.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent invoker = new Intent(getApplicationContext(), ChatActivity.class);
        invoker.putExtra("partner", "César Alejandro Guayara");
        startActivity(invoker);
      }
    });
    LinearLayout chat2 = (LinearLayout) findViewById(R.id.chat2);
    chat2.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent invoker = new Intent(getApplicationContext(), ChatActivity.class);
        invoker.putExtra("partner", "Sergio Forero Gómez");
        startActivity(invoker);
      }
    });

  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.toolbar_search:
        //TODO Completar
    }

    return true;
  }

}
