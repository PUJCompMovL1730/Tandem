package co.edu.javeriana.tandem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button btn_facebook;
    private Button btn_twitter;
    private Button btn_google;
    private TextView option_email;
    private TextView option_signup;
    private Intent homeIntent;
    private Intent loginIntent;
    private Intent signupIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_facebook = (Button) findViewById(R.id.btn_facebook);
        btn_twitter = (Button) findViewById(R.id.btn_twitter);
        btn_google = (Button) findViewById(R.id.btn_google);
        option_email = (TextView) findViewById(R.id.option_email);
        option_signup = (TextView) findViewById(R.id.option_signup);
        homeIntent = new Intent(getBaseContext(), HomeActivity.class);
        loginIntent = new Intent(getBaseContext(), LoginActivity.class);
        signupIntent = new Intent(getBaseContext(), SignupActivity.class);

        btn_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(homeIntent);
            }
        });

        btn_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(homeIntent);
            }
        });

        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(homeIntent);
            }
        });

        option_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(loginIntent);
            }
        });

        option_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(signupIntent);
            }
        });
    }
}
