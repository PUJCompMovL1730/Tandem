package co.edu.javeriana.tandem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

public class BaseNavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

  DrawerLayout drawer;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    setContentView(R.layout.base_navigation_layout);

    drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
  }

  @Override
  public boolean onNavigationItemSelected(MenuItem item){
    int id = item.getItemId();
    Intent intent;
    drawer.closeDrawer(GravityCompat.START);
    if(id == R.id.menu_profile){
      intent = new Intent(this, ProfileActivity.class);
      startActivity(intent);
    }

    return true;
  }
}
