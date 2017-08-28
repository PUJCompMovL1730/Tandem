package co.edu.javeriana.tandem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ProfileActivity extends AppCompatActivity {

  private Button friendsButton;
  private Button groupButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_profile);
    friendsButton = (Button) findViewById(R.id.friendsButton);
    friendsButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(getBaseContext(), FriendsActivity.class);
        startActivity(intent);
      }
    });
    groupButton = (Button) findViewById(R.id.groupButton);
    groupButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(getBaseContext(), GroupActivity.class);
        startActivity(intent);
      }
    });
  }

}
