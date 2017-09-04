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

    protected DrawerLayout drawer;
    private Intent profileIntent, friendsActivity, groupsActivity, messagesActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_navigation_layout);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        profileIntent = new Intent(getBaseContext(), ProfileActivity.class);
        friendsActivity = new Intent(getBaseContext(), FriendsActivity.class);
        groupsActivity = new Intent(getBaseContext(), GroupsActivity.class);
        messagesActivity = new Intent(getBaseContext(), MessagesActivity.class);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item){
        int id = item.getItemId();
        drawer.closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {
            case R.id.menu_profile: {
                startActivity(profileIntent);
                break;
            }
            case R.id.menu_friends: {
                startActivity(friendsActivity);
                break;
            }
            case R.id.menu_groups: {
                startActivity(groupsActivity);
                break;
            }case R.id.menu_messages: {
                startActivity(messagesActivity);
                break;
            }
            default:
                return false;
        }

        return true;
    }
}
