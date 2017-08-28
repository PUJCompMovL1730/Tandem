package co.edu.javeriana.tandem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button btn_facebook;
    private Button btn_twitter;
    private Button btn_google;
    private TextView option_email;
    private TextView option_signup;
    private Intent homeIntent;
    private Intent loginIntent;
    private Intent signupIntent;
    private ArrayList<String> users;
    private ArrayList<String> passs;

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

        users = new ArrayList<String>();
        users.add("asdf");
        users.add("qwer");
        users.add("zxcv");
        passs = new ArrayList<String>();
        passs.add("asdf");
        passs.add("qwer");
        passs.add("zxcv");

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
                Bundle bund = new Bundle();
                bund.putStringArrayList("users", users);
                bund.putStringArrayList("passs", passs);
                loginIntent.putExtra("bundle", bund);
                startActivity(loginIntent);
            }
        });

        option_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupIntent.putStringArrayListExtra("users", users);
                startActivity(signupIntent);
            }
        });
    }
}
